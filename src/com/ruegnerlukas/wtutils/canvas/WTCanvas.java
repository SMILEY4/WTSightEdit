package com.ruegnerlukas.wtutils.canvas;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.PatternSyntaxException;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.ruegnerlukas.simplemath.geometry.shapes.rectangle.Rectanglef;
import com.ruegnerlukas.simplemath.vectors.vec2.Vector2i;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingNode;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;

public class WTCanvas {

	private ZoomableScrollPane scrollPane;
	private Pane paneDummy;
	private SwingNode nodeCanvas;
	private JPanel canvas;
	
	private final Rectanglef bounds = new Rectanglef(0, 0, 0, 0);
	private boolean boundsAllVisible = false;
	
	private BufferedImage rendertarget;
	private Pinboard pinboard;
	
	private boolean cursorVisible = false;
	private Vector2i cursorPosition = new Vector2i();
	
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public WTCanvas(AnchorPane paneCanvas, int width, int height) {
		
		// create dummy pane
		paneDummy = new Pane();
		paneDummy.setMinSize(width, height);
		paneDummy.setPrefSize(width, height);
		paneDummy.setMaxSize(width, height);
		
		paneDummy.setOnMouseMoved(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent event) {
				event.consume();
				cursorVisible = true;
				cursorPosition.set(event.getX(), event.getY());
				onMouseMoved();
			}
		});
		paneDummy.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent event) {
				if(event.getButton() != MouseButton.SECONDARY) {
					event.consume();
					cursorVisible = true;
					cursorPosition.set(event.getX(), event.getY());
					onMouseDragged();
				}
			}
		});
		paneDummy.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				event.consume();
				cursorVisible = false;
				onMouseMoved();
			}
		});
		paneDummy.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.getButton() != MouseButton.SECONDARY) {
					event.consume();
					cursorVisible = true;
					cursorPosition.set(event.getX(), event.getY());
					onMousePressed(event.getButton());
				}
			}
		});
		paneDummy.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.getButton() != MouseButton.SECONDARY) {
					event.consume();
					cursorVisible = true;
					cursorPosition.set(event.getX(), event.getY());
					onMouseReleased(event.getButton());
				}
			}
		});
		
		// add dummy to scrollpane
		scrollPane = new ZoomableScrollPane(paneDummy) {
			@Override public void zoomEvent() {
				repaint();
			}
		};
		scrollPane.vvalueProperty().addListener(new ChangeListener() {
			@Override public void changed(ObservableValue observable, Object oldValue, Object newValue) {
				repaint();
			}
		});
		scrollPane.hvalueProperty().addListener(new ChangeListener() {
			@Override public void changed(ObservableValue observable, Object oldValue, Object newValue) {
				repaint();
			}
		});
		scrollPane.widthProperty().addListener(new ChangeListener() {
			@Override public void changed(ObservableValue observable, Object oldValue, Object newValue) {
				repaint();
			}
		});
		scrollPane.heightProperty().addListener(new ChangeListener() {
			@Override public void changed(ObservableValue observable, Object oldValue, Object newValue) {
				repaint();
			}
		});
		paneCanvas.getChildren().add(scrollPane);
		AnchorPane.setLeftAnchor(scrollPane, 0.0);
		AnchorPane.setRightAnchor(scrollPane, 0.0);
		AnchorPane.setTopAnchor(scrollPane, 0.0);
		AnchorPane.setBottomAnchor(scrollPane, 0.0);
		
		// create awt canvas
		nodeCanvas = new SwingNode();
		try {	
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					canvas = new JPanel() {
						@Override
						public void paintComponent(Graphics g) {
							super.paintComponent(g);
							((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
							draw((Graphics2D)g);
						}
					};
					canvas.addComponentListener(new ComponentAdapter() {
						@Override
						public void componentResized(ComponentEvent e) {
							canvas.repaint();
						}
					});
					canvas.repaint();
					nodeCanvas.setContent(canvas);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			Logger.get().error("Error when creating AWTCanvas" + e);
		}
		nodeCanvas.setMouseTransparent(true);
		paneCanvas.getChildren().add(nodeCanvas);
		AnchorPane.setLeftAnchor(nodeCanvas, 0.0);
		AnchorPane.setRightAnchor(nodeCanvas, 18.0);
		AnchorPane.setTopAnchor(nodeCanvas, 0.0);
		AnchorPane.setBottomAnchor(nodeCanvas, 18.0);
		nodeCanvas.toFront();
		
		// create pinboard
		pinboard = new Pinboard();
		
		// create rendertargets
		resizeRendertarget(width, height);
	}
	
	
	
	
	public void resizeRendertarget(int width, int height) {
		
		if( rendertarget != null && width == rendertarget.getWidth() && height == rendertarget.getHeight() ) {
			repaint();
			
		} else {
			rendertarget = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			paneDummy.setMinSize(width, height);
			paneDummy.setPrefSize(width, height);
			paneDummy.setMaxSize(width, height);
			repaint();
		}
	}
	
	
	
	
	public void repaint() {
		
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override public void run() {
					try {
						
						calculateBounds();
						canvas.repaint();
						
					} catch(Exception e) {
						Logger.get().error(e);
					}
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
		
//		SwingUtilities.invokeLater(new Runnable() {
//			@Override public void run() {
//				try {
//					
//					calculateBounds();
//					canvas.repaint();
//					
//				} catch(Exception e) {
//					Logger.get().error(e);
//				}
//			}
//		});
		
	}
	
	
	
	
	private void draw(Graphics2D g) {
		
		if(boundsAllVisible) {
			final double scaleContent = paneDummy.getScaleX();
			final int width = (int)(rendertarget.getWidth() * scaleContent);
			final int height = (int)(rendertarget.getHeight() * scaleContent);
			final int x = (canvas.getWidth()-width) / 2;
			final int y = (canvas.getHeight()-height) / 2;
			g.drawImage(rendertarget, x, y, width, height, null);
		    
		} else {
			g.drawImage(
					rendertarget,
					0,
					0,
					canvas.getWidth(),
					canvas.getHeight(),
					bounds.getXInt(),
					bounds.getYInt(),
					bounds.getXInt()+bounds.getWidthInt(),
					bounds.getYInt()+bounds.getHeightInt(),
					null
				);
		}
		
		pinboard.draw(g, this, paneDummy, nodeCanvas);
		
	}
	
	
	
	
	public void calculateBounds() {
		
		final double hvalue = scrollPane.getHvalue();
		final double vvalue = scrollPane.getVvalue();
		final double widthPane = scrollPane.getWidth()-18;
		final double heightPane = scrollPane.getHeight()-18;
		final double widthDummy = paneDummy.getWidth();
		final double heightDummy = paneDummy.getHeight();
		final double scaleContent = paneDummy.getScaleX();
		final double widthContent = widthDummy * scaleContent;
		final double heightContent = heightDummy * scaleContent;
		
		
		double dx = (widthContent - widthPane) / scaleContent;
		double dy = (heightContent - heightPane) / scaleContent;
		
		double x = dx * hvalue;
		double y = dy * vvalue;
		
		double width = widthPane / scaleContent;
		double height = heightPane / scaleContent;

		bounds.set(x, y, width, height);
		
		if(widthContent < widthPane && heightContent < heightPane) {
			boundsAllVisible = true;
		} else {
			boundsAllVisible = false;
		}
		
	}




	public Graphics2D getGraphics() {
		return (Graphics2D)this.rendertarget.getGraphics();
	}

	
	
	
	public int getWidth() {
		return rendertarget.getWidth();
	}
	
	
	
	
	public int getHeight() {
		return rendertarget.getHeight();
	}
	
	
	
	
	public int getNodeWidth() {
		return canvas.getWidth();
	}
	
	
	
	
	public int getNodeHeight() {
		return canvas.getHeight();
	}
	
	
	
	
	public int getCursorX() {
		return this.cursorPosition.x;
	}
	
	
	
	
	public int getCursorY() {
		return this.cursorPosition.y;
	}


	
	
	public Vector2i getCursorPosition() {
		return this.cursorPosition;
	}
	
	
	
	
	public boolean isCursorVisible() {
		return this.cursorVisible;
	}
	
	
	
	
	public Pinboard getPinboard() {
		return this.pinboard;
	}
	
	
	
	
	public void onMouseMoved() {};
	public void onMouseDragged() {};
	public void onMousePressed(MouseButton btn) {};
	public void onMouseReleased(MouseButton btn) {};

	
	
}








