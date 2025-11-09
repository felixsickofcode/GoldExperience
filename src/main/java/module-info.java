module vnu.uet.goldexperience {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires javafx.graphics;
    requires com.google.gson;
    requires javafx.media;
    requires java.sql;


    opens vnu.uet.goldexperience.model to com.google.gson;

    opens vnu.uet.goldexperience.controller to javafx.fxml;
    exports vnu.uet.goldexperience;
    exports vnu.uet.goldexperience.manager;
    exports vnu.uet.goldexperience.model;
    exports vnu.uet.goldexperience.core;
    opens vnu.uet.goldexperience to javafx.fxml;
    opens vnu.uet.goldexperience.manager to com.google.gson, javafx.fxml;
    exports vnu.uet.goldexperience.effect;
    opens vnu.uet.goldexperience.effect to com.google.gson, javafx.fxml;
    exports vnu.uet.goldexperience.effect.ball;
    opens vnu.uet.goldexperience.effect.ball to com.google.gson, javafx.fxml;
    exports vnu.uet.goldexperience.effect.paddle;
    opens vnu.uet.goldexperience.effect.paddle to com.google.gson, javafx.fxml;
    exports vnu.uet.goldexperience.effect.brick;
    opens vnu.uet.goldexperience.effect.brick to com.google.gson, javafx.fxml;
    exports vnu.uet.goldexperience.model.brick;
    opens vnu.uet.goldexperience.model.brick to com.google.gson;
    exports vnu.uet.goldexperience.model.brickFactory;
    opens vnu.uet.goldexperience.model.brickFactory to com.google.gson;

    exports vnu.uet.goldexperience.view;
    opens vnu.uet.goldexperience.view to javafx.fxml;
    exports vnu.uet.goldexperience.database;
}