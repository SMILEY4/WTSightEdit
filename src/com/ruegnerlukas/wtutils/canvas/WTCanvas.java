package com.ruegnerlukas.wtutils.canvas;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.simplemath.vectors.vec2.Vector2i;
import com.ruegnerlukas.wtutils.ZoomableScrollPane;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class WTCanvas {

	public AnchorPane parent;
	public ZoomableScrollPane paneCanvasControl;
	public Canvas canvas;
	
	public boolean cursorVisible = false;
	public Vector2d cursorPosition = new Vector2d();
	
	
	public WTCanvas(AnchorPane parent) {
		this.parent = parent;
	}
	
	
	
	
	
	public void rebuildCanvas(Image img) {
		
		canvas = new Canvas(img.getWidth(), img.getHeight());
		
		canvas.setOnMouseMoved(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				event.consume();
				cursorVisible = true;
				cursorPosition.set(event.getX(), event.getY());
				onMouseMoved();
			}
		});
		canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.getButton() != MouseButton.SECONDARY) {
					event.consume();
					cursorVisible = true;
					cursorPosition.set(event.getX(), event.getY());
					onMouseDragged();
				} else {
					repaint();
				}
			}
		});
		canvas.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				event.consume();
				cursorVisible = false;
			}
		});
		canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.getButton() != MouseButton.SECONDARY) {
					event.consume();
					cursorVisible = true;
					onMousePressed(event.getButton());
				} else {
					repaint();
				}
			}
		});
		canvas.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.getButton() != MouseButton.SECONDARY) {
					event.consume();
					cursorVisible = true;
					onMouseReleased(event.getButton());
				} else {
					repaint();
				}
			}
		});
	
		parent.getScene().setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				onKeyReleased(event.getCode());
			}
		});
		
		paneCanvasControl = new ZoomableScrollPane(canvas);
		AnchorPane.setLeftAnchor(paneCanvasControl, 0.0);
		AnchorPane.setRightAnchor(paneCanvasControl, 0.0);
		AnchorPane.setTopAnchor(paneCanvasControl, 0.0);
		AnchorPane.setBottomAnchor(paneCanvasControl, 0.0);
		parent.getChildren().setAll(paneCanvasControl);
		
		repaint();
		
	}
	
	
	
	
	
	public void repaint() {
		GraphicsContext g = canvas.getGraphicsContext2D();
		onRepaint(g);
	}
	
	
	
	public double getWidth() {
		return canvas.getWidth();
	}
	
	
	public double getHeight() {
		return canvas.getHeight();
	}
	
	
	public void onMouseMoved() {}
	public void onMouseDragged() {}
	public void onMousePressed(MouseButton btn) {}
	public void onMouseReleased(MouseButton btn) {}
	public void onKeyReleased(KeyCode code) {}
	public void onRepaint(GraphicsContext g) {};
	
	
}
