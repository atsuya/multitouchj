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

/**
 * @author Atsuya Takagi
 *
 * $Id: WindowManagerCalibratorEvent.java 64 2009-02-12 23:32:19Z Atsuya Takagi $
 */
public class WindowManagerCalibratorEvent extends Event {
    private Point sourceTopLeftPosition;
    private Point sourceTopRightPosition;
    private Point sourceBottomRightPosition;
    private Point sourceBottomLeftPosition;
    private Point destinationTopLeftPosition;
    private Point destinationTopRightPosition;
    private Point destinationBottomRightPosition;
    private Point destinationBottomLeftPosition;
    
    
    public WindowManagerCalibratorEvent(
        Object source,
        Point sourceTopLeftPosition,
        Point sourceTopRightPosition,
        Point sourceBottomRightPosition,
        Point sourceBottomLeftPosition,
        Point destinationTopLeftPosition,
        Point destinationTopRightPosition,
        Point destinationBottomRightPosition,
        Point destinationBottomLeftPosition
        ) {
        super(source);
        
        this.sourceTopLeftPosition = new Point(sourceTopLeftPosition);
        this.sourceTopRightPosition = new Point(sourceTopRightPosition);
        this.sourceBottomRightPosition = new Point(sourceBottomRightPosition);
        this.sourceBottomLeftPosition = new Point(sourceBottomLeftPosition);
        
        this.destinationTopLeftPosition = new Point(destinationTopLeftPosition);
        this.destinationTopRightPosition = new Point(destinationTopRightPosition);
        this.destinationBottomRightPosition = new Point(destinationBottomRightPosition);
        this.destinationBottomLeftPosition = new Point(destinationBottomLeftPosition);
    }
    
    public Point getSourceTopLeftPosition() {
        return sourceTopLeftPosition;
    }
    
    public Point getSourceTopRightPosition() {
        return sourceTopRightPosition;
    }
    
    public Point getSourceBottomRightPosition() {
        return sourceBottomRightPosition;
    }
    
    public Point getSourceBottomLeftPosition() {
        return sourceBottomLeftPosition;
    }
    
    public Point getDestinationTopLeftPosition() {
        return destinationTopLeftPosition;
    }
    
    public Point getDestinationTopRightPosition() {
        return destinationTopRightPosition;
    }
    
    public Point getDestinationBottomRightPosition() {
        return destinationBottomRightPosition;
    }
    
    public Point getDestinationBottomLeftPosition() {
        return destinationBottomLeftPosition;
    }
}
