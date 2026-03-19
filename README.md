# POS Demo

A desktop Point-of-Sale (POS) system built with JavaFX 21. This application demonstrates a fully functional retail management UI with modules for sales, customers, products, orders, and reporting.

## Features

- **Dashboard** – Overview of business metrics including total sales, product counts, customer counts, a weekly sales trend line chart, and per-category bar charts.
- **Sales** – Record and browse sales transactions (product, customer, amount).
- **Customers** – Manage customer records (name and phone number).
- **Products** – Manage product inventory (name and price).
- **Orders** – View and track orders.
- **Reports** – Dedicated analytics and reporting view.

## Screenshots

The application opens to a 1100 × 700 window with:
- A top navigation bar showing the application title and a **Logout** button.
- A left sidebar for navigating between modules (Dashboard, Customers, Orders, Sales, Products, Reports).
- A central content area that updates based on the selected module.

## Prerequisites

| Requirement | Version |
|-------------|---------|
| Java JDK    | 21+     |
| Maven       | 3.6+    |

> Maven Wrapper files (`mvnw` / `mvnw.cmd`) are included, so a separate Maven installation is optional.

## Getting Started

### Clone the repository

```bash
git clone https://github.com/KipCollo/pos_demo.git
cd pos_demo
```

### Run the application

```bash
# Using the Maven Wrapper (Linux / macOS)
./mvnw clean javafx:run

# Using the Maven Wrapper (Windows)
mvnw.cmd clean javafx:run

# Using a locally installed Maven
mvn clean javafx:run
```

### Build the project

```bash
./mvnw clean package
```

### Run tests

```bash
./mvnw test
```

## Project Structure

```
pos_demo/
├── pom.xml                          # Maven project configuration
├── mvnw / mvnw.cmd                  # Maven wrapper scripts
└── src/
    └── main/
        ├── java/
        │   ├── module-info.java     # Java module descriptor
        │   └── com/kipcollo/demo/
        │       ├── Launcher.java          # Application entry point
        │       ├── HelloApplication.java  # JavaFX Application subclass
        │       ├── HelloController.java   # FXML controller (starter template)
        │       ├── POSHomePage.java       # Main POS window & navigation
        │       ├── Dashboard.java         # Dashboard view with charts
        │       ├── Sales.java             # Sales management module
        │       ├── Customers.java         # Customer management module
        │       ├── Products.java          # Product management module
        │       ├── Orders.java            # Orders management module
        │       ├── Reports.java           # Reports module
        │       └── SaleRecord.java        # Data model for sale records
        └── resources/
            └── com/kipcollo/demo/
                └── hello-view.fxml  # FXML UI definition (starter template)
```

## Technology Stack

| Component | Details |
|-----------|---------|
| Language | Java 21 |
| UI Framework | JavaFX 21.0.6 (`javafx-controls`, `javafx-fxml`) |
| Build Tool | Apache Maven 3.13+ |
| Testing | JUnit Jupiter 6.0.1 |
| Module System | Java Platform Module System (JPMS) |

## License

This project is provided as a demo and learning resource. See the repository for licensing details.
