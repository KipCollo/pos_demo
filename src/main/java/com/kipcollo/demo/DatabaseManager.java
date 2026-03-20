package com.kipcollo.demo;

import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:pos_hardware.db";
    private static DatabaseManager instance;

    private DatabaseManager() {
        initDatabase();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // ── Schema initialisation ─────────────────────────────────────────────────

    private void initDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id          INTEGER PRIMARY KEY AUTOINCREMENT,
                    username    TEXT UNIQUE NOT NULL,
                    password    TEXT NOT NULL,
                    role        TEXT NOT NULL,
                    display_name TEXT NOT NULL
                )
                """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS products (
                    id          INTEGER PRIMARY KEY AUTOINCREMENT,
                    name        TEXT NOT NULL,
                    category    TEXT NOT NULL,
                    barcode     TEXT UNIQUE NOT NULL,
                    price       REAL NOT NULL,
                    stock       INTEGER NOT NULL DEFAULT 0,
                    description TEXT
                )
                """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS transactions (
                    id              INTEGER PRIMARY KEY AUTOINCREMENT,
                    cashier         TEXT NOT NULL,
                    total           REAL NOT NULL,
                    paid            REAL NOT NULL,
                    change_due      REAL NOT NULL,
                    payment_method  TEXT NOT NULL DEFAULT 'Cash',
                    created_at      TEXT NOT NULL DEFAULT (datetime('now','localtime'))
                )
                """);

            // Migrate existing databases that lack the payment_method column
            try {
                stmt.execute("ALTER TABLE transactions ADD COLUMN payment_method TEXT DEFAULT 'Cash'");
            } catch (SQLException ignored) {
                // Column already exists — safe to ignore
            }

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS transaction_items (
                    id              INTEGER PRIMARY KEY AUTOINCREMENT,
                    transaction_id  INTEGER NOT NULL REFERENCES transactions(id),
                    product_id      INTEGER NOT NULL REFERENCES products(id),
                    product_name    TEXT NOT NULL,
                    quantity        INTEGER NOT NULL,
                    unit_price      REAL NOT NULL,
                    subtotal        REAL NOT NULL
                )
                """);

            seedData(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void seedData(Connection conn) throws SQLException {
        // Seed users only if table is empty
        try (Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM users")) {
            if (rs.getInt(1) == 0) {
                insertUser(conn, "admin",    "admin123",    "admin",    "Admin User");
                insertUser(conn, "cashier1", "cash1pass",   "cashier1", "Cashier One");
                insertUser(conn, "cashier2", "cash2pass",   "cashier2", "Cashier Two");
            }
        }

        // Seed products only if table is empty
        try (Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM products")) {
            if (rs.getInt(1) == 0) {
                // Furniture
                insertProduct(conn, "Wooden Chair",      "Furniture",          "FRN-001", 4500.00, 25, "Solid wood dining chair");
                insertProduct(conn, "Office Desk",       "Furniture",          "FRN-002", 12000.00, 15, "Large L-shaped office desk");
                insertProduct(conn, "Bookshelf",         "Furniture",          "FRN-003", 6800.00, 10, "5-tier wooden bookshelf");
                insertProduct(conn, "Steel Cabinet",     "Furniture",          "FRN-004", 8500.00, 8,  "3-door steel filing cabinet");

                // Construction Tools
                insertProduct(conn, "Hammer",            "Construction Tools", "CTL-001", 850.00,  50, "16 oz claw hammer");
                insertProduct(conn, "Power Drill",       "Construction Tools", "CTL-002", 7500.00, 20, "18V cordless power drill");
                insertProduct(conn, "Circular Saw",      "Construction Tools", "CTL-003", 15000.00, 10, "7¼ inch circular saw");
                insertProduct(conn, "Measuring Tape",    "Construction Tools", "CTL-004", 450.00,  80, "5m steel measuring tape");
                insertProduct(conn, "Spirit Level",      "Construction Tools", "CTL-005", 1200.00, 30, "1.2m aluminium spirit level");

                // Hardware Supplies
                insertProduct(conn, "Assorted Screws",   "Hardware Supplies",  "HWS-001", 350.00, 200, "Box of 100 mixed screws");
                insertProduct(conn, "Nails (1kg)",       "Hardware Supplies",  "HWS-002", 180.00, 150, "1kg round wire nails");
                insertProduct(conn, "Wood Glue",         "Hardware Supplies",  "HWS-003", 520.00,  60, "500ml wood adhesive");
                insertProduct(conn, "Sandpaper Set",     "Hardware Supplies",  "HWS-004", 280.00,  90, "Assorted grit sandpaper (10 pcs)");
                insertProduct(conn, "Steel Bolts",       "Hardware Supplies",  "HWS-005", 220.00, 120, "M8 hex bolts, pack of 20");

                // Electrical
                insertProduct(conn, "Extension Cable",   "Electrical",         "ELC-001", 1800.00, 40, "10m 3-pin extension cable");
                insertProduct(conn, "Circuit Breaker",   "Electrical",         "ELC-002", 2500.00, 25, "32A single-pole MCB");
                insertProduct(conn, "LED Bulb 18W",      "Electrical",         "ELC-003", 420.00, 100, "Energy-saving LED bulb");
                insertProduct(conn, "Cable Trunking",    "Electrical",         "ELC-004", 650.00,  55, "2m white PVC trunking");
                insertProduct(conn, "Wall Socket",       "Electrical",         "ELC-005", 380.00,  70, "13A twin wall socket");

                // Plumbing
                insertProduct(conn, "PVC Pipe 1/2\"",   "Plumbing",           "PLM-001", 250.00, 200, "3m PVC pressure pipe");
                insertProduct(conn, "Gate Valve",        "Plumbing",           "PLM-002", 1100.00, 35, "1-inch brass gate valve");
                insertProduct(conn, "Pipe Wrench",       "Plumbing",           "PLM-003", 1350.00, 20, "14-inch pipe wrench");
                insertProduct(conn, "Teflon Tape",       "Plumbing",           "PLM-004", 85.00,  150, "PTFE thread seal tape");
                insertProduct(conn, "Elbow Fitting",     "Plumbing",           "PLM-005", 120.00, 180, "1/2\" PVC elbow connector");
            }
        }

        // Seed sample transactions only if table is empty
        try (Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM transactions")) {
            if (rs.getInt(1) == 0) {
                seedTransactions(conn);
            }
        }
    }

    private void insertUser(Connection conn, String username, String password,
                            String role, String displayName) throws SQLException {
        String sql = "INSERT INTO users (username, password, role, display_name) VALUES (?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, hashPassword(password));
            ps.setString(3, role);
            ps.setString(4, displayName);
            ps.executeUpdate();
        }
    }

    private void insertProduct(Connection conn, String name, String category, String barcode,
                               double price, int stock, String description) throws SQLException {
        String sql = "INSERT INTO products (name, category, barcode, price, stock, description) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, category);
            ps.setString(3, barcode);
            ps.setDouble(4, price);
            ps.setInt(5, stock);
            ps.setString(6, description);
            ps.executeUpdate();
        }
    }

    private void seedTransactions(Connection conn) throws SQLException {
        long[] txIds = {
            insertTransaction(conn, "cashier1", 5700.00,  6000.00,  300.00,  "Cash"),
            insertTransaction(conn, "cashier2", 22350.00, 25000.00, 2650.00, "M-Pesa"),
            insertTransaction(conn, "admin",    1300.00,  1500.00,  200.00,  "Cash"),
            insertTransaction(conn, "cashier1", 8000.00,  8000.00,  0.00,    "Card"),
            insertTransaction(conn, "cashier2", 3620.00,  4000.00,  380.00,  "Cash")
        };

        insertTransactionItem(conn, txIds[0], 1, "Wooden Chair", 1, 4500.00);
        insertTransactionItem(conn, txIds[0], 5, "Hammer",       1, 850.00);
        insertTransactionItem(conn, txIds[0], 11,"Nails (1kg)",  2, 180.00);

        insertTransactionItem(conn, txIds[1], 2, "Office Desk",  1, 12000.00);
        insertTransactionItem(conn, txIds[1], 6, "Power Drill",  1, 7500.00);
        insertTransactionItem(conn, txIds[1], 15,"Extension Cable", 1, 1800.00);
        insertTransactionItem(conn, txIds[1], 20,"PVC Pipe 1/2\"", 1, 250.00);
        insertTransactionItem(conn, txIds[1], 24,"Elbow Fitting",  5, 120.00 );

        insertTransactionItem(conn, txIds[2], 10,"Assorted Screws", 2, 350.00);
        insertTransactionItem(conn, txIds[2], 17,"LED Bulb 18W",   1, 420.00);
        insertTransactionItem(conn, txIds[2], 24,"Teflon Tape",    2, 85.00);

        insertTransactionItem(conn, txIds[3], 7, "Circular Saw",   1, 15000.00);
        insertTransactionItem(conn, txIds[3], 4, "Steel Cabinet",  1, 8500.00);

        insertTransactionItem(conn, txIds[4], 8, "Measuring Tape", 2, 450.00);
        insertTransactionItem(conn, txIds[4], 17,"LED Bulb 18W",   4, 420.00);
        insertTransactionItem(conn, txIds[4], 23,"Teflon Tape",    5, 85.00);
    }

    private long insertTransaction(Connection conn, String cashier, double total,
                                   double paid, double changeDue, String paymentMethod) throws SQLException {
        String sql = "INSERT INTO transactions (cashier, total, paid, change_due, payment_method) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cashier);
            ps.setDouble(2, total);
            ps.setDouble(3, paid);
            ps.setDouble(4, changeDue);
            ps.setString(5, paymentMethod);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.getLong(1);
            }
        }
    }

    private void insertTransactionItem(Connection conn, long txId, int productId,
                                       String productName, int qty, double unitPrice) throws SQLException {
        String sql = "INSERT INTO transaction_items (transaction_id, product_id, product_name, quantity, unit_price, subtotal) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, txId);
            ps.setInt(2, productId);
            ps.setString(3, productName);
            ps.setInt(4, qty);
            ps.setDouble(5, unitPrice);
            ps.setDouble(6, qty * unitPrice);
            ps.executeUpdate();
        }
    }

    // ── User Operations ───────────────────────────────────────────────────────

    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username=? AND password=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, hashPassword(password));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("username"),
                            rs.getString("password"), rs.getString("role"),
                            rs.getString("display_name"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users ORDER BY id")) {
            while (rs.next()) {
                list.add(new User(rs.getInt("id"), rs.getString("username"),
                        rs.getString("password"), rs.getString("role"),
                        rs.getString("display_name")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addUser(String username, String password, String role, String displayName) {
        String sql = "INSERT INTO users (username, password, role, display_name) VALUES (?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, hashPassword(password));
            ps.setString(3, role);
            ps.setString(4, displayName);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteUser(int id) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ── Product Operations ────────────────────────────────────────────────────

    public List<Product> getAllProducts() {
        return getProducts("SELECT * FROM products ORDER BY category, name");
    }

    public List<Product> getProductsByCategory(String category) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE category=? ORDER BY name";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapProduct(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Product> searchProducts(String query) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE LOWER(name) LIKE ? OR LOWER(category) LIKE ? OR barcode LIKE ? ORDER BY category, name";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String q = "%" + query.toLowerCase() + "%";
            ps.setString(1, q);
            ps.setString(2, q);
            ps.setString(3, q);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapProduct(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Product findByBarcode(String barcode) {
        String sql = "SELECT * FROM products WHERE barcode=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, barcode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapProduct(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean addProduct(String name, String category, String barcode,
                              double price, int stock, String description) {
        String sql = "INSERT INTO products (name, category, barcode, price, stock, description) VALUES (?,?,?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, category);
            ps.setString(3, barcode);
            ps.setDouble(4, price);
            ps.setInt(5, stock);
            ps.setString(6, description);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateProduct(int id, String name, String category, String barcode,
                                 double price, int stock, String description) {
        String sql = "UPDATE products SET name=?, category=?, barcode=?, price=?, stock=?, description=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, category);
            ps.setString(3, barcode);
            ps.setDouble(4, price);
            ps.setInt(5, stock);
            ps.setString(6, description);
            ps.setInt(7, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteProduct(int id) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM products WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean reduceStock(int productId, int qty) {
        String sql = "UPDATE products SET stock = stock - ? WHERE id = ? AND stock >= ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, qty);
            ps.setInt(2, productId);
            ps.setInt(3, qty);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ── Transaction Operations ────────────────────────────────────────────────

    public long saveTransaction(String cashier, double total, double paid,
                                double changeDue, String paymentMethod, List<CartItem> items) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                long txId = insertTransaction(conn, cashier, total, paid, changeDue, paymentMethod);
                for (CartItem item : items) {
                    insertTransactionItem(conn, txId, item.getProduct().getId(),
                            item.getName(), item.getQuantity(), item.getUnitPrice());
                    reduceStock(item.getProduct().getId(), item.getQuantity());
                }
                conn.commit();
                return txId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public List<SaleRecord> getRecentTransactions(int limit) {
        List<SaleRecord> list = new ArrayList<>();
        String sql = "SELECT cashier, total, created_at FROM transactions ORDER BY id DESC LIMIT ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new SaleRecord(
                            rs.getString("created_at"),
                            rs.getString("cashier"),
                            rs.getDouble("total")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public double getTotalRevenue() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COALESCE(SUM(total),0) FROM transactions")) {
            return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); return 0; }
    }

    public int getTotalTransactions() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM transactions")) {
            return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); return 0; }
    }

    public int getTotalProducts() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM products")) {
            return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); return 0; }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Hashes a password with SHA-256 for secure storage. */
    static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    private List<Product> getProducts(String sql) {
        List<Product> list = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapProduct(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private Product mapProduct(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getString("barcode"),
                rs.getDouble("price"),
                rs.getInt("stock"),
                rs.getString("description"));
    }
}
