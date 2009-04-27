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
package edu.csun.ecs.cs.multitouchj.application.whiteboard;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import edu.csun.ecs.cs.multitouchj.application.whiteboard.ui.InteractiveCanvas;
import edu.csun.ecs.cs.multitouchj.application.whiteboard.ui.Pen;
import edu.csun.ecs.cs.multitouchj.objectobserver.ObjectObserver;
import edu.csun.ecs.cs.multitouchj.objectobserver.motej.ObjectObserverMoteJ;
import edu.csun.ecs.cs.multitouchj.objectobserver.mouse.ObjectObserverMouse;
import edu.csun.ecs.cs.multitouchj.ui.control.Canvas;
import edu.csun.ecs.cs.multitouchj.ui.control.FramedControl;
import edu.csun.ecs.cs.multitouchj.ui.control.TexturedControl;
import edu.csun.ecs.cs.multitouchj.ui.event.TouchEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.TouchListener;
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
 * $Id: Whiteboard.java 85 2009-03-16 07:04:24Z Atsuya Takagi $
 */
public class Whiteboard implements WindowManagerCalibratorListener {
    private static final String TITLE = "Whiteboard";
    private static Log log = LogFactory.getLog(Whiteboard.class);
    private static final Object[][] BUTTONS = {
        {
            "/edu/csun/ecs/cs/multitouchj/application/whiteboard/resource/Button-Off-Red.png",
            new BasicStroke(5.0f),
            Color.RED
        },
        {
            "/edu/csun/ecs/cs/multitouchj/application/whiteboard/resource/Button-Off-Orange.png",
            new BasicStroke(5.0f),
            Color.ORANGE
        },
        {
            "/edu/csun/ecs/cs/multitouchj/application/whiteboard/resource/Button-Off-Yellow.png",
            new BasicStroke(5.0f),
            Color.YELLOW
        },
        {
            "/edu/csun/ecs/cs/multitouchj/application/whiteboard/resource/Button-Off-Green.png",
            new BasicStroke(5.0f),
            Color.GREEN
        },
        {
            "/edu/csun/ecs/cs/multitouchj/application/whiteboard/resource/Button-Off-Blue.png",
            new BasicStroke(5.0f),
            Color.BLUE
        },
        {
            "/edu/csun/ecs/cs/multitouchj/application/whiteboard/resource/Button-Off-Indigo.png",
            new BasicStroke(5.0f),
            new Color(75, 0, 130)
        },
        {
            "/edu/csun/ecs/cs/multitouchj/application/whiteboard/resource/Button-Off-Violet.png",
            new BasicStroke(5.0f),
            new Color(238, 130, 238)
        }
    };
    private static final int NUMBER_OF_PENS = 4;
    private static final Color DEFAULT_COLOR = Color.BLACK;
    private boolean isRunning;
    private boolean isCalibrated;
    private boolean calibrationRequested;
    private DisplayMode displayMode;
    private InteractiveCanvas interactiveCanvas;
    
    
    public Whiteboard() {
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
        interactiveCanvas = new InteractiveCanvas();
        WindowManager.getInstance().setBackgroundControl(interactiveCanvas);
        interactiveCanvas.setSize(new Size(displayMode.getWidth(), displayMode.getHeight()));
        interactiveCanvas.setTopLeftPosition(new Point(0.0f, 0.0f));
        
        // pens
        updatePens(new BasicStroke(5.0f), Color.BLACK);
        
        // buttons
        int totalButtonsWidth = 0;
        LinkedList<TexturedControl> buttons = new LinkedList<TexturedControl>();
        for(Object[] button : BUTTONS) {
            final Stroke stroke = (Stroke)button[1];
            final Color color = (Color)button[2];
            
            TexturedControl texturedControl = new TexturedControl();
            texturedControl.setTexture(getClass().getResource((String)button[0]));
            texturedControl.addTouchListener(new TouchListener(){
                public void touchEnded(TouchEvent touchEvent) {
                }

                public void touchMoved(TouchEvent touchEvent) {
                }

                public void touchStarted(TouchEvent touchEvent) {
                    updatePens(stroke, color);
                }
            });
            buttons.add(texturedControl);
            
            totalButtonsWidth += texturedControl.getSize().getWidth();
        }
        
        int numberOfPaddings = BUTTONS.length + 1;
        int padding = (int)Math.floor(
            ((displayMode.getWidth() - totalButtonsWidth) / (double)numberOfPaddings)
        );
        for(int i = 0; i < buttons.size(); i++) {
            TexturedControl texturedControl = buttons.get(i);
            texturedControl.setTopLeftPosition(new Point(
                (((i + 1) * padding) + (i * texturedControl.getSize().getWidth())),
                15
            ));
        }
    }
    
    private void updatePens(Stroke stroke, Color color) {
        log.debug("updatePends");
        
        for(int i = 0; i < NUMBER_OF_PENS; i++) {
            Pen pen = new Pen();
            pen.setStroke(stroke);
            pen.setPaint(color);
            
            interactiveCanvas.setPen(i, pen);
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
        
        Whiteboard whiteboard = new Whiteboard();
        whiteboard.run(parameters);
    }
}
