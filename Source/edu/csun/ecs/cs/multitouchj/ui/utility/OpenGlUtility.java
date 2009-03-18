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
package edu.csun.ecs.cs.multitouchj.ui.utility;

import org.lwjgl.opengl.GL11;

import edu.csun.ecs.cs.multitouchj.ui.geometry.Point;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Size;

/**
 * @author Atsuya Takagi
 * 
 *         $Id: OpenGlUtility.java 68 2009-02-13 10:34:36Z Atsuya Takagi $
 */
public class OpenGlUtility {
    protected OpenGlUtility() {
    }
    
    public static void orthoMode(Size size) {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0, size.getWidth(), 0, size.getHeight(), -10, 10);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
    }
    
    public static void perspectiveMode() {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }
    
    public static Point getOpenGlPosition(Size displaySize, Point position)
    {
        return new Point(
            position.getX(),
            (displaySize.getHeight() - position.getY())
        );
    }
    
    public static float getOpenGlRotation(float rotation) {
        return rotation;
    }
}
