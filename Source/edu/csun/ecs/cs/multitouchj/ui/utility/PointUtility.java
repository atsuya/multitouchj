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
package edu.csun.ecs.cs.multitouchj.ui.utility;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.csun.ecs.cs.multitouchj.ui.geometry.Point;

/**
 * @author Atsuya Takagi
 *
 * $Id: PointUtility.java 68 2009-02-13 10:34:36Z Atsuya Takagi $
 */
public class PointUtility {
    private static Log log = LogFactory.getLog(PointUtility.class);
    
    
    protected PointUtility() {
    }
    
    public static float getDistance(Point pointA, Point pointB) {
        return (float)Math.sqrt(
            Math.pow((pointB.getX() - pointA.getX()), 2) + 
            Math.pow((pointB.getY() - pointA.getY()), 2)
        );
    }
    
    /**
     * Assumes that 0 degree is x-axis. Note that (0,0) is top-left corner!
     * This is based on regular math x-y coordinate, not the one in multitouchj!
     * 
     * @param pointA
     * @param pointB
     * @return
     */
    public static float getAngle(Point pointA, Point pointB) {
        /*
        float distance = PointUtility.getDistance(pointA, pointB);
        float deltaX = Math.abs((pointA.getX() - pointB.getX()));
        float angle = (float)Math.toDegrees(Math.acos((deltaX / distance)));
        log.debug("ok: "+angle);
        
        // determine which of 4 areas it is in
        Point left = new Point(pointA);
        Point right = new Point(pointB);
        if(left.getX() > right.getX()) {
            left.set(pointB);
            right.set(pointA);
        }
        if(left.getY() < right.getY()) {
            angle = 360.0f - angle;
        }
        */
        return getAngle(pointA, pointB, true);
    }
    
    public static float getAngle(Point pointA, Point pointB, boolean isCartesianCoordinateSystem) {
        Point targetPointA = new Point(pointA);
        Point targetPointB = new Point(pointB);
        if(!isCartesianCoordinateSystem) {
            targetPointA.setY((-1 * targetPointA.getY()));
            targetPointB.setY((-1 * targetPointB.getY()));
        }
        
        Point point = new Point(
            (targetPointB.getX() - targetPointA.getX()),
            (targetPointB.getY() - targetPointA.getY())
        );
        return getAngle(point, true);
    }
    
    public static float getAngle(Point point) {
        return getAngle(point, true);
    }
    
    public static float getAngle(Point point, boolean isCartesianCoordinateSystem) {
        Point targetPoint = new Point(point);
        if(!isCartesianCoordinateSystem) {
            targetPoint.setY((-1 * targetPoint.getY()));
        }
        
        Point origin = new Point(0.0f, 0.0f);
        float distance = PointUtility.getDistance(origin, targetPoint);
        float deltaX = Math.abs(targetPoint.getX());
        float angle = (float)Math.toDegrees(Math.acos((deltaX / distance)));
        
        if(targetPoint.getX() > 0.0f) {
            if(targetPoint.getY() > 0.0f) {
                // nothing to do
            } else {
                angle = (360.0f - angle);
            }
        } else {
            if(targetPoint.getY() > 0.0f) {
                angle = (180.0f - angle);
            } else {
                angle = (180.0f + angle);
            }
        }
        
        return angle;
    }
}
