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

import edu.csun.ecs.cs.multitouchj.objectobserver.event.Event;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Point;
import edu.csun.ecs.cs.multitouchj.ui.graphic.CalibrationHandler;

/**
 * @author Atsuya Takagi
 *
 * $Id: PositionEvent.java 74 2009-02-23 09:39:07Z Atsuya Takagi $
 */
public abstract class PositionEvent extends Event {
    private Point position;
    
    
    public PositionEvent(Object source, float x, float y) {
        super(source);
        
        position = new Point(x, y);
    }
    
    public Point getPosition() {
        return new Point(position);
    }
    
    public float getX() {
        return position.getX();
    }
    
    public void setX(float x) {
        position.setX(x);
    }
    
    public float getY() {
        return position.getY();
    }
    
    public void setY(float y) {
        position.setY(y);
    }
    
    public void set(float x, float y) {
        position.set(x, y);
    }
    
    public void calibrate(CalibrationHandler calibrationHandler) {
        Point point = calibrationHandler.calibrate(new Point(getX(), getY()));
        set(point.getX(), point.getY());
    }
}
