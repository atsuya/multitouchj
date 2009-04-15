/**
 * Copyright 2009 Atsuya Takagi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package edu.csun.ecs.cs.multitouchj.application.whiteboard.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.EXTTextureRectangle;
import org.lwjgl.opengl.GL11;

import edu.csun.ecs.cs.multitouchj.objectobserver.event.ObjectObserverEvent;
import edu.csun.ecs.cs.multitouchj.ui.control.Canvas;
import edu.csun.ecs.cs.multitouchj.ui.event.ControlEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.ObjectEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.TouchEvent;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Point;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Size;
import edu.csun.ecs.cs.multitouchj.ui.gesture.GestureHandler;
import edu.csun.ecs.cs.multitouchj.ui.graphic.image.Image;
import edu.csun.ecs.cs.multitouchj.ui.graphic.image.ResizableBufferedImage;
import edu.csun.ecs.cs.multitouchj.ui.graphic.image.Texture;

/**
 * @author Atsuya Takagi
 *
 * $Id: InteractiveCanvas.java 85 2009-03-16 07:04:24Z Atsuya Takagi $
 */
public class InteractiveCanvas extends Canvas {
    private static Log log = LogFactory.getLog(InteractiveCanvas.class);
    private Hashtable<Integer, LinkedList<Point>> previousPoints;
    private Hashtable<Integer, Pen> pens;
    private boolean isDirty;
    
    
    public InteractiveCanvas() {
        super();
        
        previousPoints = new Hashtable<Integer, LinkedList<Point>>();
        pens = new Hashtable<Integer, Pen>();
        
        setDirty(true);
    }
    
    public void addPen(int id, Pen pen) {
        setPen(id, pen);
    }
    
    public void removePen(int id) {
        pens.remove(id);
    }
    
    public Pen getPen(int id) {
        return pens.get(id);
    }
    
    public Map<Integer, Pen> getPens() {
        return new Hashtable<Integer, Pen>(pens);
    }
    
    public void setPen(int id, Pen pen) {
        pens.put(id, pen);
    }
    
    public void render() {
        synchronized(resizableBufferedImage) {
            Texture texture = getTexture();
            if(texture == null) {
                try {
                    Image image = getBufferedImageImage(resizableBufferedImage.getBufferedImage());
                    texture = getTextureManager().createTexture(""+this.hashCode(), image, true);
                    setTexture(texture);
                } catch(Exception exception) {
                    log.debug(
                        "Failed to generate texture from ResizableBufferedImage.",
                        exception
                    );
                }
            }
            
            if(isDirty) {
                Image image = getBufferedImageImage(resizableBufferedImage.getBufferedImage());
                ByteBuffer imageData = prepareImage(image);
                
                GL11.glEnable(EXTTextureRectangle.GL_TEXTURE_RECTANGLE_EXT);
                GL11.glBindTexture(EXTTextureRectangle.GL_TEXTURE_RECTANGLE_EXT, texture.getId().intValue());
                GL11.glTexSubImage2D(
                    EXTTextureRectangle.GL_TEXTURE_RECTANGLE_EXT,
                    0,
                    0,
                    0,
                    image.getWidth(),
                    image.getHeight(),
                    (image.hasAlpha()) ? GL11.GL_RGBA : GL11.GL_RGB,
                    GL11.GL_UNSIGNED_BYTE,
                    imageData
                );
                isDirty = false;
            }
        }
        
        super.render();
    }
    
    protected void onControlResized(ControlEvent controlEvent) {
        super.onControlResized(controlEvent);
        
        setDirty(true);
    }
    
    protected void onTouchStarted(TouchEvent touchEvent) {
        super.onTouchStarted(touchEvent);
        
        addPreviousPoints(touchEvent.getObjectObserverEvents());
    }
    
    protected void onTouchMoved(TouchEvent touchEvent) {
        super.onTouchMoved(touchEvent);
        
        addPreviousPoints(touchEvent.getObjectObserverEvents());
        drawPoints();
    }
    
    protected void onTouchEnded(TouchEvent touchEvent) {
        super.onTouchEnded(touchEvent);
        
        clearPreviousPoints();
    }
    
    protected boolean isDirty() {
        return isDirty;
    }
    
    protected void setDirty(boolean isDirty) {
        this.isDirty = isDirty;
    }
    
    protected void drawPoints() {
        synchronized(previousPoints) {
            synchronized(resizableBufferedImage) {
                DisplayMode displayMode =
                    getWindowManager().getDisplayManager().getCurrentDisplayMode();
                BufferedImage bufferedImage = resizableBufferedImage.getBufferedImage();
                
                Graphics2D graphics = bufferedImage.createGraphics();
                for(int id : pens.keySet()) {
                    Pen pen = pens.get(id);
                    LinkedList<Point> points = previousPoints.get(id);
                    if((pen != null) && (points != null) && (points.size() > 1)) {
                        log.debug("id: "+id+", points: "+points.size());
                        
                        Point previousPoint = points.removeFirst();
                        Iterator<Point> iterator = points.iterator();
                        while(iterator.hasNext()) {
                            Point point = iterator.next();
                            if(iterator.hasNext()) {
                                iterator.remove();
                            }
                            
                            graphics.setStroke(pen.getStroke());
                            graphics.setPaint(pen.getPaint());
                            graphics.drawLine(
                                (int)Math.floor(previousPoint.getX()),
                                (displayMode.getHeight() - (int)Math.floor(previousPoint.getY())),
                                (int)Math.floor(point.getX()),
                                (displayMode.getHeight() - (int)Math.floor(point.getY()))
                            );
                            
                            previousPoint = point;
                        }
                        
                        setDirty(true);
                    }
                }
                graphics.dispose();
            }
        }
    }
    
    protected void addPreviousPoints(List<ObjectObserverEvent> objectObserverEvents) {
        synchronized(previousPoints) {
            for(ObjectObserverEvent ooe : objectObserverEvents) {
                LinkedList<Point> points = previousPoints.get(ooe.getId());
                if(points == null) {
                    points = new LinkedList<Point>();
                }
                
                points.add(new Point(ooe.getX(), ooe.getY()));
                previousPoints.put(ooe.getId(), points);
            }
        }
    }
    
    protected void clearPreviousPoints() {
        synchronized(previousPoints) {
            previousPoints.clear();
        }
    }
}
