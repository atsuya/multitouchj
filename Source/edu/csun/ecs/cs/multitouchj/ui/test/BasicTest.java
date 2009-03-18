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
import java.net.URI;
import java.net.URL;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import edu.csun.ecs.cs.multitouchj.objectobserver.ObjectObserver;
import edu.csun.ecs.cs.multitouchj.objectobserver.event.ObjectObserverEvent;
import edu.csun.ecs.cs.multitouchj.objectobserver.event.ObjectObserverListener;
import edu.csun.ecs.cs.multitouchj.objectobserver.motej.ObjectObserverMoteJ;
import edu.csun.ecs.cs.multitouchj.objectobserver.mouse.ObjectObserverMouse;
import edu.csun.ecs.cs.multitouchj.ui.control.TexturedControl;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Point;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Size;
import edu.csun.ecs.cs.multitouchj.ui.graphic.DisplayManager;
import edu.csun.ecs.cs.multitouchj.ui.graphic.WindowManager;
import edu.csun.ecs.cs.multitouchj.ui.graphic.WindowManagerCalibratorDefault;

/**
 * @author Atsuya Takagi
 *
 * $Id: BasicTest.java 68 2009-02-13 10:34:36Z Atsuya Takagi $
 */
public class BasicTest implements ObjectObserverListener {
    private Log log = LogFactory.getLog(BasicTest.class);
    private boolean isRunning;
    
    
    @Before
    public void setUp() {
    }
    
    @After
    public void cleanUp() {
    }
    
    @Test
    public void test() {
        DisplayManager.create();
        DisplayManager displayManager = DisplayManager.getInstance();
        ObjectObserver objectObserver = new ObjectObserverMouse();
        WindowManager windowManager = null;

        try {
            DisplayMode displayMode = displayManager.getDisplayMode(800, 600, 32);
            displayManager.createDisplay(displayMode);
            Keyboard.create();
            
            TreeMap<String, String> parameters = new TreeMap<String, String>();
            objectObserver.initialize(parameters);
            objectObserver.addObjectObserverListener(this);
            
            WindowManager.create(displayManager, objectObserver);
            windowManager = WindowManager.getInstance();
            
            TexturedControl panel = new TexturedControl();
            panel.setColor(Color.WHITE);
            panel.setPosition(new Point(200.0f, 100.0f));
            panel.setSize(new Size(400.0f, 200.0f));
            //panel.setTexture(new URL("file:///Users/axt/Documents/Development/Eclipse/MultiTouchJ/Resources/Machine.png"));
            panel.setTexture(new URL("file:///Users/atsuya/Documents/Development/Eclipse/MultiTouchJ/Resources/Syougi.jpg"));
            panel.setOpacity(0.8f);
            panel.setRotation(60.0f);
            
            windowManager.calibrate();
            
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

    /* (non-Javadoc)
     * @see edu.csun.ecs.cs.multitouchj.objectobserver.event.ObjectObserverListener#objectObserved(edu.csun.ecs.cs.multitouchj.objectobserver.event.ObjectObserverEvent)
     */
    public void objectObserved(ObjectObserverEvent event) {
        //log.info("objectObserved called.");
        //log.info(event.toString());
    }

    /* (non-Javadoc)
     * @see edu.csun.ecs.cs.multitouchj.objectobserver.event.ObjectObserverListener#objectTouched(edu.csun.ecs.cs.multitouchj.objectobserver.event.ObjectObserverEvent)
     */
    public void objectTouched(ObjectObserverEvent event) {
        //log.info("objectTouched called.");
        //log.info(event.toString());
        
        /*
        if((event.getX() < 50.0f) && (event.getY() < 50.0f)) {
            isRunning = false;
        }
        */
    }
}
