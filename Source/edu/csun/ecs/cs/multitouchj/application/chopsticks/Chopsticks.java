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
package edu.csun.ecs.cs.multitouchj.application.chopsticks;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import edu.csun.ecs.cs.multitouchj.application.chopsticks.ui.GrabbableControl;
import edu.csun.ecs.cs.multitouchj.objectobserver.ObjectObserver;
import edu.csun.ecs.cs.multitouchj.objectobserver.motej.ObjectObserverMoteJ;
import edu.csun.ecs.cs.multitouchj.objectobserver.mouse.ObjectObserverMouse;
import edu.csun.ecs.cs.multitouchj.ui.event.WindowManagerCalibratorEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.WindowManagerCalibratorListener;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Point;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Size;
import edu.csun.ecs.cs.multitouchj.ui.graphic.CursorCollectionDefault;
import edu.csun.ecs.cs.multitouchj.ui.graphic.DisplayManager;
import edu.csun.ecs.cs.multitouchj.ui.graphic.WindowManager;
import edu.csun.ecs.cs.multitouchj.utility.FrameMeter;

/**
 * @author Atsuya Takagi
 *
 * $Id$
 */
public class Chopsticks implements WindowManagerCalibratorListener {
    private static final String TITLE = "Chopsticks";
    private static final String URL_IMAGE_GRABBED =
        "/edu/csun/ecs/cs/multitouchj/application/chopsticks/resource/Grabbed.png";
    private static Log log = LogFactory.getLog(Chopsticks.class);
    private static final int NUMBER_OF_CONTROLS = 1;
    private boolean isRunning;
    private boolean isCalibrated;
    private boolean calibrationRequested;
    private DisplayMode displayMode;
    private LinkedList<GrabbableControl> grabbableControls;
    
    
    public Chopsticks() {
        grabbableControls = new LinkedList<GrabbableControl>();
    }
    
    /* (non-Javadoc)
     * @see edu.csun.ecs.cs.multitouchj.ui.event.WindowManagerCalibratorListener#calibrated(edu.csun.ecs.cs.multitouchj.ui.event.WindowManagerCalibratorEvent)
     */
    public void calibrated(WindowManagerCalibratorEvent event) {
        isCalibrated = true;
    }
    
    public void run(Map<String, String> parameters) {
        DisplayManager.create();
        DisplayManager displayManager = DisplayManager.getInstance();
        displayManager.setWindowTitle(TITLE);
        ObjectObserver objectObserver = new ObjectObserverMoteJ();
        WindowManager windowManager = null;

        try {
            for(DisplayMode mode : displayManager.getAvailableDisplayModes()) {
                log.info(mode.getWidth()+"x"+mode.getHeight()+":"+mode.getBitsPerPixel());
            }
            displayMode = displayManager.getDisplayMode(800, 600, 32);
            displayManager.createDisplay(displayMode);
            Keyboard.create();
            
            objectObserver.initialize(parameters);
            
            WindowManager.create(displayManager, objectObserver);
            windowManager = WindowManager.getInstance();
            windowManager.getWindowManagerCalibrator().addWindowManagerCalibratorListener(this);
            
            FrameMeter frameMeter = new FrameMeter();
            calibrationRequested = true;
            isCalibrated = false;
            isRunning = true;
            while(isRunning) {
                if(Display.isCloseRequested()) {
                    break;
                }
                
                while(Keyboard.next()) {
                    if(Keyboard.getEventKey() == Keyboard.KEY_F) {
                        if(Keyboard.getEventKeyState()) {
                            displayManager.setFullScreen(!displayManager.isFullScreen());
                        }
                    }
                    if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
                        isRunning = false;
                    }
                    if(Keyboard.getEventKey() == Keyboard.KEY_C) {
                        calibrationRequested = true;
                    }
                }
                
                if(calibrationRequested) {
                    windowManager.calibrate();
                    calibrationRequested = false;
                    isCalibrated = false;
                } else if(isCalibrated) {
                    windowManager.setCursorCollection(new CursorCollectionDefault());
                    
                    log.info("Loading images...");
                    loadImages();
                    isCalibrated = false;
                }
                
                if(frameMeter.update()) {
                    displayManager.setWindowTitle(TITLE+" - "+frameMeter.getFps()+" fps");
                }
                updateControls();
                windowManager.update();
                Thread.sleep(30);
            }
        } catch(Exception exception) {
            exception.printStackTrace();
        } finally {
            try {
                if(windowManager != null) {
                    windowManager.destroy();
                }
                Keyboard.destroy();
                displayManager.destroy();
            } catch(Exception exception) {
                exception.printStackTrace();
            }
        }
    }
    
    private void loadImages() throws Exception {
        for(int i = 0; i < NUMBER_OF_CONTROLS; i++) {
            GrabbableControl grabbableControl = new GrabbableControl();
            grabbableControl.setTexture(getClass().getResource(URL_IMAGE_GRABBED));
            grabbableControl.setSize(new Size(150.0f, 150.0f));
            grabbableControl.setPosition(new Point(350.0f, 0.0f));
            grabbableControls.add(grabbableControl);
        }
    }
    
    private void updateControls() {
        for(GrabbableControl grabbableControl : grabbableControls) {
            if(!grabbableControl.isGrabbed()) {
                Point position = grabbableControl.getPosition();
                position.add(0.0f, 0.1f);
                grabbableControl.setPosition(position);
            }
        }
    }
    
    public static void main(String[] args) {
        LinkedList<String> arguments = new LinkedList<String>();
        for(String argument : args) {
            arguments.add(argument);
        }
        
        TreeMap<String, String> parameters = new TreeMap<String, String>();
        if(arguments.contains("-ix")) {
            parameters.put(ObjectObserverMoteJ.Parameter.InverseX.toString(), "");
        }
        if(arguments.contains("-iy")) {
            parameters.put(ObjectObserverMoteJ.Parameter.InverseY.toString(), "");
        }
        
        Chopsticks chopsticks = new Chopsticks();
        chopsticks.run(parameters);
    }
}
