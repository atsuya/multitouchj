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

import java.awt.Color;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
    private static Log log = LogFactory.getLog(GrabbableControl.class);
    public enum Grabbed {
        LeftRight("/edu/csun/ecs/cs/multitouchj/application/chopsticks/resource/Grabbed-LeftRight.png"),
        LeftBottom("/edu/csun/ecs/cs/multitouchj/application/chopsticks/resource/Grabbed-LeftBottom.png"),
        RightBottom("/edu/csun/ecs/cs/multitouchj/application/chopsticks/resource/Grabbed-RightBottom.png"),
        Bottom("/edu/csun/ecs/cs/multitouchj/application/chopsticks/resource/Grabbed-Bottom.png");
        
        private final String uri;
        
        Grabbed(String uri) {
            this.uri = uri;
        }
        
        public String getUri() {
            return uri;
        }
    }
    private Grabbed grabbed;
    
    
    public GrabbableControl() {
        super();
        
        setGrabbed(null);
    }
    
    public boolean isGrabbed() {
        return (grabbed != null);
    }
    
    public void setGrabbed(Grabbed grabbed) {
        this.grabbed = grabbed;
    }
    
    public void updateGrabbed() {
        try {
            URL url = null;
            if(grabbed != null) {
                url = getClass().getResource(this.grabbed.getUri());
            }
            setTexture(url);
        } catch(Exception exception){
            log.debug("Failed to set texture: "+grabbed.getUri(), exception);
        }
    }
    
    protected List<GestureHandler>getGestureHandlers() {
        LinkedList<GestureHandler> gestureHandlers = new LinkedList<GestureHandler>();
        gestureHandlers.add(new GestureHandlerGrab());
        
        return gestureHandlers;
    }
}
