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
package edu.csun.ecs.cs.multitouchj.ui.control;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.opengl.DisplayMode;

import edu.csun.ecs.cs.multitouchj.objectobserver.event.Event;
import edu.csun.ecs.cs.multitouchj.objectobserver.event.EventListenerManager;
import edu.csun.ecs.cs.multitouchj.objectobserver.event.ObjectObserverEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.ControlEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.ControlListener;
import edu.csun.ecs.cs.multitouchj.ui.event.FloatEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.FloatListener;
import edu.csun.ecs.cs.multitouchj.ui.event.ObjectEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.TouchEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.TouchListener;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Point;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Size;
import edu.csun.ecs.cs.multitouchj.ui.gesture.GestureHandler;
import edu.csun.ecs.cs.multitouchj.ui.gesture.GestureManager;
import edu.csun.ecs.cs.multitouchj.ui.graphic.Renderable;
import edu.csun.ecs.cs.multitouchj.ui.graphic.WindowManager;
import edu.csun.ecs.cs.multitouchj.ui.utility.OpenGlUtility;

/**
 * @author Atsuya Takagi
 *
 * $Id: Control.java 82 2009-03-15 23:07:07Z Atsuya Takagi $
 */
public abstract class Control implements Renderable {
    private static final Class[] EVENTS_INTERESTED = {
        TouchEvent.class,
        FloatEvent.class
    };
    private static Log log = LogFactory.getLog(Control.class);
    private Point center;
    private Size size;
    private Color color;
    private float opacity;
    private boolean visibility;
    private float rotation;
    private WindowManager windowManager;
    private EventListenerManager eventListenerManager;
    private GestureManager gestureManager;
    
    
    public Control() {
        center = new Point();
        size = new Size();
        color = Color.WHITE;
        opacity = 1.0f;
        visibility = true;
        rotation = 0.0f;
        eventListenerManager = new EventListenerManager();
        gestureManager = new GestureManager(this);
        windowManager = WindowManager.getInstance();
        
        initialize();
    }
    
    public void addTouchListener(TouchListener touchListener) {
        eventListenerManager.addEventListener(TouchListener.class, touchListener);
    }
    
    public void removeTouchListener(TouchListener touchListener) {
        eventListenerManager.removeEventListener(TouchListener.class, touchListener);
    }
    
    public void addFloatListener(FloatListener floatListener) {
        eventListenerManager.addEventListener(FloatListener.class, floatListener);
    }
    
    public void removeFloatListener(FloatListener floatListener) {
        eventListenerManager.removeEventListener(FloatListener.class, floatListener);
    }
    
    public void addControlListener(ControlListener controlListener) {
        eventListenerManager.addEventListener(ControlListener.class, controlListener);
    }
    
    public void removeControlListener(ControlListener controlListener) {
        eventListenerManager.removeEventListener(ControlListener.class, controlListener);
    }
    
    public void dispose() {
        removeTouchListener(gestureManager);
        removeFloatListener(gestureManager);
        
        windowManager.unregisterControl(this);
    }
    
    public Point getPosition() {
        return new Point(center);
    }
    
    public Point getOpenGlPosition() {
        DisplayMode displayMode =
            windowManager.getDisplayManager().getCurrentDisplayMode();
        return OpenGlUtility.getOpenGlPosition(
            new Size(displayMode.getWidth(), displayMode.getHeight()),
            center
        );
    }
    
    public void setPosition(Point position) {
        center.set(position);
        
        ControlEvent controlEvent = new ControlEvent(this);
        onControlMoved(controlEvent);
    }
    
    public Point getTopLeftPosition() {
        return new Point(
            (center.getX() - (size.getWidth() / 2.0f)),
            (center.getY() - (size.getHeight() / 2.0f))
        );
    }
    
    public void setTopLeftPosition(Point position) {
        Size size = getSize();
        Point center = new Point(
            (position.getX() + (size.getWidth() / 2.0f)),
            (position.getY() + (size.getHeight() / 2.0f))
        );
        
        setPosition(center);
    }
    
