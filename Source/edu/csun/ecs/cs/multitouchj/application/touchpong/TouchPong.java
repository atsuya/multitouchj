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

import edu.csun.ecs.cs.multitouchj.application.touchpong.ui.BounceableControl;
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
    private static final String URL_IMAGE_WON =
        "/edu/csun/ecs/cs/multitouchj/application/touchpong/resource/Won.png";
    private static final String URL_IMAGE_LOST =
        "/edu/csun/ecs/cs/multitouchj/application/touchpong/resource/Lost.png";
    private static final int BAR_SIZE = 10;
    private static final int PADDLE_SIZE = 10;
    private static final int BALL_SIZE = 10;
    private static final float BALL_SPEED = 2.0f;
    private static final int NUMBER_OF_PADDLES = 2;
    private static final int NUMBER_OF_BALL_SHADOWS = 5;
    private static final int SHADOW_SKIP = 3;
    private enum GameState {
        Ready,
        Start,
        Playing,
        End
    }
    private static Log log = LogFactory.getLog(TouchPong.class);
    private float ballSpeed = BALL_SPEED;
    private boolean isRunning;
    private boolean isCalibrated;
    private boolean calibrationRequested;
    private DisplayMode displayMode;
    private FramedControl backgroundControl;
    private VisualControl middleDotControl;
    private LinkedList<BounceableControl> bounceableControls;
    private Hashtable<Integer, BounceableControl> paddles;
    private VisualControl ball;
    private GameState gameState;
    private Vector2f ballUnitVector;
    private BounceableControl bouncedControl;
    private LinkedList<Point> previousBallPositions;
    private LinkedList<VisualControl> ballShadows;
    private int shadowCount;
    private VisualControl wonControl;
    private VisualControl lostControl;
    
    
    public TouchPong() {
        bounceableControls = new LinkedList<BounceableControl>();
        paddles = new Hashtable<Integer, BounceableControl>();
        
        gameState = null;
        ballUnitVector = new Vector2f();
        previousBallPositions = new LinkedList<Point>();
        ballShadows = new LinkedList<VisualControl>();
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
                    if(Keyboard.getEventKey() == Keyboard.KEY_UP) {
                        ballSpeed += 0.1f;
                    }
                    if(Keyboard.getEventKey() == Keyboard.KEY_DOWN) {
                        ballSpeed -= 0.1f;
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
                    } else if(gameState.equals(GameState.End)) {
                        onGameStateEnd();
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
        BounceableControl topBar = new BounceableControl();
        topBar.setTexture(getClass().getResource(URL_IMAGE_WHITE_RECTANGLE));
        topBar.setSize(new Size(displayMode.getWidth(), BAR_SIZE));
        topBar.setTopLeftPosition(new Point(0, 0));
        bounceableControls.add(topBar);
        
        // bottom bar
        BounceableControl bottomBar = new BounceableControl();
        bottomBar.setTexture(getClass().getResource(URL_IMAGE_WHITE_RECTANGLE));
        bottomBar.setSize(new Size(displayMode.getWidth(), BAR_SIZE));
        bottomBar.setTopLeftPosition(new Point(0, (displayMode.getHeight() - BAR_SIZE)));
        bounceableControls.add(bottomBar);
        
        // win/lose
        wonControl = new VisualControl();
        //wonControl.setColor(Color.BLACK);
        wonControl.setTexture(getClass().getResource(URL_IMAGE_WON));
        wonControl.setVisible(false);
        lostControl = new VisualControl();
        //lostControl.setColor(Color.BLACK);
        lostControl.setTexture(getClass().getResource(URL_IMAGE_LOST));
        lostControl.setVisible(false);
        
        // paddles
        for(int i = 0; i < NUMBER_OF_PADDLES; i++) {
            BounceableControl paddle = new BounceableControl();
            paddle.setTexture(getClass().getResource(URL_IMAGE_WHITE_RECTANGLE));
            paddle.setVisible(false);
            paddles.put(i, paddle);
        }
        
        // ball shadows
        for(int i = 0; i < NUMBER_OF_BALL_SHADOWS; i++) {
            float opacity = (1.0f - ((1.0f / (float)(NUMBER_OF_BALL_SHADOWS + 1)) * (i + 1)));
            float size = (BALL_SIZE * (NUMBER_OF_BALL_SHADOWS / (float)(NUMBER_OF_BALL_SHADOWS + (i + 1)))); 
            log.debug("opacity: "+opacity+", size: "+size);
            
            VisualControl visualControl = new VisualControl();
            visualControl.setTexture(getClass().getResource(URL_IMAGE_WHITE_RECTANGLE));
            visualControl.setSize(new Size(size, size));
            visualControl.setOpacity(opacity);
            visualControl.setVisible(false);
            ballShadows.addLast(visualControl);
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
        if(GameState.Ready.equals(gameState)) {
            gameState = GameState.Start;
            return;
        }
        if(!GameState.Playing.equals(gameState)) {
            return;
        }
        
        //log.debug("handleTouches: "+touchEvent.getObjectObserverEvents().size()+", "+ended);
        if(ended) {
            for(BounceableControl paddle : paddles.values()) {
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
                
                BounceableControl paddle = paddles.get(i);
                if((ooeA != null) && (ooeB != null)) {
                    Point pointA = new Point(ooeA.getX(), ooeA.getY());
                    Point pointB = new Point(ooeB.getX(), ooeB.getY());
                    float distance = PointUtility.getDistance(pointA, pointB);
                    float angle = PointUtility.getAngle(pointA, pointB, false);
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
        
        bouncedControl = null;
        shadowCount = 0;
        ballSpeed = BALL_SPEED;
        previousBallPositions.clear();
        //gameState = GameState.Start;
        
        ball.setVisible(false);
        for(VisualControl visualControl : ballShadows) {
            visualControl.setVisible(false);
        }
    }
    
    private void onGameStateStart() {
        wonControl.setVisible(false);
        lostControl.setVisible(false);
        
        Random random = new Random(new Date().getTime());
        float ballAngle = (float)random.nextInt(360);
        float ballX = (float)Math.cos(Math.toRadians(ballAngle));
        float ballY = (float)Math.sin(Math.toRadians(ballAngle));
        if(ballX == 0.0f) {
            ballX = 0.5f;
        }
        if(ballY == 0.0f) {
            ballY = 0.5f;
        }
        
        log.debug("angle: "+ballAngle+", x: "+ballX+", y: "+ballY);
        ballUnitVector.set(ballX, ballY);
        ballUnitVector = ballUnitVector.normalise(null);
        
        gameState = GameState.Playing;
    }
    
    private void onGameStatePlaying() {
        // ball
        Point position = ball.getPosition();
        if(shadowCount >= SHADOW_SKIP) {
            if(previousBallPositions.size() >= NUMBER_OF_BALL_SHADOWS) {
                previousBallPositions.removeLast();
            }
            previousBallPositions.addFirst(new Point(position));
            shadowCount = 0;
        } else {
            shadowCount += 1;
        }
        position.add((ballSpeed * ballUnitVector.getX()), -(ballSpeed * ballUnitVector.getY()));
        ball.setPosition(position);
        ball.setVisible(true);
        
        for(int i = 0; i < NUMBER_OF_BALL_SHADOWS; i++) {
            VisualControl visualControl = ballShadows.get(i);
            if(previousBallPositions.size() > i) {
                visualControl.setPosition(previousBallPositions.get(i));
                visualControl.setVisible(true);
            } else {
                visualControl.setVisible(false);
            }
        }
        
        // bounce
        if(bouncedControl != null) {
            if(!bouncedControl.isHit(ball.getPosition())) {
                bouncedControl = null;
            }
        } else {
            for(BounceableControl bounceableControl : paddles.values()) {
                bounceableControls.add(bounceableControl);
            }
            
            for(BounceableControl bounceableControl : bounceableControls) {
                if(bounceBall(bounceableControl)) {
                    bouncedControl = bounceableControl;
                    break;
                }
            }
            
            for(BounceableControl bounceableControl : paddles.values()) {
                bounceableControls.remove(bounceableControl);
            }
        }
        
        // goal
        if((position.getX() <= 0.0f) || (position.getX() >= (float)displayMode.getWidth())) {
            gameState = GameState.End;
        }
    }
    
    private void onGameStateEnd() {
        Point position = ball.getPosition();
        if(position.getX() <= 0.0f) {
            wonControl.setPosition(new Point((displayMode.getWidth() * (3.0f / 4.0f)), (displayMode.getHeight() / 2.0f)));
            lostControl.setPosition(new Point((displayMode.getWidth() / 4.0f), (displayMode.getHeight() / 2.0f)));
        } else {
            wonControl.setPosition(new Point((displayMode.getWidth() / 4.0f), (displayMode.getHeight() / 2.0f)));
            lostControl.setPosition(new Point((displayMode.getWidth() * (3.0f / 4.0f)), (displayMode.getHeight() / 2.0f)));
        }
        wonControl.setVisible(true);
        lostControl.setVisible(true);
        
        gameState = GameState.Ready;
    }
    
    private boolean bounceBall(BounceableControl control) {
        /*
        log.debug("Cheking...");
        log.debug("\tcontrol: "+control.getPosition().toString()+", ball: "+ball.getPosition().toString());
        log.debug("\tvector: x="+ballUnitVector.getX()+", y="+ballUnitVector.getY());
        */
        
        boolean isHit = false;
        if((control.isVisible()) && (control.isHit(ball.getPosition()))) {
            log.debug("bounced!!!!!!!!!!!!!!");
            
            float angleDelta = 90.0f - control.getRotation();
            
            // get current angle of ball vector
            Point previousBallPosition = new Point(0.0f, 0.0f);
            Point currentBallPosition = new Point(ballUnitVector.getX(), ballUnitVector.getY());
            //float ballVectorMagnitude = PointUtility.getDistance(previousBallPosition, currentBallPosition);
            float currentBallAngle = PointUtility.getAngle(previousBallPosition, currentBallPosition);
            log.debug("\t\tcurrentBallAngle="+currentBallAngle);
            
            // get new vector
            float targetBallAngle = (currentBallAngle + angleDelta);
            float newX = (float)Math.cos(Math.toRadians(targetBallAngle));
            float newY = (float)Math.sin(Math.toRadians(targetBallAngle));
            log.debug("\t\tnew vector: x="+newX+", y="+newY);
            newX = (-1 * newX);
            
            // back to original angle
            previousBallPosition.set(0.0f, 0.0f);
            currentBallPosition.set(newX, newY);
            log.debug("\t\tnewX="+newX+", newY="+newY);
            float newAngle = PointUtility.getAngle(previousBallPosition, currentBallPosition);
            targetBallAngle = (newAngle - angleDelta);
            log.debug("\t\tnew angle="+newAngle+", targetBallAngle="+targetBallAngle);
            
            newX = (float)Math.cos(Math.toRadians(targetBallAngle));
            newY = (float)Math.sin(Math.toRadians(targetBallAngle));
            ballUnitVector.set(newX, newY);
            ballUnitVector = ballUnitVector.normalise(null);
            log.debug("\t\tfinal: x="+ballUnitVector.getX()+", y="+ballUnitVector.getY());
            
            isHit = true;
        }
        
        return isHit;
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
