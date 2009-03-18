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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.EXTTextureRectangle;
import org.lwjgl.opengl.GL11;

import edu.csun.ecs.cs.multitouchj.objectobserver.event.ObjectObserverEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.ControlEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.ObjectEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.TouchEvent;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Point;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Size;
import edu.csun.ecs.cs.multitouchj.ui.gesture.GestureHandler;
import edu.csun.ecs.cs.multitouchj.ui.graphic.image.Image;
import edu.csun.ecs.cs.multitouchj.ui.graphic.image.ResizableBufferedImage;
import edu.csun.ecs.cs.multitouchj.ui.graphic.image.Texture;

/**
 * @author Atsuya Takagi
 *
 * $Id: Canvas.java 84 2009-03-16 06:52:06Z Atsuya Takagi $
 */
public abstract class Canvas extends FramedControl {
    private static Log log = LogFactory.getLog(Canvas.class);
    protected ResizableBufferedImage resizableBufferedImage;
    
    
    public Canvas() {
        super();
        
        resizableBufferedImage = new ResizableBufferedImage(10, 10, BufferedImage.TYPE_4BYTE_ABGR);
    }
    
    protected void onControlResized(ControlEvent controlEvent) {
        super.onControlResized(controlEvent);
        
        synchronized(resizableBufferedImage) {
            Size size = getAdjustedSize();
            resizableBufferedImage.setSize(size);
        }
    }
    
    protected List<GestureHandler>getGestureHandlers() {
        return new LinkedList<GestureHandler>();
    }
    
    protected Size getAdjustedSize() {
        Size size = getSize();
        size.set((int)Math.floor(size.getWidth()), (int)Math.floor(size.getHeight()));
        
        return size;
    }
    
    protected ByteBuffer prepareImage(Image image)
    {
        ByteBuffer imageData = ByteBuffer.allocateDirect(
            image.getData().length).order(ByteOrder.nativeOrder()
        );
        imageData.put(image.getData());
        imageData.flip();
        
        
        return imageData;
    }
    
    protected Image getBufferedImageImage(BufferedImage bufferedImage) {
        byte[] bytes = (byte[])bufferedImage.getRaster().getDataElements(
            0,
            0,
            bufferedImage.getWidth(),
            bufferedImage.getHeight(),
            null
        );
        
        return new Image(bufferedImage.getWidth(), bufferedImage.getHeight(), bytes, true);
    }
}