    public Size getSize() {
        return new Size(size);
    }
    
    public void setSize(Size size) {
        this.size.set(size);
        
        ControlEvent controlEvent = new ControlEvent(this);
        onControlResized(controlEvent);
    }
    
    public Color getColor() {
        return color;
    }
    
    public void setColor(Color color) {
        this.color = new Color(color.getRGB());
    }
    
    public float getOpacity() {
        return opacity;
    }
    
    public void setOpacity(float opacity) {
        float value = opacity;
        if(value > 1.0f) {
            value = 1.0f;
        } else if(value < 0.0f) {
            value = 0.0f;
        }
        
        this.opacity = value;
    }
    
    public boolean isVisible() {
        return visibility;
    }
    
    public void setVisible(boolean value) {
        this.visibility = value;
    }
    
    public float getRotation() {
        return rotation;
    }
    
    public void setRotation(float rotation) {
        float value = rotation;
        while(value < 0.0f) {
            value += 360.0f;
        }
        value = (Math.abs(value) % 360.0f);
        
        log.info("Rotation: "+value);
        
        this.rotation = value;
    }
    
    public boolean isWithin(Point position) {
        boolean result = false;
        if(getRotation() == 0.0f) {
            result = isWithinWithoutRotation(position);
        } else {
            result = isWithinWithRotation(position);
        }
        
        return result;
    }
    
    /*
    public boolean wantsEvent(Event event) {
        boolean wants = false;
        
        if(isEventInterested(event)) {
            if(event instanceof ObjectEvent) {
                ObjectEvent objectEvent = (ObjectEvent)event;
                wants = isWithin(objectEvent.getPosition());
            }
        }
        
        return wants;
    }
    */
    
    public void dispatchEvent(Event event) {
        processEvent(event);
    }
    
    public Point getClientPosition(Point point) {
        Point topLeft = getTopLeftPosition();
        return new Point(
            (point.getX() - topLeft.getX()),
            (point.getY() - topLeft.getY())
        );
    }
    
    protected WindowManager getWindowManager() {
        return windowManager;
    }
    
    protected void processEvent(Event event) {
        if(isEventInterested(event)) {
            if(event instanceof ObjectEvent) {
                processObjectEvent((ObjectEvent)event);
            }
        }
    }
    
    protected void processObjectEvent(ObjectEvent objectEvent) {
        ObjectEvent.Status status  = objectEvent.getStatus();
        
        if(objectEvent instanceof TouchEvent) { 
            TouchEvent touchEvent = (TouchEvent)objectEvent;
            if(ObjectEvent.Status.Started.equals(status)) {
                onTouchStarted(touchEvent);
            } else if(ObjectEvent.Status.Moved.equals(status)) {
                onTouchMoved(touchEvent);
            } else if(ObjectEvent.Status.Ended.equals(status)) {
                onTouchEnded(touchEvent);
            }
        } else {
            FloatEvent floatEvent = (FloatEvent)objectEvent;
            if(ObjectEvent.Status.Started.equals(status)) {
                onFloatStarted(floatEvent);
            } else if(ObjectEvent.Status.Moved.equals(status)) {
                onFloatMoved(floatEvent);
            } else if(ObjectEvent.Status.Ended.equals(status)) {
                onFloatEnded(floatEvent);
            }
        }
    }
    
    protected boolean isEventInterested(Event event) {
        boolean result = false;
        for(Class klass : EVENTS_INTERESTED) {
            if(event.getClass().equals(klass)) {
                result = true;
                break;
            }
        }
        
        return result;
    }
    
    protected void onTouchStarted(TouchEvent touchEvent) {
        //log.info("Control.onTouchStarted");
        notifyEventListeners(
            TouchListener.class,
            "touchStarted",
            new Class[]{TouchEvent.class},
            new Object[]{touchEvent}
        );
    }
    
    protected void onTouchMoved(TouchEvent touchEvent) {
        log.info("Control.onTouchMoved");
        notifyEventListeners(
            TouchListener.class,
            "touchMoved",
            new Class[]{TouchEvent.class},
            new Object[]{touchEvent}
        );
    }
    
