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
package edu.csun.ecs.cs.multitouchj.ui.test;

import java.awt.Color;
import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import edu.csun.ecs.cs.multitouchj.objectobserver.ObjectObserver;
import edu.csun.ecs.cs.multitouchj.objectobserver.motej.ObjectObserverMoteJ;
import edu.csun.ecs.cs.multitouchj.objectobserver.mouse.ObjectObserverMouse;
import edu.csun.ecs.cs.multitouchj.ui.control.FramedControl;
import edu.csun.ecs.cs.multitouchj.ui.control.TexturedControl;
import edu.csun.ecs.cs.multitouchj.ui.control.TouchableControl;
import edu.csun.ecs.cs.multitouchj.ui.event.WindowManagerCalibratorEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.WindowManagerCalibratorListener;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Point;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Size;
import edu.csun.ecs.cs.multitouchj.ui.graphic.CursorCollectionDefault;
import edu.csun.ecs.cs.multitouchj.ui.graphic.DisplayManager;
import edu.csun.ecs.cs.multitouchj.ui.graphic.WindowManager;
import edu.csun.ecs.cs.multitouchj.ui.graphic.image.Texture;
import edu.csun.ecs.cs.multitouchj.utility.FrameMeter;

/**
 * @author Atsuya Takagi
 *
 * $Id: PhotoTest.java 80 2009-03-15 07:21:37Z Atsuya Takagi $
 */
public class PhotoTest implements WindowManagerCalibratorListener {
    private static final String IMAGE_DIRECTORY = "../Resources/PhotoTest";
    private static final long ANIMATION_TIME = 1000;
    private static Log log = LogFactory.getLog(PhotoTest.class);
    private boolean isRunning;
    private boolean isCalibrated;
    private boolean calibrationRequested;
    private DisplayMode displayMode;
    private LinkedList<TouchableControl> touchableControls;
    private Hashtable<TouchableControl, Point> desiredPositions;
    private Hashtable<TouchableControl, Float> desiredAngles;
    private Hashtable<TouchableControl, Size> desiredSizes;
    private long animationTimeStarted;
    private boolean animationStart;
    
    
    public PhotoTest() {
        touchableControls = new LinkedList<TouchableControl>();
        desiredPositions = new Hashtable<TouchableControl, Point>();
        desiredAngles = new Hashtable<TouchableControl, Float>();
        desiredSizes = new Hashtable<TouchableControl, Size>();
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
            
            /*
            panel.setColor(Color.WHITE);
            panel.setPosition(new Point(200.0f, 100.0f));
            panel.setSize(new Size(400.0f, 200.0f));
            //panel.setTexture(new URL("file:///Users/axt/Documents/Development/Eclipse/MultiTouchJ/Resources/Machine.png"));
            panel.setTexture(new URL("file:///Users/atsuya/Documents/Development/Eclipse/MultiTouchJ/Resources/Syougi.jpg"));
            panel.setSize(new Size(400.0f, 200.0f));
            panel.setOpacity(0.8f);
            panel.setRotation(-370.0f);
            */
            
            FrameMeter frameMeter = new FrameMeter();
            calibrationRequested = true;
            isCalibrated = false;
            isRunning = true;
            animationStart = false;
            while(isRunning) {
                if(Display.isCloseRequested()) {
                    break;
                }
                
                while(Keyboard.next()) {
                    switch(Keyboard.getEventKey()) {
                        case Keyboard.KEY_F:
                            if(Keyboard.getEventKeyState()) {
                                displayManager.setFullScreen(!displayManager.isFullScreen());
                            }
                            break;
                        case Keyboard.KEY_ESCAPE:
                            isRunning = false;
                            break;
                        case Keyboard.KEY_C:
                            calibrationRequested = true;
                            break;
                        case Keyboard.KEY_I:
                            //resetPhotos();
                            initiateAnimation();
                            break;
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
                    
                    initiateAnimation();
                }
                
                if(animationStart) {
                    animateControls();
                }
                
                if(frameMeter.update()) {
                    displayManager.setWindowTitle(frameMeter.getFps()+" fps");
                }
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
        File imageDirectory = new File(IMAGE_DIRECTORY);
        for(File file : imageDirectory.listFiles()) {
            if(file.getName().endsWith(".jpg")) {
                TouchableControl panel = new TouchableControl();
                panel.setTexture(new URL("file://"+file.getAbsolutePath()));
                panel.setMargin(10.0f);
                touchableControls.add(panel);
            }
        }
    }
    
    private void animateControls() {
        long time = new Date().getTime();
        long timeElapsed = (time - animationTimeStarted);
        if(timeElapsed >= ANIMATION_TIME) {
            animationStart = false;
        } else {
            for(TouchableControl touchableControl : touchableControls) {
                float ratio = (timeElapsed / (float)ANIMATION_TIME);
                
                Point position = desiredPositions.get(touchableControl);
                Size size = desiredSizes.get(touchableControl);
                float angle = desiredAngles.get(touchableControl);
                touchableControl.setPosition(new Point((position.getX() * ratio), (position.getY() * ratio)));
                touchableControl.setSize(new Size((size.getWidth() * ratio), (size.getHeight() * ratio)));
                touchableControl.setRotation((angle * ratio));
            }
        }
    }
    
    private void initiateAnimation() {
        desiredPositions.clear();
        desiredAngles.clear();
        desiredSizes.clear();
        
        Random xRandom = new Random();
        Random yRandom = new Random();
        Random angleRandom = new Random();
        for(TouchableControl touchableControl : touchableControls) {
            float x = xRandom.nextInt(displayMode.getWidth() - 30);
            float y = yRandom.nextInt(displayMode.getHeight() - 30);
            Point position = new Point(x, y);
            
            Texture texture = touchableControl.getTexture();
            Size size = new Size((texture.getImage().getWidth() / 2.0f), (texture.getImage().getHeight() / 2.0f));
            
            float angle = (float)angleRandom.nextInt(360);
            
            desiredPositions.put(touchableControl, position);
            desiredAngles.put(touchableControl, angle);
            desiredSizes.put(touchableControl, size);
        }
        
        animationTimeStarted = new Date().getTime();
        animationStart = true;
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
        
        PhotoTest photoTest = new PhotoTest();
        photoTest.run(parameters);
    }
}
