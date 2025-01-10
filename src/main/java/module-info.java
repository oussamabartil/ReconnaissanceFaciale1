module bartil.oussama.reconnaissancefaciale1 {
        requires javafx.controls;
        requires javafx.fxml;
        requires de.jensd.fx.glyphs.fontawesome;
        requires de.jensd.fx.glyphs.commons;
        requires opencv;
        requires java.sql;
    requires java.desktop;
    requires org.json;

    opens bartil.oussama.reconnaissancefaciale1.dao.entities to javafx.base;
        opens bartil.oussama.reconnaissancefaciale1 to javafx.fxml;
        exports bartil.oussama.reconnaissancefaciale1;
        exports bartil.oussama.reconnaissancefaciale1.controlleur;
        opens bartil.oussama.reconnaissancefaciale1.controlleur to javafx.fxml;
        }
