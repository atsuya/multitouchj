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
package edu.csun.ecs.cs.multitouchj.ui.control;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.csun.ecs.cs.multitouchj.objectobserver.event.ObjectObserverEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.ObjectEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.TouchEvent;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Point;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Size;
import edu.csun.ecs.cs.multitouchj.ui.gesture.GestureHandler;
import edu.csun.ecs.cs.multitouchj.ui.gesture.GestureHandlerMove;
import edu.csun.ecs.cs.multitouchj.ui.gesture.GestureHandlerRotate;
import edu.csun.ecs.cs.multitouchj.ui.gesture.GestureHandlerScale;
import edu.csun.ecs.cs.multitouchj.ui.utility.PointUtility;

/**
 * @author Atsuya Takagi
 *
 * $Id: TouchableControl.java 79 2009-03-01 23:36:26Z Atsuya Takagi $
 */
public class TouchableControl extends FramedControl {
    private static Log log = LogFactory.getLog(TouchableControl.class);
    
    
    public TouchableControl() {
        super();
    }
    
    protected List<GestureHandler>getGestureHandlers() {
        List<GestureHandler> gestureHandlers = super.getGestureHandlers();
        gestureHandlers.add(new GestureHandlerMove());
        gestureHandlers.add(new GestureHandlerScale());
        gestureHandlers.add(new GestureHandlerRotate());
        
        return gestureHandlers;
    }
}
