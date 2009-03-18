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
package edu.csun.ecs.cs.multitouchj.objectobserver;

import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.csun.ecs.cs.multitouchj.objectobserver.event.EventListenerManager;
import edu.csun.ecs.cs.multitouchj.objectobserver.event.ObjectObserverEvent;
import edu.csun.ecs.cs.multitouchj.objectobserver.event.ObjectObserverListener;

/**
 * @author Atsuya Takagi
 *
 * $Id: ObjectObserver.java 58 2009-02-11 01:38:58Z Atsuya Takagi $
 */
public abstract class ObjectObserver {
    private Log log = LogFactory.getLog(ObjectObserver.class);
    private EventListenerManager eventListenerManager;
    private boolean isStarted;
    
    
    public ObjectObserver() {
        eventListenerManager = new EventListenerManager();
        setStarted(false);
    }
    
    public void addObjectObserverListener(ObjectObserverListener listener) {
        eventListenerManager.addEventListener(
            ObjectObserverListener.class,
            listener
        );
    }
    
    public void removeObjectObserverListener(ObjectObserverListener listener) {
        eventListenerManager.removeEventListener(
            ObjectObserverListener.class,
            listener
        );
    }
    
    public abstract void initialize(Map<String, String> parameters) throws Exception;
    
    public abstract void start() throws Exception;
    
    public abstract void stop() throws Exception;
    
    public boolean isStarted() {
        return isStarted;
    }
    
    public void setStarted(boolean value) {
        isStarted = value;
    }
    
    protected void objectObserved(int id, float x, float y, float size) {
        ObjectObserverEvent event = new ObjectObserverEvent(
            this,
            id,
            x,
            y,
            size,
            new Date().getTime()
        );
        notifyListeners("objectObserved", event);
    }
    
    protected void objectTouched(int id, float x, float y, float size) {
        ObjectObserverEvent event = new ObjectObserverEvent(
            this,
            id,
            x,
            y,
            size,
            new Date().getTime()
        );
        notifyListeners("objectTouched", event);
    }
    
    protected void notifyListeners(String methodName, ObjectObserverEvent event) {
        try {
            eventListenerManager.notifyEventListeners(
                ObjectObserverListener.class,
                methodName,
                new Class[]{ObjectObserverEvent.class},
                new Object[]{event}
            );
        } catch(Exception exception) {
            log.error("Failed to notify ObjectObserverListener.", exception);
        }
    }
}
