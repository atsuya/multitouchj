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
package edu.csun.ecs.cs.multitouchj.application.chopsticks.gesture;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.csun.ecs.cs.multitouchj.application.chopsticks.ui.GrabbableControl;
import edu.csun.ecs.cs.multitouchj.application.chopsticks.ui.GrabbableControl.Grabbed;
import edu.csun.ecs.cs.multitouchj.objectobserver.event.ObjectObserverEvent;
import edu.csun.ecs.cs.multitouchj.ui.control.Control;
import edu.csun.ecs.cs.multitouchj.ui.event.TouchEvent;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Point;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Size;
import edu.csun.ecs.cs.multitouchj.ui.gesture.GestureHandler;

/**
 * @author Atsuya Takagi
 *
 * $Id$
 */
public class GestureHandlerGrab extends GestureHandler {
    private static Log log = LogFactory.getLog(GestureHandlerGrab.class);
    private static final int GRAB_SIZE = 20;
    
    
    /* (non-Javadoc)
     * @see edu.csun.ecs.cs.multitouchj.ui.gesture.GestureHandler#handleTouchMoved(edu.csun.ecs.cs.multitouchj.ui.control.Control, java.util.List)
     */
    @Override
    public void handleTouchMoved(Control control, List<TouchEvent> touchEvents) {
        GrabbableControl grabbableControl = (GrabbableControl)control;
        
        TouchEvent currentTouchEvent = touchEvents.get(0);
        TouchEvent previousTouchEvent = touchEvents.get(1);
        List<ObjectObserverEvent> currentOoes =
            currentTouchEvent.getObjectObserverEvents();
        List<ObjectObserverEvent> previousOoes =
            previousTouchEvent.getObjectObserverEvents();
        
        // is grabbed?
        if(currentOoes.size() == 2) {
            log.debug("2 inputs!");
            
            ObjectObserverEvent currentOoe1 = currentOoes.get(0);
            ObjectObserverEvent currentOoe2 = currentOoes.get(1);
            Point clientPosition1 = control.getClientPosition(
                new Point(currentOoe1.getX(), currentOoe1.getY())
            );
            Point clientPosition2 = control.getClientPosition(
                new Point(currentOoe2.getX(), currentOoe2.getY())
            );
            
            boolean isLeftGrabbed =
                (isLeftGrabbed(control, clientPosition1) || isLeftGrabbed(control, clientPosition2));
            boolean isRightGrabbed =
                (isRightGrabbed(control, clientPosition1) || isRightGrabbed(control, clientPosition2));
            boolean isBottomGrabbed =
                (isBottomGrabbed(control, clientPosition1) || isBottomGrabbed(control, clientPosition2));
            
            Grabbed grabbed = null;
            if(isLeftGrabbed && isRightGrabbed) {
                grabbed = Grabbed.LeftRight;
            } else if(isLeftGrabbed && isBottomGrabbed) {
                grabbed = Grabbed.LeftBottom;
            } else if(isRightGrabbed && isBottomGrabbed) {
                grabbed = Grabbed.RightBottom;
            }
            if(grabbed != null) {
                log.debug("Grabbed: "+grabbed.toString());
                grabbableControl.setGrabbed(grabbed);
                
                // move
                if(previousOoes.size() == 2) {
                    ObjectObserverEvent previousOoe1 = previousOoes.get(0);
                    ObjectObserverEvent previousOoe2 = previousOoes.get(1);
                    float deltaX = (currentOoe1.getX() - previousOoe1.getX());
                    float deltaY = (currentOoe1.getY() - previousOoe1.getY());
                    
                    Point position = new Point(control.getPosition());
                    position.add(deltaX, deltaY);
                    control.setPosition(position);
                }
            }
        }
        
        /*
        ObjectObserverEvent currentOoe = currentOoes.get(0);
        Point clientPosition = control.getClientPosition(new Point(currentOoe.getX(), currentOoe.getY()));
        Size size = control.getSize();
        
        log.debug("client: "+clientPosition.toString());
        
        if(clientPosition.getY() > (size.getHeight() - 20)) {
            grabbableControl.setGrabbed(true);
            log.debug("grabbed");
            
        } else {
            grabbableControl.setGrabbed(false);
            log.debug("NOT grabbed");
        }
        */
        /*
        if((previousOoes.size() == 2) && (currentOoes.size() == 2)) {
            ObjectObserverEvent previousOoe = previousOoes.get(0);
            ObjectObserverEvent currentOoe = currentOoes.get(0);
            
            float deltaX = (currentOoe.getX() - previousOoe.getX());
            float deltaY = (currentOoe.getY() - previousOoe.getY());
            
            Point currentPosition = control.getPosition();
            control.setPosition(
                new Point(
                    (currentPosition.getX() + deltaX),
                    (currentPosition.getY() + deltaY)
                )
            );
        }
        */
    }
    
    /* (non-Javadoc)
     * @see edu.csun.ecs.cs.multitouchj.ui.gesture.GestureHandler#handleTouchMoved(edu.csun.ecs.cs.multitouchj.ui.control.Control, java.util.List)
     */
    @Override
    public void handleTouchEnded(Control control, List<TouchEvent> touchEvents) {
        GrabbableControl grabbableControl = (GrabbableControl)control;
        grabbableControl.setGrabbed(null);
    }
    
    private boolean isLeftGrabbed(Control control, Point clientPosition) {
        boolean isGrabbed = false;
        
        if(clientPosition.getX() < GRAB_SIZE) {
            isGrabbed = true;
        }
        
        return isGrabbed;
    }
    
    private boolean isRightGrabbed(Control control, Point clientPosition) {
        boolean isGrabbed = false;
        
        Size size = control.getSize();
        if(clientPosition.getX() > (size.getWidth() - GRAB_SIZE)) {
            isGrabbed = true;
        }
        
        return isGrabbed;
    }
    
    private boolean isBottomGrabbed(Control control, Point clientPosition) {
        boolean isGrabbed = false;
        
        Size size = control.getSize();
        if(clientPosition.getY() > (size.getHeight() - GRAB_SIZE)) {
            isGrabbed = true;
        }
        
        return isGrabbed;
    }
}
