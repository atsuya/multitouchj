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
import java.util.LinkedList;

import edu.csun.ecs.cs.multitouchj.objectobserver.event.ObjectObserverEvent;
import edu.csun.ecs.cs.multitouchj.ui.control.Control;
import edu.csun.ecs.cs.multitouchj.ui.event.FloatEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.ObjectEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.TouchEvent;

/**
 * @author Atsuya Takagi
 *
 * $Id: GestureHandler.java 71 2009-02-19 08:27:26Z Atsuya Takagi $
 */
public abstract class GestureHandler {
    public GestureHandler() {
    }
    
    public void handleTouchStarted(Control control, List<TouchEvent> touchEvents) {
    }
    
    public void handleTouchMoved(Control control, List<TouchEvent> touchEvents) {
    }
    
    public void handleTouchEnded(Control control, List<TouchEvent> touchEvents) {
    }
    
    public void handleFloatStarted(Control control, List<FloatEvent> floatEvents) {
    }
    
    public void handleFloatMoved(Control control, List<FloatEvent> floatEvents) {
    }
    
    public void handleFloatEnded(Control control, List<FloatEvent> floatEvents) {
    }
    
    protected List<List<ObjectObserverEvent>> extractObjectObserverEvents(
        List objectEvents
        ) {
        LinkedList<List<ObjectObserverEvent>> lists =
            new LinkedList<List<ObjectObserverEvent>>();
        for(Object object : objectEvents) {
            if(object instanceof ObjectEvent) {
                ObjectEvent objectEvent = (ObjectEvent)object;
                lists.addLast(objectEvent.getObjectObserverEvents());
            }
        }
        
        return lists;
    }
}
