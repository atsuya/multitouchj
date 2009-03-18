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

import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.csun.ecs.cs.multitouchj.objectobserver.event.Event;
import edu.csun.ecs.cs.multitouchj.objectobserver.event.EventListenerManager;
import edu.csun.ecs.cs.multitouchj.objectobserver.event.ObjectObserverEvent;
import edu.csun.ecs.cs.multitouchj.objectobserver.event.ObjectObserverListener;
import edu.csun.ecs.cs.multitouchj.ui.graphic.WindowManager;

/**
 * @author Atsuya Takagi
 *
 * $Id: ObjectEventManager.java 79 2009-03-01 23:36:26Z Atsuya Takagi $
 */
public class ObjectEventManager implements ObjectObserverListener {
    private static final long CLEAN_UP_TIME = 200;
    private static Log log = LogFactory.getLog(ObjectEventManager.class);
    private enum ObjectType {
        Floated, Touched
    }
    private Hashtable<ObjectType, LinkedHashMap<Integer, ObjectObserverEvent>> activeObjects;
    private Hashtable<ObjectType, Boolean> eventsCleaned;
    private boolean isRunning;
    private Thread thread;
    private boolean isUpdated;
    private EventListenerManager eventListenerManager;
    
    
    public ObjectEventManager() {
        activeObjects = new Hashtable<ObjectType, LinkedHashMap<Integer, ObjectObserverEvent>>();
        for(ObjectType objectType : ObjectType.values()) {
            activeObjects.put(objectType, new LinkedHashMap<Integer, ObjectObserverEvent>());
        }
        
        eventsCleaned = new Hashtable<ObjectType, Boolean>();
        eventListenerManager = new EventListenerManager();
        
        setRunning(false);
        setUpdated(false);
    }
    
    public void addObjectEventManagerListener(ObjectEventManagerListener listener) {
        eventListenerManager.addEventListener(ObjectEventManagerListener.class, listener);
    }
    
    public void removeObjectEventManagerListener(ObjectEventManagerListener listener) {
        eventListenerManager.removeEventListener(ObjectEventManagerListener.class, listener);
    }
    
    public void start() {
        if(!isRunning()) {
            thread = new Thread(new Runnable(){
                public void run() {
                    handleEvent();
                }
            });
            thread.start();
        }
    }
    
    public void stop() {
        setRunning(false);
        try {
            thread.join(60000);
        } catch(Exception exception) {
            log.error("Failed to join.", exception);
        }
    }
    
    public boolean isRunning() {
        return isRunning;
    }

    /* (non-Javadoc)
     * @see edu.csun.ecs.cs.multitouchj.objectobserver.event.ObjectObserverListener#objectObserved(edu.csun.ecs.cs.multitouchj.objectobserver.event.ObjectObserverEvent)
     */
    public void objectObserved(ObjectObserverEvent event) {
        handleObjectObserverEvent(ObjectType.Floated, event);
    }

    /* (non-Javadoc)
     * @see edu.csun.ecs.cs.multitouchj.objectobserver.event.ObjectObserverListener#objectTouched(edu.csun.ecs.cs.multitouchj.objectobserver.event.ObjectObserverEvent)
     */
    public void objectTouched(ObjectObserverEvent event) {
        handleObjectObserverEvent(ObjectType.Touched, event);
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    protected void handleEvent() {
        setRunning(true);
        
        for(ObjectType objectType : ObjectType.values()) {
            eventsCleaned.put(objectType, true);
        }
        
        while(isRunning()) {
            synchronized(activeObjects) {
                // check for "started" or "moved"
                if(isUpdated()) {
                    for(ObjectType objectType : activeObjects.keySet()) {
                        LinkedHashMap<Integer, ObjectObserverEvent> objects = activeObjects.get(objectType);
                        if(objects.size() > 0) {
                            LinkedList<ObjectObserverEvent> ooes =
                                new LinkedList<ObjectObserverEvent>(objects.values());
                            ObjectEvent objectEvent = null;
                            if(ObjectType.Floated.equals(objectType)) {
                                objectEvent = new FloatEvent(
                                    this,
                                    ooes.getLast(),
                                    ooes,
                                    (eventsCleaned.get(objectType)) ? ObjectEvent.Status.Started : ObjectEvent.Status.Moved
                                );
                            } else {
                                objectEvent = new TouchEvent(
                                    this,
                                    ooes.getLast(),
                                    ooes,
                                    (eventsCleaned.get(objectType)) ? ObjectEvent.Status.Started : ObjectEvent.Status.Moved
                                );
                            }
                            notifyEventListeners(objectEvent);
                            
                            eventsCleaned.put(objectType, false);
                        }
                    }
                    
                    setUpdated(false);
                }
                
                // check for "ended"
                for(ObjectType objectType : activeObjects.keySet()) {
                    LinkedHashMap<Integer, ObjectObserverEvent> objects = activeObjects.get(objectType);
                    if(objects.size() > 0) {
                        LinkedList<ObjectObserverEvent> ooes =
                            new LinkedList<ObjectObserverEvent>(objects.values());
                        
                        // check for ones expired
                        long currentTime = (new Date()).getTime();
                        LinkedList<Integer> deletedIds = new LinkedList<Integer>();
                        for(ObjectObserverEvent ooe : ooes) {
                            if((currentTime - ooe.getTime()) >= CLEAN_UP_TIME) {
                                deletedIds.add(ooe.getId());
                            }
                        }
                        for(int deletedId : deletedIds) {
                            objects.remove(deletedId);
                        }
                        
                        // check if all expired
                        if(objects.size() == 0) {
                            ObjectEvent objectEvent = null;
                            if(ObjectType.Floated.equals(objectType)) {
                                objectEvent = new FloatEvent(
                                    this,
                                    ooes.getLast(),
                                    ooes,
                                    ObjectEvent.Status.Ended
                                );
                            } else {
                                objectEvent = new TouchEvent(
                                    this,
                                    ooes.getLast(),
                                    ooes,
                                    ObjectEvent.Status.Ended
                                );
                            }
                            notifyEventListeners(objectEvent);
                            
                            eventsCleaned.put(objectType, true);
                        }
                    }
                }
            }
            
            try {
                Thread.sleep(15);
            } catch(Exception exception) {}
        }
    }
    
    protected void setRunning(boolean value) {
        isRunning = value;
    }
    
    protected boolean isUpdated() {
        return isUpdated;
    }
    
    protected void setUpdated(boolean value) {
        isUpdated = value;
    }
    
    protected void handleObjectObserverEvent(
        ObjectType objectType,
        ObjectObserverEvent event
        ) {
        if(isRunning()) {
            synchronized(activeObjects) {
                LinkedHashMap<Integer, ObjectObserverEvent> objects =
                    activeObjects.get(objectType);
                // remove is needed to keep its order
                if(objects.containsKey(event.getId())) {
                    objects.remove(event.getId());
                }
                objects.put(event.getId(), event);
                
                setUpdated(true);
            }
        }
    }
    
    protected void notifyEventListeners(ObjectEvent objectEvent) {
        try {
            boolean isFloat = (objectEvent instanceof FloatEvent);
            
            eventListenerManager.notifyEventListeners(
                ObjectEventManagerListener.class,
                (isFloat) ? "floatEventGenerated" : "touchEventGenerated",
                (isFloat) ? new Class[]{FloatEvent.class} : new Class[]{TouchEvent.class},
                new Object[]{objectEvent}
            );
        } catch(Exception exception) {
            log.error("Failed to notify listeners.", exception);
        }
    }
}
