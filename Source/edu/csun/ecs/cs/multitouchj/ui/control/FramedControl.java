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
package edu.csun.ecs.cs.multitouchj.ui.control;

import java.awt.Color;

import org.lwjgl.opengl.EXTTextureRectangle;
import org.lwjgl.opengl.GL11;

import edu.csun.ecs.cs.multitouchj.ui.geometry.Point;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Size;
import edu.csun.ecs.cs.multitouchj.ui.graphic.image.Texture;
import edu.csun.ecs.cs.multitouchj.ui.utility.OpenGlUtility;

/**
 * @author Atsuya Takagi
 *
 * $Id: FramedControl.java 80 2009-03-15 07:21:37Z Atsuya Takagi $
 */
public class FramedControl extends TexturedControl {
    private float margin;
    
    
    public FramedControl() {
        super();
        
        setMargin(0.0f);
    }
    
    public float getMargin() {
        return margin;
    }
    
    public void setMargin(float margin) {
        float value = margin;
        if(value < 0.0f) {
            value = 0.0f;
        }
        
        this.margin = value;
    }
    
    public void render() {
        // render with no texture
        Texture texture = getTexture();
        setTexture((Texture)null);
        
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        super.render();
        GL11.glPopMatrix();
        
        setTexture(texture);
        
        // rendering texture
        if((!isVisible()) || (texture == null)) {
            return;
        }
        
        Size controlSize = getSize();
        Size imageSize = texture.getImage().getSize();
        float margin = getMargin();
        if((margin >= controlSize.getWidth()) || (margin >= controlSize.getHeight())) {
            margin = 0.0f;
        }
        
        Color color = getColor();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(
            ((float)color.getRed() / 255.0f),
            ((float)color.getGreen() / 255.0f),
            ((float)color.getBlue() / 255.0f),
            getOpacity()
        );
        
        Point position = getOpenGlPosition();
        float halfWidth = (controlSize.getWidth() / 2.0f);
        float halfHeight = (controlSize.getHeight() / 2.0f);
        GL11.glTranslatef(position.getX(), position.getY(), 0.0f);
        
        float rotation = OpenGlUtility.getOpenGlRotation(getRotation());
        GL11.glRotatef(rotation, 0.0f, 0.0f, 1.0f);
        
        // render texture
        GL11.glEnable(EXTTextureRectangle.GL_TEXTURE_RECTANGLE_EXT);
        GL11.glBindTexture(EXTTextureRectangle.GL_TEXTURE_RECTANGLE_EXT, texture.getId().intValue());
        
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(
            ((-1 * halfWidth) + margin),
            ((-1 * halfHeight) + margin),
            0.0f
        );
        
        GL11.glTexCoord2f(imageSize.getWidth(), 0.0f);
        GL11.glVertex3f(
            (halfWidth - margin),
            ((-1 * halfHeight) + margin),
            0.0f
        );
        
        GL11.glTexCoord2f(imageSize.getWidth(), imageSize.getHeight());
        GL11.glVertex3f(
            (halfWidth - margin),
            (halfHeight - margin),
            0.0f
        );
        
        GL11.glTexCoord2f(0.0f, imageSize.getHeight());
        GL11.glVertex3f(
            ((-1 * halfWidth) + margin),
            (halfHeight - margin),
            0.0f
        );
        GL11.glEnd();
    }
}
