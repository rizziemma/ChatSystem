module ChatSystem {
	requires java.desktop;
	requires java.sql;
	requires javafx.graphics;
	requires javafx.controls;
	requires javafx.fxml;
	requires org.controlsfx.controls;
	exports src.resources;
	exports src.application;
	opens src.application to javafx.graphics;
}