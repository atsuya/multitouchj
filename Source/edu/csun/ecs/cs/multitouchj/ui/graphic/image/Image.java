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

import edu.csun.ecs.cs.multitouchj.ui.geometry.Size;

/**
 * @author Atsuya Takagi
 * 
 * $Id: Image.java 57 2009-02-10 08:02:38Z Atsuya Takagi $
 */
public class Image {
    private int width;
    private int height;
    private byte[] data;
    private boolean hasAlpha;

    
    public Image(int width, int height, byte[] data, boolean hasAlpha) {
        this.width = width;
        this.height = height;

        this.data = new byte[data.length];
        System.arraycopy(data, 0, this.data, 0, data.length);

        this.hasAlpha = hasAlpha;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Size getSize() {
        return new Size(width, height);
    }

    public byte[] getData() {
        return data;
    }

    public boolean hasAlpha() {
        return hasAlpha;
    }

    public void emptyData() {
        data = new byte[0];
    }
}
