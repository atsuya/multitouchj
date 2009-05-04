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
package edu.csun.ecs.cs.multitouchj.ui.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import edu.csun.ecs.cs.multitouchj.ui.geometry.Point;
import edu.csun.ecs.cs.multitouchj.ui.utility.PointUtility;

/**
 * @author Atsuya Takagi
 *
 * $Id$
 */
public class PointUtilityTest {
    private static Log log = LogFactory.getLog(PointUtilityTest.class);
    
    
    @Test
    public void test() {
        Point pointA = new Point();
        Point pointB = new Point();
        float angle = 0.0f;
        
        // (0, 0) to (1.0f, 1.0f)
        pointA.set(0.0f, 0.0f);
        pointB.set(1.0f, 1.0f);
        angle = PointUtility.getAngle(pointA, pointB, true);
        log.debug("Cartesian (0, 0) to (1.0f, 1.0f): "+angle);
        
        // (0, 0) to (1.0f, -1.0f)
        pointA.set(0.0f, 0.0f);
        pointB.set(1.0f, -1.0f);
        angle = PointUtility.getAngle(pointA, pointB, true);
        log.debug("Cartesian (0, 0) to (1.0f, -1.0f): "+angle);
        
        // (0, 0) to (-1.0f, -1.0f)
        pointA.set(0.0f, 0.0f);
        pointB.set(-1.0f, -1.0f);
        angle = PointUtility.getAngle(pointA, pointB, true);
        log.debug("Cartesian (0, 0) to (-1.0f, -1.0f): "+angle);
        
        // (0, 0) to (-1.0f, 1.0f)
        pointA.set(0.0f, 0.0f);
        pointB.set(-1.0f, 1.0f);
        angle = PointUtility.getAngle(pointA, pointB, true);
        log.debug("Cartesian (0, 0) to (-1.0f, 1.0f): "+angle);
        
        
        // (0, 0) to (1.0f, 1.0f)
        pointA.set(1.0f, 1.0f);
        pointB.set(2.0f, 2.0f);
        angle = PointUtility.getAngle(pointA, pointB, false);
        log.debug("MultiTouchJ (0, 0) to (1.0f, 1.0f): "+angle);
        
        // (0, 0) to (1.0f, -1.0f)
        pointA.set(1.0f, 1.0f);
        pointB.set(2.0f, 0.0f);
        angle = PointUtility.getAngle(pointA, pointB, false);
        log.debug("MultiTouchJ (0, 0) to (1.0f, -1.0f): "+angle);
        
        // (0, 0) to (-1.0f, -1.0f)
        pointA.set(1.0f, 1.0f);
        pointB.set(0.0f, 0.0f);
        angle = PointUtility.getAngle(pointA, pointB, false);
        log.debug("MultiTouchJ (0, 0) to (-1.0f, -1.0f): "+angle);
        
        // (0, 0) to (-1.0f, 1.0f)
        pointA.set(1.0f, 1.0f);
        pointB.set(0.0f, 2.0f);
        angle = PointUtility.getAngle(pointA, pointB, false);
        log.debug("MultiTouchJ (0, 0) to (-1.0f, 1.0f): "+angle);
    }
}
