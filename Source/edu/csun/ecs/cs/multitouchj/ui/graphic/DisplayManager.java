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
package edu.csun.ecs.cs.multitouchj.ui.graphic;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

/**
 * @author Atsuya Takagi
 * 
 *         $Id: DisplayManager.java 54 2009-02-09 20:30:44Z Atsuya Takagi $
 */
public class DisplayManager {
    private static DisplayManager instance = null;
    
    
    protected DisplayManager() {
    }
    
    public static DisplayManager create() {
        if(instance == null) {
            instance = new DisplayManager();
        }
        
        return instance;
    }
    
    public static DisplayManager getInstance() {
        return instance;
    }
    
    public DisplayMode[] getAvailableDisplayModes() {
        try {
            return Display.getAvailableDisplayModes();
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public void createDisplay(DisplayMode displayMode) throws Exception {
        if(isCreate()) {
            throw new Exception("Display has already been created.");
        }
        
        if(!setDisplayMode(displayMode)) {
            throw new Exception("Failed not set DisplayMode.");
        }
        
        // Display.setFullscreen(false);
        Display.setVSyncEnabled(false);
        Display.create();
    }
    
    public void destroy() {
        Display.destroy();
    }
    
    public boolean update() {
        try {
            Display.update();
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean isCreate() {
        return Display.isCreated();
    }
    
    public boolean setFullScreen(boolean fullScreen) {
        try {
            Display.setFullscreen(fullScreen);
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean setDisplayMode(DisplayMode displayMode) {
        try {
            Display.setDisplayMode(displayMode);
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean isFullScreen() {
        return Display.isFullscreen();
    }
    
    public DisplayMode getCurrentDisplayMode() {
        return Display.getDisplayMode();
    }
    
    public boolean setWindowTitle(String title) {
        Display.setTitle(title);
        return true;
    }
    
    public DisplayMode getDisplayMode(int width, int height, int bitsPerPixel) {
        DisplayMode[] displayModes = getAvailableDisplayModes();
        if(displayModes == null) {
            return null;
        }
        
        DisplayMode displayMode = null;
        for(int i = 0; i < displayModes.length; i++) {
            if((displayModes[i].getWidth() == width)
                    && (displayModes[i].getHeight() == height)
                    && (displayModes[i].getBitsPerPixel() == bitsPerPixel)) {
                displayMode = displayModes[i];
                break;
            }
        }
        
        return displayMode;
    }
    
    public DisplayMode getBestFitDisplayMode(int width, int height,
            int bitsPerPixel) {
        DisplayMode displayMode;
        
        displayMode = getDisplayMode(width, height, bitsPerPixel);
        if(displayMode != null) {
            return displayMode;
        }
        
        // try look at all available displaymodes and see which one is the best.
        DisplayMode[] displayModes = getAvailableDisplayModes();
        if(displayModes == null) {
            return null;
        }
        
        int bestScore = 0;
        displayMode = null;
        for(int i = 0; i < displayModes.length; i++) {
            int score = Math.abs((displayModes[i].getWidth() - width));
            score += Math.abs((displayModes[i].getHeight() - height));
            score += Math
                    .abs((displayModes[i].getBitsPerPixel() - bitsPerPixel));
            
            if((displayMode == null) || (score < bestScore)) {
                displayMode = displayModes[i];
                bestScore = score;
            }
        }
        
        return displayMode;
    }
}
