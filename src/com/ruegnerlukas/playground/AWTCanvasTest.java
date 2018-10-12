package com.ruegnerlukas.playground;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.ruegnerlukas.wtsights.ui.main.MainMenuController;

import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class AWTCanvasTest extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
	
		FXMLLoader loader = new FXMLLoader(MainMenuController.class.getResource("/ui/layout_awtcanvastest.fxml"));
		Parent root = (Parent) loader.load();
		AWTCanvasTest controller = (AWTCanvasTest)loader.getController();
		Scene scene = new Scene(root, 500, 500, true);
		primaryStage.setTitle("AWT Canvas Test");
		primaryStage.setScene(scene);
		
		controller.create();
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				System.exit(0);
			} 
		});
		
		primaryStage.show();
	
	}
	
	
	
	
	
	@FXML private AnchorPane paneCanvas;
	
	
	
	void create() {
		System.out.println("create");
		
		final SwingNode swingNode = new SwingNode();
		createSwingContent(swingNode);
		
		AnchorPane.setLeftAnchor(swingNode, 0.0);
		AnchorPane.setRightAnchor(swingNode, 0.0);
		AnchorPane.setTopAnchor(swingNode, 0.0);
		AnchorPane.setBottomAnchor(swingNode, 0.0);
		paneCanvas.getChildren().add(swingNode);
		
		paneCanvas.setOnScroll(new EventHandler<ScrollEvent>() {
			@Override public void handle(ScrollEvent event) {
				double scale = paneCanvas.getScaleX();
				scale = scale * (event.getDeltaY() > 0 ? 0.9 : 1.0/0.9);
				System.out.println("scale: " + scale + "  " + event.getDeltaY());
				paneCanvas.setScaleX(scale);
				paneCanvas.setScaleY(scale);
			}
		});
		
	}
	
	
	
	
	void createSwingContent(SwingNode swingNode) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override public void run() {
				
				final JPanel panel = new JPanel() {
					private static final long serialVersionUID = 1L;
					@Override public void paintComponent(Graphics g) {
						super.paintComponent(g);
						g.setColor(Color.WHITE);
						g.fillRect(0, 0, getWidth(), getHeight());
						g.setColor(Color.BLACK);
						g.drawString("Hello World", 60, 60);
					}
				};
				
				panel.addComponentListener(new ComponentAdapter() {
					@Override public void componentResized(ComponentEvent e) {
						panel.repaint();
					}
				});
				panel.repaint();
				
				swingNode.setContent(panel);
			}
		});
	}
	
	
	
	
	
}















