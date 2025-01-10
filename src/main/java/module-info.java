module projet.java.reconnaissancefaciale1 {
        requires javafx.controls;
        requires javafx.fxml;
        requires de.jensd.fx.glyphs.fontawesome;
        requires de.jensd.fx.glyphs.commons;
        requires opencv;
        requires java.sql;
    requires java.desktop;
    requires org.json;

    opens projet.java.reconnaissancefaciale1.dao.entities to javafx.base;
        opens projet.java.reconnaissancefaciale1 to javafx.fxml;
        exports projet.java.reconnaissancefaciale1;
        exports projet.java.reconnaissancefaciale1.controlleur;
        opens projet.java.reconnaissancefaciale1.controlleur to javafx.fxml;
        }
