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

/**
 * @author Atsuya Takagi
 *
 * $Id: ObjectObserverEvent.java 58 2009-02-11 01:38:58Z Atsuya Takagi $
 */
public class ObjectObserverEvent extends Event {
    private int id;
    private float x;
    private float y;
    private float size;
    private long time;
    
    
    public ObjectObserverEvent(
        Object source,
        int id,
        float x,
        float y,
        float size,
        long time
        ) {
        super(source);
        
        this.id = id;
        this.x = x;
        this.y = y;
        this.size = size;
        this.time = time;
    }
    
    public int getId() {
        return id;
    }
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public float getSize() {
        return size;
    }
    
    public long getTime() {
        return time;
    }
    
    public String toString() {
        return "id: "+id+", x: "+x+", y: "+y+", size: "+size;
    }
}
