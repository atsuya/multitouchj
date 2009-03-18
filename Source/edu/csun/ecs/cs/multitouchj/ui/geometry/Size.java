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
package edu.csun.ecs.cs.multitouchj.ui.geometry;

/**
 * @author Atsuya Takagi
 *
 * $Id: Size.java 68 2009-02-13 10:34:36Z Atsuya Takagi $
 */
public class Size {
    private float width;
    private float height;
    
    
    public Size(float width, float height) {
        set(width, height);
    }
    
    public Size() {
        this(0.0f, 0.0f);
    }
    
    public Size(Size size) {
        this(size.getWidth(), size.getHeight());
    }
    
    public float getWidth() {
        return width;
    }
    
    public void setWidth(float width) {
        if(width < 0.0f) {
            throw new RuntimeException("Width cannot be smaller than 0.");
        }
        
        this.width = width;
    }
    
    public float getHeight() {
        return height;
    }
    
    public void setHeight(float height) {
        if(height < 0.0f) {
            throw new RuntimeException("Height cannot be smaller than 0.");
        }
        
        this.height = height;
    }
    
    public void set(float width, float height) {
        setWidth(width);
        setHeight(height);
    }
    
    public void set(Size size) {
        set(size.getWidth(), size.getHeight());
    }
    
    public String toString() {
        return "width: "+width+", height: "+height;
    }
}
