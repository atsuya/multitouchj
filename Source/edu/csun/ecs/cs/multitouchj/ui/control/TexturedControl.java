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
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.opengl.EXTTextureRectangle;
import org.lwjgl.opengl.GL11;

import edu.csun.ecs.cs.multitouchj.ui.geometry.Point;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Size;
import edu.csun.ecs.cs.multitouchj.ui.graphic.image.Texture;
import edu.csun.ecs.cs.multitouchj.ui.graphic.image.TextureManager;
import edu.csun.ecs.cs.multitouchj.ui.utility.OpenGlUtility;

/**
 * @author Atsuya Takagi
 *
 * $Id: TexturedControl.java 80 2009-03-15 07:21:37Z Atsuya Takagi $
 */
public class TexturedControl extends Control {
    private static Log log = LogFactory.getLog(TexturedControl.class);
    private TextureManager textureManager;
    private Texture texture;
    
    
    public TexturedControl() {
        super();
        
        textureManager = TextureManager.getInstance();
    }
    
    public Texture getTexture() {
        return texture;
    }
    
    public void setTexture(URL url) throws Exception {
        Texture texture = null;
        if(url != null) {
            texture = textureManager.createTexture(url);
            setSize(texture.getImage().getSize());
        }
        
        setTexture(texture);
    }
    
    /* (non-Javadoc)
     * @see edu.csun.ecs.cs.multitouchj.ui.graphic.Renderable#render()
     */
    public void render() {
        if(!isVisible()) {
            return;
        }
        
        Size controlSize = getSize();
        Size imageSize = null;
        if(texture != null) {
            imageSize = texture.getImage().getSize();
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
        
        if(texture != null) {
            GL11.glEnable(EXTTextureRectangle.GL_TEXTURE_RECTANGLE_EXT);
            GL11.glBindTexture(
                EXTTextureRectangle.GL_TEXTURE_RECTANGLE_EXT,
                texture.getId().intValue()
            );
        } else {
            GL11.glDisable(EXTTextureRectangle.GL_TEXTURE_RECTANGLE_EXT);
        }
        
        // bl -> br -> tr -> tl
        GL11.glBegin(GL11.GL_QUADS);
        if(texture != null) {
            GL11.glTexCoord2f(0.0f, 0.0f);
        }
        GL11.glVertex3f((-1 * halfWidth), (-1 * halfHeight), 0.0f);
        
        if(texture != null) {
            GL11.glTexCoord2f(imageSize.getWidth(), 0.0f);
        }
        GL11.glVertex3f(halfWidth, (-1 * halfHeight), 0.0f);
        
        if(texture != null) {
            GL11.glTexCoord2f(imageSize.getWidth(), imageSize.getHeight());
        }
        GL11.glVertex3f(halfWidth, halfHeight, 0.0f);
        
        if(texture != null) {
            GL11.glTexCoord2f(0.0f, imageSize.getHeight());
        }
        GL11.glVertex3f((-1 * halfWidth), halfHeight, 0.0f);
        GL11.glEnd();
    }
    
    /*
    public boolean isWithin(Point position) {
        Texture texture = getTexture();
        if(texture != null) {
            if(texture.getId() > 2) {
                return super.isWithin(position);
            }
        }
        return false;
    }
    */
    
    protected TextureManager getTextureManager() {
        return textureManager;
    }
    
    protected void setTexture(Texture texture) {
        this.texture = texture;
    }
}
