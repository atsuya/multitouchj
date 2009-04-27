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
package edu.csun.ecs.cs.multitouchj.application.touchpong;

import java.awt.Color;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.vector.Vector2f;

import edu.csun.ecs.cs.multitouchj.objectobserver.ObjectObserver;
import edu.csun.ecs.cs.multitouchj.objectobserver.event.ObjectObserverEvent;
import edu.csun.ecs.cs.multitouchj.objectobserver.motej.ObjectObserverMoteJ;
import edu.csun.ecs.cs.multitouchj.objectobserver.mouse.ObjectObserverMouse;
import edu.csun.ecs.cs.multitouchj.ui.control.Control;
import edu.csun.ecs.cs.multitouchj.ui.control.FramedControl;
import edu.csun.ecs.cs.multitouchj.ui.control.VisualControl;
import edu.csun.ecs.cs.multitouchj.ui.event.TouchEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.TouchListener;
import edu.csun.ecs.cs.multitouchj.ui.event.WindowManagerCalibratorEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.WindowManagerCalibratorListener;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Point;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Size;
import edu.csun.ecs.cs.multitouchj.ui.graphic.CursorCollectionDefault;
import edu.csun.ecs.cs.multitouchj.ui.graphic.DisplayManager;
import edu.csun.ecs.cs.multitouchj.ui.graphic.WindowManager;
import edu.csun.ecs.cs.multitouchj.ui.utility.PointUtility;
import edu.csun.ecs.cs.multitouchj.utility.FrameMeter;

/**
 * @author Atsuya Takagi
 *
 * $Id$
 */
