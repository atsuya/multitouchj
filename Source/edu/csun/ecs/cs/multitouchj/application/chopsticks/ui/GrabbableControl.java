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
package edu.csun.ecs.cs.multitouchj.application.chopsticks.ui;

import java.util.LinkedList;
import java.util.List;

import edu.csun.ecs.cs.multitouchj.application.chopsticks.gesture.GestureHandlerGrab;
import edu.csun.ecs.cs.multitouchj.ui.control.FramedControl;
import edu.csun.ecs.cs.multitouchj.ui.control.TouchableControl;
import edu.csun.ecs.cs.multitouchj.ui.gesture.GestureHandler;
import edu.csun.ecs.cs.multitouchj.ui.gesture.GestureHandlerMove;

/**
 * @author Atsuya Takagi
 *
 * $Id$
 */
public class GrabbableControl extends TouchableControl {
    private boolean isGrabbed;
    
    
    public GrabbableControl() {
        super();
        
        setGrabbed(false);
    }
    
    public boolean isGrabbed() {
        return isGrabbed;
    }
    
    public void setGrabbed(boolean isGrabbed) {
        this.isGrabbed = isGrabbed;
    }
    
    protected List<GestureHandler>getGestureHandlers() {
        LinkedList<GestureHandler> gestureHandlers = new LinkedList<GestureHandler>();
        //gestureHandlers.add(new GestureHandlerMove());
        
        return gestureHandlers;
    }
}
