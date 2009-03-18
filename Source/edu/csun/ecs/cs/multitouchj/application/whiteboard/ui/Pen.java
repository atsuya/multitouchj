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
package edu.csun.ecs.cs.multitouchj.application.whiteboard.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

/**
 * @author Atsuya Takagi
 *
 * $Id: Pen.java 83 2009-03-16 06:51:26Z Atsuya Takagi $
 */
public class Pen {
    private Stroke stroke;
    private Paint paint;
    
    
    public Pen() {
        stroke = new BasicStroke(5.0f);
        paint = Color.BLACK;
    }
    
    public Stroke getStroke() {
        return stroke;
    }
    
    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }
    
    public Paint getPaint() {
        return paint;
    }
    
    public void setPaint(Paint paint) {
        this.paint = paint;
    }
}
