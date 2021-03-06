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
package edu.csun.ecs.cs.multitouchj.utility;

import java.util.Date;

/**
 * @author Atsuya Takagi
 * 
 * $Id: FrameMeter.java 70 2009-02-17 09:29:14Z Atsuya Takagi $
 */
public class FrameMeter {
    private int currentFps;
    private int previousFps;
    private long timeStarted;
    

    public FrameMeter() {
        previousFps = currentFps = 0;
        timeStarted = getTime();
    }

    public boolean update() {
        currentFps += 1;

        long time = getTime();
        if((time - timeStarted) < 1000) {
            return false;
        }

        timeStarted = time;
        previousFps = currentFps;
        currentFps = 0;

        return true;
    }

    public int getFps() {
        return previousFps;
    }

    private long getTime() {
        return ((new Date()).getTime());
    }
}
