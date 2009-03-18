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

import java.net.URL;
import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.csun.ecs.cs.multitouchj.ui.control.TexturedControl;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Point;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Size;
import edu.csun.ecs.cs.multitouchj.ui.graphic.WindowManagerCalibrator.Position;

/**
 * @author Atsuya Takagi
 *
 * $Id: WindowManagerCalibratorDefault.java 70 2009-02-17 09:29:14Z Atsuya Takagi $
 */
public class WindowManagerCalibratorDefault extends WindowManagerCalibrator {
    private static final String URI_IMAGE_WAITING =
        "/edu/csun/ecs/cs/multitouchj/ui/resource/CalibrationTarget-Waiting.png";
    private static final String URI_IMAGE_OK =
        "/edu/csun/ecs/cs/multitouchj/ui/resource/CalibrationTarget-Ok.png";
    private static Log log = LogFactory.getLog(WindowManagerCalibratorDefault.class);
    private Hashtable<Position, Hashtable<String, TexturedControl>> controls;
    
    
    public WindowManagerCalibratorDefault() {
        controls = new Hashtable<Position, Hashtable<String, TexturedControl>>();
    }
    
    public void start(Size displaySize) {
        prepareControls();
        hideControls();
        
        super.start(displaySize);
    }

    /* (non-Javadoc)
     * @see edu.csun.ecs.cs.multitouchj.ui.graphic.WindowManagerCalibrator#presentCalibrationTarget(edu.csun.ecs.cs.multitouchj.ui.graphic.WindowManagerCalibrator.Position)
     */
    @Override
    protected void presentCalibrationTarget(Position position) {
        Size displaySize = getDisplaySize();
        Size controlSize = getControlSize();
        
        Point controlPosition = new Point();
        Point destinationPosition = new Point();
        if(Position.TopLeft.equals(position)) {
            controlPosition.set(
                (controlSize.getWidth() / 2.0f),
                (controlSize.getHeight() / 2.0f)
            );
            destinationPosition.set(
                (controlSize.getWidth() / 2.0f),
                (controlSize.getHeight() / 2.0f)
            );
        } else if(Position.TopRight.equals(position)) {
            controlPosition.set(
                (displaySize.getWidth() - (controlSize.getWidth() / 2.0f)),
                (controlSize.getHeight() / 2.0f)
            );
            destinationPosition.set(
                (displaySize.getWidth() - (controlSize.getWidth() / 2.0f)),
                (controlSize.getHeight() / 2.0f)
            );
        } else if(Position.BottomRight.equals(position)) {
            controlPosition.set(
                (displaySize.getWidth() - (controlSize.getWidth() / 2.0f)),
                (displaySize.getHeight() - (controlSize.getHeight() / 2.0f))
            );
            destinationPosition.set(
                (displaySize.getWidth() - (controlSize.getWidth() / 2.0f)),
                (displaySize.getHeight() - (controlSize.getHeight() / 2.0f))
            );
        } else {
            controlPosition.set(
                (controlSize.getWidth() / 2.0f),
                (displaySize.getHeight() - (controlSize.getHeight() / 2.0f))
            );
            destinationPosition.set(
                (controlSize.getWidth() / 2.0f),
                (displaySize.getHeight() - (controlSize.getHeight() / 2.0f))
            );
        }
        
        getDestinationPositions().put(position, new Point(destinationPosition));
        
        Hashtable<String, TexturedControl> targetControls = controls.get(position);
        for(TexturedControl control : targetControls.values()) {
            control.setPosition(controlPosition);
        }
        targetControls.get(URI_IMAGE_WAITING).setVisible(true);
    }
    
    protected void onSourcePositionCaptured(Position position) {
        super.onSourcePositionCaptured(position);
        
        Hashtable<String, TexturedControl> targetControls = controls.get(position);
        targetControls.get(URI_IMAGE_WAITING).setVisible(false);
        targetControls.get(URI_IMAGE_OK).setVisible(true);
        
        log.info("Hmm: "+targetControls.get(URI_IMAGE_OK).getPosition().toString());
    }
    
    protected void onFinished() {
        hideControls();
        
        super.onFinished();
    }
    
    protected void prepareControls() {
        if(controls.size() == 0) {
            try {
                String[] types = new String[]{URI_IMAGE_WAITING, URI_IMAGE_OK};
                for(Position position : Position.values()) {
                    Hashtable<String, TexturedControl> texturedControls =
                        new Hashtable<String, TexturedControl>();
                    for(String type : types) {
                        TexturedControl control = new TexturedControl();
                        control.setTexture(getClass().getResource(type));
                        
                        texturedControls.put(type, control);
                    }
                    
                    controls.put(position, texturedControls);
                }
            } catch(Exception exception) {
                log.error("Failed to load image.", exception);
            }
        }
    }
    
    protected void hideControls() {
        Size displaySize = getDisplaySize();
        
        for(Hashtable<String, TexturedControl> pair : controls.values()) {
            for(TexturedControl control : pair.values()) {
                control.setPosition(new Point(displaySize.getWidth(), displaySize.getHeight()));
                control.setVisible(false);
            }
        }
    }
    
    protected Size getControlSize() {
        return controls.get(Position.TopLeft).get(URI_IMAGE_WAITING).getSize();
    }
}
