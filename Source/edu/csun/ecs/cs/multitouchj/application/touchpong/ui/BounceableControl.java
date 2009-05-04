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
package edu.csun.ecs.cs.multitouchj.application.touchpong.ui;

import edu.csun.ecs.cs.multitouchj.ui.control.FramedControl;
import edu.csun.ecs.cs.multitouchj.ui.control.VisualControl;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Point;

/**
 * @author Atsuya Takagi
 *
 * $Id$
 */
public class BounceableControl extends FramedControl {
    public BounceableControl() {
        super();
    }
    
    public boolean isWithin(Point position) {
        return false;
    }
    
    public boolean isHit(Point point) {
        return super.isWithin(point);
    }
}
