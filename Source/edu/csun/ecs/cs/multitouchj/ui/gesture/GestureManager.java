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
package edu.csun.ecs.cs.multitouchj.ui.gesture;

import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.csun.ecs.cs.multitouchj.ui.control.Control;
import edu.csun.ecs.cs.multitouchj.ui.event.FloatEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.FloatListener;
import edu.csun.ecs.cs.multitouchj.ui.event.TouchEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.TouchListener;

/**
 * @author Atsuya Takagi
 *
 * $Id: GestureManager.java 74 2009-02-23 09:39:07Z Atsuya Takagi $
 */
public class GestureManager implements TouchListener, FloatListener {
    private static final int MAXIMUM_EVENTS = 10;
    private static Log log = LogFactory.getLog(GestureManager.class);
    private LinkedList<GestureHandler> gestureHandlers;
    private LinkedList<TouchEvent> touchEvents;
    private LinkedList<FloatEvent> floatEvents;
    private Control control;
    
    
    public GestureManager(Control control) {
        this.control = control;
        gestureHandlers = new LinkedList<GestureHandler>();
        touchEvents = new LinkedList<TouchEvent>();
        floatEvents = new LinkedList<FloatEvent>();
    }
    
    /* (non-Javadoc)
     * @see edu.csun.ecs.cs.multitouchj.ui.event.TouchListener#touchEnded(edu.csun.ecs.cs.multitouchj.ui.event.TouchEvent)
     */
    public void touchEnded(TouchEvent touchEvent) {
        addTouchEvent(touchEvent);
        handleTouchEnded();
        
        removeTouchEvents();
    }

    /* (non-Javadoc)
     * @see edu.csun.ecs.cs.multitouchj.ui.event.TouchListener#touchMoved(edu.csun.ecs.cs.multitouchj.ui.event.TouchEvent)
     */
    public void touchMoved(TouchEvent touchEvent) {
        addTouchEvent(touchEvent);
        handleTouchMoved();
    }

    /* (non-Javadoc)
     * @see edu.csun.ecs.cs.multitouchj.ui.event.TouchListener#touchStarted(edu.csun.ecs.cs.multitouchj.ui.event.TouchEvent)
     */
    public void touchStarted(TouchEvent touchEvent) {
        addTouchEvent(touchEvent);
        handleTouchStarted();
    }

    /* (non-Javadoc)
     * @see edu.csun.ecs.cs.multitouchj.ui.event.FloatListener#floatEnded(edu.csun.ecs.cs.multitouchj.ui.event.FloatEvent)
     */
    public void floatEnded(FloatEvent floatEvent) {
        addFloatEvent(floatEvent);
        handleFloatEnded();
        
        removeFloatEvents();
    }

    /* (non-Javadoc)
     * @see edu.csun.ecs.cs.multitouchj.ui.event.FloatListener#floatMoved(edu.csun.ecs.cs.multitouchj.ui.event.FloatEvent)
     */
    public void floatMoved(FloatEvent floatEvent) {
        addFloatEvent(floatEvent);
        handleFloatMoved();
    }

    /* (non-Javadoc)
     * @see edu.csun.ecs.cs.multitouchj.ui.event.FloatListener#floatStarted(edu.csun.ecs.cs.multitouchj.ui.event.FloatEvent)
     */
    public void floatStarted(FloatEvent floatEvent) {
        addFloatEvent(floatEvent);
        handleFloatStarted();
    }
    
    public synchronized void addGestureHandler(GestureHandler gestureHandler) {
        gestureHandlers.add(gestureHandler);
    }
    
    public synchronized void removeGestureHandler(GestureHandler gestureHandler) {
        gestureHandlers.remove(gestureHandler);
    }
    
    protected synchronized void addTouchEvent(TouchEvent touchEvent) {
        touchEvents.addFirst(touchEvent);
        if(touchEvents.size() > MAXIMUM_EVENTS) {
            touchEvents.removeLast();
        }
    }
    
    protected synchronized void removeTouchEvent(TouchEvent touchEvent) {
        touchEvents.remove(touchEvent);
    }
    
    protected synchronized void removeTouchEvents() {
        touchEvents.clear();
    }
    
    protected synchronized void addFloatEvent(FloatEvent floatEvent) {
        floatEvents.addFirst(floatEvent);
        if(floatEvents.size() > MAXIMUM_EVENTS) {
            floatEvents.removeLast();
        }
    }
    
    protected synchronized void removeFloatEvent(FloatEvent floatEvent) {
        floatEvents.remove(floatEvent);
    }
    
    protected synchronized void removeFloatEvents() {
        floatEvents.clear();
    }
    
    protected synchronized void handleFloatStarted() {
        for(GestureHandler gestureHandler : gestureHandlers) {
            gestureHandler.handleFloatStarted(control, floatEvents);
        }
    }
    
    protected synchronized void handleFloatMoved() {
        for(GestureHandler gestureHandler : gestureHandlers) {
            gestureHandler.handleFloatMoved(control, floatEvents);
        }
    }
    
    protected synchronized void handleFloatEnded() {
        for(GestureHandler gestureHandler : gestureHandlers) {
            gestureHandler.handleFloatEnded(control, floatEvents);
        }
    }
    
    protected synchronized void handleTouchStarted() {
        for(GestureHandler gestureHandler : gestureHandlers) {
            gestureHandler.handleTouchStarted(control, touchEvents);
        }
    }
    
    protected synchronized void handleTouchMoved() {
        for(GestureHandler gestureHandler : gestureHandlers) {
            gestureHandler.handleTouchMoved(control, touchEvents);
        }
    }
    
    protected synchronized void handleTouchEnded() {
        for(GestureHandler gestureHandler : gestureHandlers) {
            gestureHandler.handleTouchEnded(control, touchEvents);
        }
    }
}
