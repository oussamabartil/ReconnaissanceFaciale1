module bartil.oussama.reconnaissancefaciale1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing; // Required for SwingFXUtils
    requires de.jensd.fx.glyphs.fontawesome;
    requires de.jensd.fx.glyphs.commons;
    requires opencv;
    requires java.sql;
    requires java.desktop;

    // Open specific packages for JavaFX reflection
    opens bartil.oussama.reconnaissancefaciale1.dao.entities to javafx.base;
    opens bartil.oussama.reconnaissancefaciale1 to javafx.fxml;
    opens bartil.oussama.reconnaissancefaciale1.controlleur to javafx.fxml;

    // Export specific packages for external usage
    exports bartil.oussama.reconnaissancefaciale1;
    exports bartil.oussama.reconnaissancefaciale1.controlleur;
}
