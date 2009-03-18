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
package edu.csun.ecs.cs.multitouchj.ui.event;

import java.util.List;

import edu.csun.ecs.cs.multitouchj.objectobserver.event.Event;
import edu.csun.ecs.cs.multitouchj.objectobserver.event.ObjectObserverEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.ObjectEvent.Status;

/**
 * @author Atsuya Takagi
 *
 * $Id: TouchEvent.java 74 2009-02-23 09:39:07Z Atsuya Takagi $
 */
public class TouchEvent extends ObjectEvent {
    public TouchEvent(
        Object source,
        ObjectObserverEvent targetObjectObserverEvent,
        List<ObjectObserverEvent> objectObserverEvents,
        Status status
        ) {
        super(source, targetObjectObserverEvent, objectObserverEvents, status);
    }

    /* (non-Javadoc)
     * @see edu.csun.ecs.cs.multitouchj.ui.event.ObjectEvent#copy(edu.csun.ecs.cs.multitouchj.ui.event.ObjectEvent)
     */
    @Override
    public ObjectEvent copy() {
        return new TouchEvent(
            getSource(),
            getTargetObjectObserverEvent(),
            getObjectObserverEvents(),
            getStatus()
        );
    }
}
