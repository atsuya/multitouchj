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
package edu.csun.ecs.cs.multitouchj.ui.graphic;

import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.csun.ecs.cs.multitouchj.objectobserver.event.EventListenerManager;
import edu.csun.ecs.cs.multitouchj.ui.event.FloatEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.ObjectEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.ObjectEventManagerListener;
import edu.csun.ecs.cs.multitouchj.ui.event.TouchEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.WindowManagerCalibratorEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.WindowManagerCalibratorListener;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Point;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Size;

/**
 * @author Atsuya Takagi
 *
 * $Id: WindowManagerCalibrator.java 66 2009-02-13 01:55:18Z Atsuya Takagi $
 */
public abstract class WindowManagerCalibrator implements ObjectEventManagerListener {
    private static Log log = LogFactory.getLog(WindowManagerCalibrator.class);
    private static final Position[] POSITION_ORDER = {
        Position.TopLeft,
        Position.TopRight,
        Position.BottomRight,
        Position.BottomLeft
    };
    protected enum Position {
        TopLeft,
        TopRight,
        BottomRight,
        BottomLeft
    }
    private Position currentPosition;
    private Hashtable<Position, Point> sourcePositions;
    private Hashtable<Position, Point> destinationPositions;
    private boolean isStarted;
    private EventListenerManager eventListenerManager;
    private Size displaySize;
    
    
    public WindowManagerCalibrator() {
        sourcePositions = new Hashtable<Position, Point>();
        destinationPositions = new Hashtable<Position, Point>();
        
        eventListenerManager = new EventListenerManager();
        displaySize = new Size();
        
        setStarted(false);
    }
    
    /* (non-Javadoc)
     * @see edu.csun.ecs.cs.multitouchj.ui.event.ObjectEventManagerListener#floatEventGenerated(edu.csun.ecs.cs.multitouchj.ui.event.FloatEvent)
     */
    public void floatEventGenerated(FloatEvent floatEvent) {
    }

    /* (non-Javadoc)
     * @see edu.csun.ecs.cs.multitouchj.ui.event.ObjectEventManagerListener#touchEventGenerated(edu.csun.ecs.cs.multitouchj.ui.event.TouchEvent)
     */
    public void touchEventGenerated(TouchEvent touchEvent) {
        if(isStarted()) {
            if(ObjectEvent.Status.Started.equals(touchEvent.getStatus())) {
                log.info("Calibration: "+touchEvent.getX()+", "+touchEvent.getY());
                sourcePositions.put(
                    currentPosition,
                    new Point(touchEvent.getX(), touchEvent.getY())
                );
                onSourcePositionCaptured(currentPosition);
                
                Position nextPosition = getNextPosition();
                if(nextPosition != null) {
                    currentPosition = nextPosition;
                    presentCalibrationTarget(currentPosition);
                } else {
                    onFinished();
                }
            }
        }
    }
    
    public void addWindowManagerCalibratorListener(
        WindowManagerCalibratorListener listener
        ) {
        eventListenerManager.addEventListener(
            WindowManagerCalibratorListener.class,
            listener
        );
    }
    
    public void removeWindowManagerCalibratorListener(
        WindowManagerCalibratorListener listener
        ) {
        eventListenerManager.removeEventListener(
            WindowManagerCalibratorListener.class,
            listener
        );
    }
    
    public void start(Size displaySize) {
        this.displaySize.set(displaySize);
        
        sourcePositions.clear();
        destinationPositions.clear();
        
        setStarted(true);
        currentPosition = POSITION_ORDER[0];
        presentCalibrationTarget(currentPosition);
    }
    
    protected void onSourcePositionCaptured(Position position) {
    }
    
    protected void onFinished() {
        try {
            WindowManagerCalibratorEvent event = new WindowManagerCalibratorEvent(
                this,
                sourcePositions.get(Position.TopLeft),
                sourcePositions.get(Position.TopRight),
                sourcePositions.get(Position.BottomRight),
                sourcePositions.get(Position.BottomLeft),
                destinationPositions.get(Position.TopLeft),
                destinationPositions.get(Position.TopRight),
                destinationPositions.get(Position.BottomRight),
                destinationPositions.get(Position.BottomLeft)
            );
            eventListenerManager.notifyEventListeners(
                WindowManagerCalibratorListener.class,
                "calibrated",
                new Class[]{WindowManagerCalibratorEvent.class},
                new Object[]{event}
            );
        } catch(Exception exception) {
            log.error("Failed to notify event listener.", exception);
        }
        
        setStarted(false);
    }
    
    protected abstract void presentCalibrationTarget(Position position);
    
    protected Position getNextPosition() {
        int index = -1;
        for(int i = 0; i < POSITION_ORDER.length; i++) {
            if(POSITION_ORDER[i].equals(currentPosition)) {
                index = i;
                break;
            }
        }
        if(index != -1) {
            index += 1;
        }
        
        
        return ((index < 0) || (index >= POSITION_ORDER.length)) ? null : POSITION_ORDER[index];
    }
    
    protected void setStarted(boolean value) {
        isStarted = value;
    }
    
    protected boolean isStarted() {
        return isStarted;
    }
    
    protected Size getDisplaySize() {
        return displaySize;
    }
    
    protected Hashtable<Position, Point> getSourcePositions() {
        return sourcePositions;
    }
    
    protected Hashtable<Position, Point> getDestinationPositions() {
        return destinationPositions;
    }
}
