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
import edu.csun.ecs.cs.multitouchj.ui.event.FloatEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.ObjectEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.TouchEvent;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Point;

/**
 * @author Atsuya Takagi
 *
 * $Id: GestureHandlerMove.java 77 2009-02-28 01:09:09Z Atsuya Takagi $
 */
public class GestureHandlerMove extends GestureHandler {
    private static Log log = LogFactory.getLog(GestureHandlerMove.class);
    
    
    /* (non-Javadoc)
     * @see edu.csun.ecs.cs.multitouchj.ui.gesture.GestureHandler#handleTouchMoved(edu.csun.ecs.cs.multitouchj.ui.control.Control, java.util.List)
     */
    @Override
    public void handleTouchMoved(Control control, List<TouchEvent> touchEvents) {
        TouchEvent currentTouchEvent = touchEvents.get(0);
        TouchEvent previousTouchEvent = touchEvents.get(1);
        List<ObjectObserverEvent> currentOoes =
            currentTouchEvent.getObjectObserverEvents();
        List<ObjectObserverEvent> previousOoes =
            previousTouchEvent.getObjectObserverEvents();
        
        if(previousOoes.size() == 1) {
            if(currentOoes.size() == 1) {
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
        }
    }
}
