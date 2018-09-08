package com.ruegnerlukas.wtutils.canvas;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.wtutils.ZoomableScrollPane;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
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
	public ResizableCanvas canvasOverlay;
	
	public boolean cursorVisible = false;
	public Vector2d cursorPosition = new Vector2d();
	
	
	public WTCanvas(AnchorPane parent) {
		this.parent = parent;
		
		this.canvasOverlay = new ResizableCanvas(parent) {
			@Override
			public void onRepaint(GraphicsContext g) {
				onRepaintOverlay(g);
			}
		};
		canvasOverlay.setMouseTransparent(true);
		parent.getChildren().add(canvasOverlay);
		AnchorPane.setLeftAnchor(canvasOverlay, 0.0);
		AnchorPane.setRightAnchor(canvasOverlay, 0.0);
		AnchorPane.setTopAnchor(canvasOverlay, 0.0);
		AnchorPane.setBottomAnchor(canvasOverlay, 0.0);
		canvasOverlay.toFront();

	}
	
	
	
	
	public void rebuildCanvas(Image img) {
		rebuildCanvas((int)img.getWidth(), (int)img.getHeight());
	}
	
	
	public void rebuildCanvas(int width, int height) {
		
		if( canvas != null && width == (int)canvas.getWidth() && height == (int)canvas.getHeight() ) {
			repaint();
			return;
		}

		canvas = new Canvas(width, height);
		
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
		
		paneCanvasControl = new ZoomableScrollPane(canvas) {
			@Override
			public void onZoom() {
				canvasOverlay.repaint();
			};
		};
		AnchorPane.setLeftAnchor(paneCanvasControl, 0.0);
		AnchorPane.setRightAnchor(paneCanvasControl, 0.0);
		AnchorPane.setTopAnchor(paneCanvasControl, 0.0);
		AnchorPane.setBottomAnchor(paneCanvasControl, 0.0);
		parent.getChildren().setAll(canvasOverlay, paneCanvasControl);
		canvasOverlay.toFront();
		
		repaint();
		
	}
	
	
	
	
	
	public void repaint() {
		onRepaint(canvas.getGraphicsContext2D());
		canvasOverlay.repaint();
	}
	
	
	
	public double getWidth() {
		return canvas.getWidth();
	}
	
	
	public double getHeight() {
		return canvas.getHeight();
	}
	
	
	
	
	public Point2D transformToOverlay(double x, double y) {
		return canvasOverlay.sceneToLocal(canvas.localToScene(x, y));
	}
	
	
	
	
	public Point2D transformToCanvas(double x, double y) {
		return canvas.sceneToLocal(canvasOverlay.localToScene(x, y));
	}
	
	
	
	public void onMouseMoved() {}
	public void onMouseDragged() {}
	public void onMousePressed(MouseButton btn) {}
	public void onMouseReleased(MouseButton btn) {}
	public void onKeyReleased(KeyCode code) {}
	public void onRepaint(GraphicsContext g) {};
	public void onRepaintOverlay(GraphicsContext g) {};

	
}