public class TouchPong
    implements WindowManagerCalibratorListener, TouchListener {
    private static final String TITLE = "Touch Pong";
    private static final String URL_IMAGE_WHITE_RECTANGLE =
        "/edu/csun/ecs/cs/multitouchj/application/touchpong/resource/WhiteRectangle.png";
    private static final String URL_IMAGE_MIDDLE_DOT =
        "/edu/csun/ecs/cs/multitouchj/application/touchpong/resource/MiddleDot.png";
    private static final int BAR_SIZE = 10;
    private static final int PADDLE_SIZE = 5;
    private static final int BALL_SIZE = 10;
    private static final float BALL_SPEED = 0.5f;
    private static final int NUMBER_OF_PADDLES = 2;
    private enum GameState {
        Ready,
        Start,
        Playing,
        End
    }
    private static Log log = LogFactory.getLog(TouchPong.class);
    private boolean isRunning;
    private boolean isCalibrated;
    private boolean calibrationRequested;
    private DisplayMode displayMode;
    private FramedControl backgroundControl;
    private VisualControl middleDotControl;
    private LinkedList<VisualControl> bounceableControls;
    private Hashtable<Integer, VisualControl> paddles;
    private VisualControl ball;
    private GameState gameState;
    private float ballAngle;
    
    
    public TouchPong() {
        bounceableControls = new LinkedList<VisualControl>();
        paddles = new Hashtable<Integer, VisualControl>();
        
        gameState = null;
    }
    
    /* (non-Javadoc)
     * @see edu.csun.ecs.cs.multitouchj.ui.event.WindowManagerCalibratorListener#calibrated(edu.csun.ecs.cs.multitouchj.ui.event.WindowManagerCalibratorEvent)
     */
    public void calibrated(WindowManagerCalibratorEvent event) {
        isCalibrated = true;
    }
    
    /* (non-Javadoc)
     * @see edu.csun.ecs.cs.multitouchj.ui.event.TouchListener#touchEnded(edu.csun.ecs.cs.multitouchj.ui.event.TouchEvent)
     */
    public void touchEnded(TouchEvent touchEvent) {
        handleTouchs(touchEvent, true);
    }

    /* (non-Javadoc)
     * @see edu.csun.ecs.cs.multitouchj.ui.event.TouchListener#touchMoved(edu.csun.ecs.cs.multitouchj.ui.event.TouchEvent)
     */
    public void touchMoved(TouchEvent touchEvent) {
        handleTouchs(touchEvent, false);
    }

    /* (non-Javadoc)
     * @see edu.csun.ecs.cs.multitouchj.ui.event.TouchListener#touchStarted(edu.csun.ecs.cs.multitouchj.ui.event.TouchEvent)
     */
    public void touchStarted(TouchEvent touchEvent) {
        handleTouchs(touchEvent, false);
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
                    
                    gameState = GameState.Ready;
                }
                
                if(frameMeter.update()) {
                    displayManager.setWindowTitle(TITLE+" - "+frameMeter.getFps()+" fps");
                }
                
                // game state
                if(gameState != null) {
                    if(gameState.equals(GameState.Ready)) {
                        onGameStateReady();
                    } else if(gameState.equals(GameState.Start)) {
                        onGameStateStart();
                    } else if(gameState.equals(GameState.Playing)) {
                        onGameStatePlaying();
                    }
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
        // background
        backgroundControl = new FramedControl();
        backgroundControl.setColor(Color.BLACK);
        backgroundControl.setOpacity(0.5f);
        backgroundControl.setSize(new Size(displayMode.getWidth(), displayMode.getHeight()));
        backgroundControl.setTopLeftPosition(new Point(0, 0));
        WindowManager.getInstance().setBackgroundControl(backgroundControl);
        
        // middle dot
        middleDotControl = new VisualControl();
        middleDotControl.setTexture(getClass().getResource(URL_IMAGE_MIDDLE_DOT));
        Size middleDotControlSize = middleDotControl.getSize();
        middleDotControl.setSize(new Size(middleDotControlSize.getWidth(), displayMode.getHeight()));
        middleDotControl.setTopLeftPosition(new Point(
            ((displayMode.getWidth() / 2.0f) - (middleDotControlSize.getWidth() / 2.0f)),
            0
        ));
        
        // top bar
        VisualControl topBar = new VisualControl();
        topBar.setTexture(getClass().getResource(URL_IMAGE_WHITE_RECTANGLE));
        topBar.setSize(new Size(displayMode.getWidth(), BAR_SIZE));
        topBar.setTopLeftPosition(new Point(0, 0));
        bounceableControls.add(topBar);
        
        // bottom bar
        VisualControl bottomBar = new VisualControl();
        bottomBar.setTexture(getClass().getResource(URL_IMAGE_WHITE_RECTANGLE));
        bottomBar.setSize(new Size(displayMode.getWidth(), BAR_SIZE));
        bottomBar.setTopLeftPosition(new Point(0, (displayMode.getHeight() - BAR_SIZE)));
        bounceableControls.add(bottomBar);
        
        // paddles
        for(int i = 0; i < NUMBER_OF_PADDLES; i++) {
            VisualControl paddle = new VisualControl();
            paddle.setTexture(getClass().getResource(URL_IMAGE_WHITE_RECTANGLE));
            paddle.setVisible(false);
            paddles.put(i, paddle);
        }
        
        // ball
        ball = new VisualControl();
        //ball.setColor(Color.BLACK);
        ball.setTexture(getClass().getResource(URL_IMAGE_WHITE_RECTANGLE));
        ball.setSize(new Size(BALL_SIZE, BALL_SIZE));
        //ball.setMargin(1.0f);
        ball.setVisible(false);
        
        // listeners
        backgroundControl.addTouchListener(this);
    }
    
    private void handleTouchs(TouchEvent touchEvent, boolean ended) {
        log.debug("handleTouches: "+touchEvent.getObjectObserverEvents().size()+", "+ended);
        
        if(ended) {
            for(FramedControl paddle : paddles.values()) {
                paddle.setVisible(false);
            }
        } else { 
            List<ObjectObserverEvent> ooes = touchEvent.getObjectObserverEvents();
            
            // try finding the pair
            for(int i = 0; i < NUMBER_OF_PADDLES; i++) {
                int idA = (i * 2);
                int idB = (i * 2) + 1;
                ObjectObserverEvent ooeA = null;
                ObjectObserverEvent ooeB = null;
                for(ObjectObserverEvent ooe : ooes) {
                    if(ooe.getId() == idA) {
                        ooeA = ooe;
                    } else if(ooe.getId() == idB) {
                        ooeB = ooe;
                    }
                }
                
                FramedControl paddle = paddles.get(i);
                if((ooeA != null) && (ooeB != null)) {
                    Point pointA = new Point(ooeA.getX(), ooeA.getY());
                    Point pointB = new Point(ooeB.getX(), ooeB.getY());
                    float distance = PointUtility.getDistance(pointA, pointB);
                    float angle = PointUtility.getAngle(pointA, pointB);
                    Point center = new Point(
                        (pointA.getX() + ((pointB.getX() - pointA.getX()) / 2.0f)),
                        (pointA.getY() + ((pointB.getY() - pointA.getY()) / 2.0f))
                    );
                    
                    //log.debug("center: "+center.toString());
                    //log.debug("angle: "+angle);
                    //log.debug("distance: "+distance);
                    
                    paddle.setVisible(true);
                    paddle.setSize(new Size(distance, PADDLE_SIZE));
                    paddle.setPosition(center);
                    paddle.setRotation(angle);
                } else {
                    paddle.setVisible(false);
                }
            }
        }
    }
    
    private void onGameStateReady() {
        ball.setVisible(true);
        ball.setPosition(new Point((displayMode.getWidth() / 2.0f), (displayMode.getHeight() / 2.0f)));
        
        Random random = new Random(new Date().getTime());
        //ballAngle = random.nextInt(360);
        ballAngle = 170.0f;
        
        gameState = GameState.Start;
    }
    
    private void onGameStateStart() {
        gameState = GameState.Playing;
    }
    
    private void onGameStatePlaying() {
        Point position = ball.getPosition();
        float deltaX = BALL_SPEED * (float)Math.cos(Math.toRadians(ballAngle));
        float deltaY = BALL_SPEED * (float)Math.sin(Math.toRadians(ballAngle));
        position.add(deltaX, deltaY);
        ball.setPosition(position);
        
        // bars
        for(VisualControl visualControl : bounceableControls) {
            
        }
    }
    
    private void bounceBall(Control control) {
        
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
        
        TouchPong touchPong = new TouchPong();
        touchPong.run(parameters);
    }
}
