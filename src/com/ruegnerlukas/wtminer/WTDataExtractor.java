package com.ruegnerlukas.wtminer;

import com.ruegnerlukas.simpleutils.logging.LogLevel;
import com.ruegnerlukas.simpleutils.logging.builder.DefaultMessageBuilder;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class WTDataExtractor extends Application {


	public static void main(String[] args) {

		Logger.get().redirectStdOutput(LogLevel.DEBUG, LogLevel.ERROR);
		((DefaultMessageBuilder)Logger.get().getMessageBuilder()).setSourceNameSizeMin(23);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			Logger.get().info("=========================================");
			Logger.get().blankLine();
			Logger.get().blankLine();
			Logger.get().blankLine();
			Logger.get().blankLine();
			Logger.get().close();
		}, "Shutdown-thread"));

		launch(args);
	}




	@Override
	public void start(Stage primaryStage) throws Exception {

		ExtractorController controller = new ExtractorController();

		FXMLLoader loader = new FXMLLoader(WTDataExtractor.class.getResource("layout_extractor.fxml"));
		loader.setController(controller);

		Parent root = loader.load();
		Scene scene = new Scene(root, 600, 240, true);
		primaryStage.setTitle("WT Data Extractor");

		controller.create(primaryStage);

		primaryStage.setScene(scene);
		primaryStage.show();

	}

}
