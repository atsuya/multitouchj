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

import edu.csun.ecs.cs.multitouchj.ui.geometry.Point;

/**
 * @author Atsuya Takagi
 *
 * $Id: CalibrationHandler.java 64 2009-02-12 23:32:19Z Atsuya Takagi $
 */
public abstract class CalibrationHandler {
    public CalibrationHandler() {
    }
    
    public abstract void setSourcePositions(
        Point sourceTopLeftPosition,
        Point sourceTopRightPosition,
        Point sourceBottomRightPosition,
        Point sourceBottomLeftPosition
    );
    
    public abstract void setDestinationPositions(
        Point destinationTopLeftPosition,
        Point destinationTopRightPosition,
        Point destinationBottomRightPosition,
        Point destinationBottomLeftPosition
    );
    
    public void setPositions(
        Point sourceTopLeftPosition,
        Point sourceTopRightPosition,
        Point sourceBottomRightPosition,
        Point sourceBottomLeftPosition,
        Point destinationTopLeftPosition,
        Point destinationTopRightPosition,
        Point destinationBottomRightPosition,
        Point destinationBottomLeftPosition
        ) {
        setSourcePositions(
            sourceTopLeftPosition,
            sourceTopRightPosition,
            sourceBottomRightPosition,
            sourceBottomLeftPosition
        );
        setDestinationPositions(
            destinationTopLeftPosition,
            destinationTopRightPosition,
            destinationBottomRightPosition,
            destinationBottomLeftPosition
        );
    }
    
    public abstract Point calibrate(Point sourcePosition);
}
