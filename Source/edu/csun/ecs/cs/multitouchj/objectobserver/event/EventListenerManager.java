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
package edu.csun.ecs.cs.multitouchj.objectobserver.event;

import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;


/**
 * @author Atsuya Takagi
 *
 * $Id: EventListenerManager.java 54 2009-02-09 20:30:44Z Atsuya Takagi $
 */
public class EventListenerManager {
    private Hashtable<Class, LinkedList<EventListener>> eventListeners;
    
    
    public EventListenerManager() {
        eventListeners = new Hashtable<Class, LinkedList<EventListener>>();
    }
    
    public List<EventListener> getEventListeners(Class klass) {
        return new LinkedList<EventListener>(prepareEventListeners(klass));
    }
    
    public void addEventListener(Class klass, EventListener eventListener) {
        LinkedList<EventListener> listeners = prepareEventListeners(klass);
        listeners.add(eventListener);
    }
    
    public void addEventListeners(Class klass, List<EventListener> eventListeners) {
        for(EventListener eventListener : eventListeners) {
            addEventListener(klass, eventListener);
        }
    }
    
    public void removeEventListener(Class klass, EventListener eventListener) {
        LinkedList<EventListener> listeners = prepareEventListeners(klass);
        listeners.remove(eventListener);
    }
    
    public void removeEventListeners(Class klass, List<EventListener> eventListeners) {
        for(EventListener eventListener : eventListeners) {
            removeEventListener(klass, eventListener);
        }
    }
    
    public boolean containsEventListeners(Class klass) {
        return eventListeners.containsKey(klass);
    }
    
    public void notifyEventListeners(
        Class klass,
        String methodName,
        Class[] classes,
        Object[] objects
        ) throws Exception {
        try {
            Method method = klass.getMethod(methodName, classes);
            for(EventListener eventListener : prepareEventListeners(klass)) {
                method.invoke(eventListener, objects);
            }
        } catch(Exception exception) {
            throw exception;
        }
    }
    
    private LinkedList<EventListener> prepareEventListeners(Class klass) {
        LinkedList<EventListener> listeners = eventListeners.get(klass);
        if(listeners == null) {
            listeners = new LinkedList<EventListener>();
            eventListeners.put(klass, listeners);
        }
        
        return listeners;
    }
}
