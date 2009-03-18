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
package edu.csun.ecs.cs.multitouchj.ui.graphic.image;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import org.lwjgl.opengl.EXTTextureRectangle;
import org.lwjgl.opengl.GL11;

/**
 * @author Atsuya Takagi
 * 
 * $Id: TextureHandler.java 57 2009-02-10 08:02:38Z Atsuya Takagi $
 */
public class TextureHandler {
    private IntBuffer textureIdBuffer;

    public TextureHandler() {
        textureIdBuffer = ByteBuffer.allocateDirect(4).order(
                ByteOrder.nativeOrder()).asIntBuffer();
    }

    public Texture generateTexture(Image image) {
        Integer textureId = generateTextureId();
        ByteBuffer imageData = prepareImage(image);

        GL11.glEnable(EXTTextureRectangle.GL_TEXTURE_RECTANGLE_EXT);
        GL11.glBindTexture(EXTTextureRectangle.GL_TEXTURE_RECTANGLE_EXT,
                textureId.intValue());

        GL11.glTexParameteri(EXTTextureRectangle.GL_TEXTURE_RECTANGLE_EXT,
                GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(EXTTextureRectangle.GL_TEXTURE_RECTANGLE_EXT,
                GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        // GL11.glTexParameteri(EXTTextureRectangle.GL_TEXTURE_RECTANGLE_EXT,
        // GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
        // GL11.glTexParameteri(EXTTextureRectangle.GL_TEXTURE_RECTANGLE_EXT,
        // GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
        GL11.glTexImage2D(EXTTextureRectangle.GL_TEXTURE_RECTANGLE_EXT, 0,
                (image.hasAlpha()) ? GL11.GL_RGBA8 : GL11.GL_RGB8, image
                        .getWidth(), image.getHeight(), 0,
                (image.hasAlpha()) ? GL11.GL_RGBA : GL11.GL_RGB,
                GL11.GL_UNSIGNED_BYTE, imageData);

        System.out.println("Texture generated: " + textureId);

        return new Texture(textureId, image);
    }

    public void deleteTexture(Texture texture) {
        deleteTextureId(texture.getId());
        texture.getImage().emptyData();

        System.out.println("Texture deleted: " + texture.getId());
    }

    public Texture updateTexture(Texture texture, Image image) {
        ByteBuffer imageData = prepareImage(image);

        GL11.glEnable(EXTTextureRectangle.GL_TEXTURE_RECTANGLE_EXT);
        GL11.glBindTexture(EXTTextureRectangle.GL_TEXTURE_RECTANGLE_EXT,
                texture.getId().intValue());

        GL11.glTexSubImage2D(EXTTextureRectangle.GL_TEXTURE_RECTANGLE_EXT, 0,
                0, 0, image.getWidth(), image.getHeight(),
                (image.hasAlpha()) ? GL11.GL_RGBA : GL11.GL_RGB,
                GL11.GL_UNSIGNED_BYTE, imageData);

        return new Texture(texture.getId(), image);
    }

    private Integer generateTextureId() {
        textureIdBuffer.clear();
        GL11.glGenTextures(textureIdBuffer);

        System.out.println("Generating texture id: " + textureIdBuffer.get(0));

        return new Integer(textureIdBuffer.get());
    }

    private boolean deleteTextureId(Integer textureId) {
        textureIdBuffer.clear();
        textureIdBuffer.put(textureId.intValue());
        textureIdBuffer.flip();
        GL11.glDeleteTextures(textureIdBuffer);

        System.out.println("Deleting texture id: " + textureId.intValue());

        return true;
    }

    private ByteBuffer prepareImage(Image image) {
        ByteBuffer imageData = ByteBuffer
                .allocateDirect(image.getData().length).order(
                        ByteOrder.nativeOrder());
        imageData.put(image.getData());
        imageData.flip();

        return imageData;
    }
}
