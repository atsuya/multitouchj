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
import edu.csun.ecs.cs.multitouchj.ui.geometry.Size;
import edu.csun.ecs.cs.multitouchj.ui.utility.PointUtility;

/**
 * @author Atsuya Takagi
 *
 * $Id: GestureHandlerScale.java 77 2009-02-28 01:09:09Z Atsuya Takagi $
 */
public class GestureHandlerScale extends GestureHandler {
    private static Log log = LogFactory.getLog(GestureHandlerScale.class);
    
    
    public void handleTouchMoved(Control control, List<TouchEvent> touchEvents) {
        //log.info("GestureHandlerScale!");
        
        TouchEvent currentTouchEvent = touchEvents.get(0);
        TouchEvent previousTouchEvent = touchEvents.get(1);
        List<ObjectObserverEvent> currentOoes =
            currentTouchEvent.getObjectObserverEvents();
        List<ObjectObserverEvent> previousOoes =
            previousTouchEvent.getObjectObserverEvents();
        
        if(previousOoes.size() == 2) {
            if(currentOoes.size() == 2) {
                ObjectObserverEvent previousEventA = previousOoes.get(0);
                ObjectObserverEvent previousEventB = previousOoes.get(1);
                Point previousPointA = new Point(previousEventA.getX(), previousEventA.getY());
                Point previousPointB = new Point(previousEventB.getX(), previousEventB.getY());
                float previousDistance = PointUtility.getDistance(previousPointA, previousPointB);
                
                ObjectObserverEvent currentEventA = currentOoes.get(0);
                ObjectObserverEvent currentEventB = currentOoes.get(1);
                Point currentPointA = new Point(currentEventA.getX(), currentEventA.getY());
                Point currentPointB = new Point(currentEventB.getX(), currentEventB.getY());
                float currentDistance = PointUtility.getDistance(currentPointA, currentPointB);
                
                float ratio = (currentDistance / previousDistance);
                Size size = control.getSize();
                float newWidth = (size.getWidth() * ratio);
                float newHeight = (size.getHeight() * ratio);
                if((newWidth > 0.0f) && (newHeight > 0.0f)) {
                    control.setSize(new Size(newWidth, newHeight));
                }
                /*
                ObjectObserverEvent previousEventA = previousOoes.get(0);
                ObjectObserverEvent previousEventB = previousOoes.get(1);
                float previousDeltaX = Math.abs(previousEventA.getX() - previousEventB.getX());
                float previousDeltaY = Math.abs(previousEventA.getY() - previousEventB.getY());
                
                ObjectObserverEvent currentEventA = currentOoes.get(0);
                ObjectObserverEvent currentEventB = currentOoes.get(1);
                float currentDeltaX = Math.abs(currentEventA.getX() - currentEventB.getX());
                float currentDeltaY = Math.abs(currentEventA.getY() - currentEventB.getY());
                
                float deltaX = (currentDeltaX - previousDeltaX);
                float deltaY = (currentDeltaY - previousDeltaY);
                
                Point position = control.getPosition();
                control.setPosition(new Point(
                    (position.getX() - (deltaX / 2.0f)),
                    (position.getY() - (deltaY / 2.0f))
                ));
                Size size = control.getSize();
                control.setSize(new Size(
                    (size.getWidth() + deltaX),
                    (size.getHeight() + deltaY)
                ));
                */
            }
        }
    }
}
