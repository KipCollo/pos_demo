package com.kipcollo.demo;

import javafx.application.Application;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
        // Ensure DB is initialised before showing any UI
        DatabaseManager.getInstance();
        new LoginPage(stage).show();
    }
}