    protected void onTouchEnded(TouchEvent touchEvent) {
        //log.info("Control.onTouchEnded");
        notifyEventListeners(
            TouchListener.class,
            "touchEnded",
            new Class[]{TouchEvent.class},
            new Object[]{touchEvent}
        );
    }
    
    protected void onFloatStarted(FloatEvent floatEvent) {
        //log.info("Control.onFloatStarted");
        notifyEventListeners(
            FloatListener.class,
            "floatStarted",
            new Class[]{FloatEvent.class},
            new Object[]{floatEvent}
        );
    }
    
    protected void onFloatMoved(FloatEvent floatEvent) {
        //log.info("Control.onFloatMoved");
        notifyEventListeners(
            FloatListener.class,
            "floatMoved",
            new Class[]{FloatEvent.class},
            new Object[]{floatEvent}
        );
    }
    
    protected void onFloatEnded(FloatEvent floatEvent) {
        //log.info("Control.onFloatEnded");
        notifyEventListeners(
            FloatListener.class,
            "floatEnded",
            new Class[]{FloatEvent.class},
            new Object[]{floatEvent}
        );
    }
    
    protected void onControlResized(ControlEvent controlEvent) {
        notifyEventListeners(
            ControlListener.class,
            "controlResized",
            new Class[]{ControlEvent.class},
            new Object[]{controlEvent}
        );
    }
    
    protected void onControlMoved(ControlEvent controlEvent) {
        notifyEventListeners(
            ControlListener.class,
            "controlMoved",
            new Class[]{ControlEvent.class},
            new Object[]{controlEvent}
        );
    }
    
    protected void notifyEventListeners(
        Class klass,
        String methodName,
        Class[] classes,
        Object[] objects
        ) {
        try {
            eventListenerManager.notifyEventListeners(
                klass,
                methodName,
                classes,
                objects
            );
        } catch(Exception exception) {
            log.info("Failed to notify event listeners.", exception);
        }
    }
    
    protected boolean isWithinWithoutRotation(Point position) {
        Size size = getSize();
        Point center = getPosition();
        Point topLeftPosition = new Point(
            (center.getX() - (size.getWidth() / 2.0f)),
            (center.getY() - (size.getHeight() / 2.0f))
        );
        
        boolean result = false;
        if(((position.getX() >= topLeftPosition.getX()) &&
            (position.getX() <= (topLeftPosition.getX() + size.getWidth()))) &&
            ((position.getY() >= topLeftPosition.getY()) &&
            (position.getY() <= (topLeftPosition.getY() + size.getHeight())))) {
            result = true;
        }
        
        return result;
    }
    
