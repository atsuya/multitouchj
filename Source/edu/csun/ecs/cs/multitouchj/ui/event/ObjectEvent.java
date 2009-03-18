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

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.csun.ecs.cs.multitouchj.objectobserver.event.Event;
import edu.csun.ecs.cs.multitouchj.objectobserver.event.ObjectObserverEvent;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Point;
import edu.csun.ecs.cs.multitouchj.ui.graphic.CalibrationHandler;

/**
 * @author Atsuya Takagi
 *
 * $Id: ObjectEvent.java 79 2009-03-01 23:36:26Z Atsuya Takagi $
 */
public abstract class ObjectEvent extends PositionEvent {
    private static Log log = LogFactory.getLog(ObjectEvent.class);
    public enum Status {
        Started,
        Moved,
        Ended
    }
    private ObjectObserverEvent targetObjectObserverEvent;
    private LinkedList<ObjectObserverEvent> objectObserverEvents;
    private Status status;
    
    
    public ObjectEvent(
        Object source,
        ObjectObserverEvent targetObjectObserverEvent,
        List<ObjectObserverEvent> objectObserverEvents,
        Status status
        ) {
        super(source, targetObjectObserverEvent.getX(), targetObjectObserverEvent.getY());

        this.targetObjectObserverEvent = targetObjectObserverEvent;
        this.objectObserverEvents = new LinkedList<ObjectObserverEvent>(objectObserverEvents);
        this.status = status;
    }
    
    public abstract ObjectEvent copy();
    
    public ObjectObserverEvent getTargetObjectObserverEvent() {
        return targetObjectObserverEvent;
    }
    
    public void setTargetObjectObserverEvent(ObjectObserverEvent targetObjectObserverEvent) {
        this.targetObjectObserverEvent = targetObjectObserverEvent;
    }
    
    public List<ObjectObserverEvent> getObjectObserverEvents() {
        return new LinkedList<ObjectObserverEvent>(objectObserverEvents);
    }
    
    public void setObjectObserverEvents(List<ObjectObserverEvent> objectObserverEvents) {
        this.objectObserverEvents.clear();
        this.objectObserverEvents.addAll(objectObserverEvents);
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public void calibrate(CalibrationHandler calibrationHandler) {
        super.calibrate(calibrationHandler);
        
        // determine the index of target ooe in all ooes.
        int targetIndex = -1;
        for(int i = 0; i < objectObserverEvents.size(); i++) {
            if(objectObserverEvents.get(i).equals(targetObjectObserverEvent)) {
                targetIndex = i;
                break;
            }
        }
        
        // calibrate all ooe
        LinkedList<ObjectObserverEvent> newOoes = new LinkedList<ObjectObserverEvent>();
        for(ObjectObserverEvent ooe : objectObserverEvents) {
            Point point = calibrationHandler.calibrate(new Point(ooe.getX(), ooe.getY()));
            ObjectObserverEvent newOoe = new ObjectObserverEvent(
                ooe.getSource(),
                ooe.getId(),
                point.getX(),
                point.getY(),
                ooe.getSize(),
                ooe.getTime()
            );
            
            newOoes.add(newOoe);
        }
        objectObserverEvents.clear();
        objectObserverEvents.addAll(newOoes);
        
        // set target ooe
        ObjectObserverEvent newTargetOoe = null;
        if(targetIndex != -1) {
            newTargetOoe = objectObserverEvents.get(targetIndex);
        }
        targetObjectObserverEvent = newTargetOoe;
    }
}
