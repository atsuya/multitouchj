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
package edu.csun.ecs.cs.multitouchj.objectobserver.mouse;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.DisplayMode;

import edu.csun.ecs.cs.multitouchj.objectobserver.ObjectObserver;
import edu.csun.ecs.cs.multitouchj.ui.graphic.DisplayManager;

/**
 * @author Atsuya Takagi
 *
 * $Id: ObjectObserverMouse.java 58 2009-02-11 01:38:58Z Atsuya Takagi $
 */
public class ObjectObserverMouse extends ObjectObserver implements Runnable {
    private static Log log = LogFactory.getLog(ObjectObserverMouse.class);
    private Thread thread;
    private boolean stopThread;
    private DisplayManager displayManager;
    
    
    /* (non-Javadoc)
     * @see edu.csun.ecs.cs.multitouchj.objectobserver.ObjectObserver#initialize(java.util.Map)
     */
    @Override
    public void initialize(Map<String, String> parameters) throws Exception {
    }

    /* (non-Javadoc)
     * @see edu.csun.ecs.cs.multitouchj.objectobserver.ObjectObserver#start()
     */
    @Override
    public void start() throws Exception {
        if(!isStarted()) {
            displayManager = DisplayManager.getInstance();
            
            log.info("Creating keyboard.");
            Keyboard.create();
            
            log.info("ObjectObserverMouse started.");
            stopThread = false;
            thread = new Thread(this);
            thread.start();
            
            setStarted(true);
        }
    }

    /* (non-Javadoc)
     * @see edu.csun.ecs.cs.multitouchj.objectobserver.ObjectObserver#stop()
     */
    @Override
    public void stop() throws Exception {
        if(isStarted()) {
            log.info("ObjectObserverMouse stopping...");
            stopThread = true;
            thread.join();
            
            log.info("Destroying keyboard.");
            Keyboard.destroy();
            
            setStarted(false);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        while(!stopThread) {
            while(Mouse.next()) {
                DisplayMode displayMode = displayManager.getCurrentDisplayMode();
                
                int id = 0;
                float x = (float)Mouse.getX();
                float y = ((float)displayMode.getHeight() - (float)Mouse.getY());
                float size = 1.0f;
                
                if(Mouse.isButtonDown(0)) {
                    objectTouched(id, x, y, size);
                } else {
                    objectObserved(id, x, y, size);
                }
            }
            
            try {
                Thread.sleep(50);
            } catch(Exception exception) {}
        }
    }
}
