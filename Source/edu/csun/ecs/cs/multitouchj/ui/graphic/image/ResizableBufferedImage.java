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

import java.awt.image.BufferedImage;

import edu.csun.ecs.cs.multitouchj.ui.geometry.Point;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Size;

/**
 * @author Atsuya Takagi
 * 
 * $Id: ResizableBufferedImage.java 81 2009-03-15 20:54:39Z Atsuya Takagi $
 */
public class ResizableBufferedImage {
    private BufferedImage bufferedImage;

    
    public ResizableBufferedImage(int width, int height, int imageType) {
        bufferedImage = createBufferedImage(width, height, imageType);
    }

    public ResizableBufferedImage(Size size, int imageType) {
        bufferedImage = createBufferedImage(size, imageType);
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public BufferedImage getBufferedImage(Size size) {
        return getBufferedImage(0, 0, (int)size.getWidth(), (int)size.getHeight());
    }

    public BufferedImage getBufferedImage(int width, int height) {
        return getBufferedImage(0, 0, width, height);
    }

    public BufferedImage getBufferedImage(Point position, Size size) {
        return getBufferedImage(
            (int)position.getX(),
            (int)position.getY(),
            (int)size.getWidth(),
            (int)size.getHeight()
        );
    }

    public boolean doesFit(Size size) {
        return doesFit(0, 0, (int)size.getWidth(), (int)size.getHeight());
    }

    public boolean doesFit(Point position, Size size) {
        return doesFit(
            (int)position.getX(),
            (int)position.getY(),
            (int)size.getWidth(),
            (int)size.getHeight()
        );
    }

    public boolean doesFit(int x, int y, int width, int height) {
        if((x < 0) || (y < 0)) {
            return false;
        }

        Size bufferedImageSize = getSize();
        if(((x + width) > bufferedImageSize.getWidth())
                || ((y + height) > bufferedImageSize.getHeight())) {
            return false;
        }

        return true;
    }

    public synchronized void setSize(Size size) {
        if(!size.equals(getSize())) {
            bufferedImage = createBufferedImage(size, bufferedImage.getType());
        }
    }

    public Size getSize() {
        return new Size(bufferedImage.getWidth(), bufferedImage.getHeight());
    }

    protected BufferedImage getBufferedImage(int x, int y, int width, int height) {
        if(!doesFit(x, y, width, height)) {
            Size newSize = new Size((x + width), (y + height));
            setSize(newSize);
        }

        return bufferedImage.getSubimage(x, y, width, height);
    }

    private BufferedImage createBufferedImage(Size size, int imageType) {
        return new BufferedImage((int)size.getWidth(), (int)size.getHeight(), imageType);
    }

    private BufferedImage createBufferedImage(
        int width,
        int height,
        int imageType
        ) {
        return new BufferedImage(width, height, imageType);
    }
}