    protected boolean isWithinWithRotation(Point position) {
        // topleft, topright, bottomright, bottomleft
        // get points centered at (0, 0)
        Size size = getSize();
        Point[] points = new Point[4];
        points[0] = new Point(
            -1 * (size.getWidth() / 2.0f),
            (size.getHeight() / 2.0f)
        );
        points[1] = new Point(
            (size.getWidth() / 2.0f),
            (size.getHeight() / 2.0f)
        );
        points[2] = new Point(
            (size.getWidth() / 2.0f),
            -1 * (size.getHeight() / 2.0f)
        );
        points[3] = new Point(
            -1 * (size.getWidth() / 2.0f),
            -1 * (size.getHeight() / 2.0f)
        );
        /*
        log.info("Vertices:");
        for(int i = 0; i < points.length; i++) {
            log.info("\t"+points[i].toString());
        }
        */
        
        // rotate at (0, 0)
        double rotationInDegree = getRotation();
        double rotationInRadian = Math.toRadians(rotationInDegree);
        for(Point point : points) {
            double rotatedX =
                (point.getX() * Math.cos(rotationInRadian)) -
                (point.getY() * Math.sin(rotationInRadian));
            double rotatedY =
                (point.getX() * Math.sin(rotationInRadian)) +
                (point.getY() * Math.cos(rotationInRadian));
            point.set((float)rotatedX, (float)rotatedY);
        }
        /*
        log.info("Rotated Vertices:");
        for(int i = 0; i < points.length; i++) {
            log.info("\t"+points[i].toString());
        }
        */
        
        // check to see if position is above or below the slope of sides of
        // this rectangle
        DisplayMode displayMode =
            windowManager.getDisplayManager().getCurrentDisplayMode();
        Point center = getOpenGlPosition();
        Point centeredPosition = OpenGlUtility.getOpenGlPosition(
            new Size(displayMode.getWidth(), displayMode.getHeight()),
            position
        );
        //log.info("Position GL: "+centeredPosition.toString());
        centeredPosition.minus(center);
        //log.info("Center: "+center.toString());
        //log.info("Position: "+position.toString());
        //log.info("Centered: "+centeredPosition.toString());
        
        boolean[] results = new boolean[points.length];
        for(int i = 0; i < points.length; i++) {
            Point target1 = points[i];
            Point target2 = points[((i + 1) % points.length)];
            Point target3 = points[((i + 2) % points.length)];
            
            double targetValue = (centeredPosition.getY() - target1.getY());
            if(target2.getX() != target1.getX()) {
                targetValue = (centeredPosition.getY() - target1.getY()) -
                    ((target2.getY() - target1.getY()) / (target2.getX() - target1.getX())) *
                    (centeredPosition.getX() - target1.getX());
            }
            double testValue = (target3.getY() - target1.getY());
            if(target2.getX() != target1.getX()) {
                testValue = (target3.getY() - target1.getY()) -
                    ((target2.getY() - target1.getY()) / (target2.getX() - target1.getX())) *
                    (target3.getX() - target1.getX());
            }
            //log.info("target: "+targetValue+", test: "+testValue);
            
            results[i] = (targetValue == 0.0);
            if(((targetValue > 0.0) && (testValue > 0.0)) ||
                ((targetValue < 0.0) && (testValue < 0.0))) {
                results[i] = true;
            }
        }
        /*
        log.info("Results:");
        for(int i = 0; i < results.length; i++) {
            log.info("\t"+results[i]);
        }
        */
        
        boolean isWithin = true;
        for(boolean result : results) {
            if(!result) {
                isWithin = false;
                break;
            }
        }
        /*
        float[] results = new float[points.length];
        for(int i = 0; i < points.length; i++) {
            Point target1 = points[i];
            Point target2 = points[((i + 1) % points.length)];
            
            float targetValue = (centeredPosition.getY() - target1.getY());
            if(target2.getX() != target1.getX()) {
                targetValue = (centeredPosition.getY() - target1.getY()) -
                    ((target2.getY() - target1.getY()) / (target2.getX() - target1.getX())) *
                    (centeredPosition.getX() - target1.getX());
            }
            results[i] = targetValue;
        }
        log.info("Results:");
        for(int i = 0; i < results.length; i++) {
            log.info("\t"+results[i]);
        }
        
        boolean isWithin = false;
        for(float result : results) {
            if(result == 0.0f) {
                isWithin = true;
                break;
            }
        }
        if(!isWithin) {
            int numberOfAboves = 0;
            for(float result : results) {
                if(result > 0.0f) {
                    numberOfAboves += 1;
                }
            }
            if(numberOfAboves == 2) {
                isWithin = true;
            }
        }
        */
        
        //log.info("isWithin: "+isWithin);
        
        return isWithin;
    }
    
    protected void initialize() {
        windowManager.registerControl(this);
        
        for(GestureHandler gestureHandler : getGestureHandlers()) {
            gestureManager.addGestureHandler(gestureHandler);
        }
        addFloatListener(gestureManager);
        addTouchListener(gestureManager);
    }
    
    protected List<GestureHandler>getGestureHandlers() {
        LinkedList<GestureHandler> gestureHandlers = new LinkedList<GestureHandler>();
        return gestureHandlers;
    }
}
