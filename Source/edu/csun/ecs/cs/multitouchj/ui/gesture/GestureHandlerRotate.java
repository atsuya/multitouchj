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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.csun.ecs.cs.multitouchj.objectobserver.event.ObjectObserverEvent;
import edu.csun.ecs.cs.multitouchj.ui.control.Control;
import edu.csun.ecs.cs.multitouchj.ui.event.TouchEvent;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Point;
import edu.csun.ecs.cs.multitouchj.ui.utility.PointUtility;

/**
 * @author Atsuya Takagi
 *
 * $Id: GestureHandlerRotate.java 79 2009-03-01 23:36:26Z Atsuya Takagi $
 */
public class GestureHandlerRotate extends GestureHandler {
    private static Log log = LogFactory.getLog(GestureHandlerRotate.class);
    
    
    public void handleTouchMoved(Control control, List<TouchEvent> touchEvents) {
        TouchEvent currentTouchEvent = touchEvents.get(0);
        TouchEvent previousTouchEvent = touchEvents.get(1);
        List<ObjectObserverEvent> currentOoes =
            currentTouchEvent.getObjectObserverEvents();
        List<ObjectObserverEvent> previousOoes =
            previousTouchEvent.getObjectObserverEvents();
        
        if(previousOoes.size() == 2) {
            if(currentOoes.size() == 2) {
                ObjectObserverEvent previousOoeA = previousOoes.get(0);
                ObjectObserverEvent previousOoeB = previousOoes.get(1);
                ObjectObserverEvent currentOoeA = currentOoes.get(0);
                ObjectObserverEvent currentOoeB = currentOoes.get(1);
                
                double previousAngle = getAngle(previousOoeA, previousOoeB);
                double currentAngle = getAngle(currentOoeA, currentOoeB);
                double angleDelta = Math.abs(currentAngle - previousAngle);
                
                float angle = control.getRotation();
                
                ObjectObserverEvent previousAboveOoe = previousOoeA;
                ObjectObserverEvent previousBelowOoe = previousOoeB;
                if(previousBelowOoe.getY() < previousAboveOoe.getY()) {
                    previousAboveOoe = previousOoeB;
                    previousBelowOoe = previousOoeA;
                }
                
                ObjectObserverEvent currentAboveOoe = currentOoeA;
                ObjectObserverEvent currentBelowOoe = currentOoeB;
                if(currentBelowOoe.getY() < currentAboveOoe.getY()) {
                    currentAboveOoe = currentOoeB;
                    currentBelowOoe = currentOoeA;
                }
                
                boolean isClockwise = true;
                if((currentAboveOoe.getX() <= previousAboveOoe.getX()) &&
                    (currentBelowOoe.getX() >= previousBelowOoe.getX())) {
                    isClockwise = false;
                }
                if(isClockwise) {
                    angleDelta *= -1;
                }
                control.setRotation((control.getRotation() + (float)angleDelta));
                
                log.debug("prev: "+previousAngle+", curr: "+currentAngle+", delta: "+angleDelta+", now: "+angle+", after: "+control.getRotation());
            }
        }
    }
    
    private double getAngle(ObjectObserverEvent eventA, ObjectObserverEvent eventB) {
        Point pointA = new Point(eventA.getX(), eventA.getY());
        Point pointB = new Point(eventB.getX(), eventB.getY());
        
        return PointUtility.getAngle(pointA, pointB);
    }
}
