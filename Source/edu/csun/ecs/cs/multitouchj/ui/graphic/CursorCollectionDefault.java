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

import java.awt.Color;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.csun.ecs.cs.multitouchj.ui.control.Control;
import edu.csun.ecs.cs.multitouchj.ui.control.TexturedControl;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Point;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Size;

/**
 * @author Atsuya Takagi
 *
 * $Id: CursorCollectionDefault.java 76 2009-02-24 18:16:37Z Atsuya Takagi $
 */
public class CursorCollectionDefault extends CursorCollection {
    private static final int NUMBER_OF_CURSORS = 4;
    private static final Color[] CURSOR_COLORS = {
        Color.WHITE,
        Color.RED,
        Color.BLUE,
        Color.YELLOW
    };
    private static final String URL_IMAGE =
        "/edu/csun/ecs/cs/multitouchj/ui/resource/Cursor-TopLeft.png";
    private static Log log = LogFactory.getLog(CursorCollectionDefault.class);
    private Hashtable<Integer, Control> controls;
    
    
    public CursorCollectionDefault() {
        super();
        
        controls = new Hashtable<Integer, Control>();
    }
    
    public void initialize() throws Exception {
        synchronized(controls) {
            for(int i = 0; i < NUMBER_OF_CURSORS; i++) {
                Control control = createControl(i);
                controls.put(i, control);
            }
        }
    }
    
    public Control getCursor(int id) {
        synchronized(controls) {
            Control control = controls.get(id);
            if(control == null) {
                try {
                    control = createControl(id);
                    controls.put(id, control);
                } catch(Exception exception) {}
            }
            
            return control;
        }
    }
    
    /* (non-Javadoc)
     * @see edu.csun.ecs.cs.multitouchj.ui.graphic.CursorCollection#getCursors()
     */
    @Override
    public List<Control> getCursors() {
        return new LinkedList<Control>(controls.values());
    }
    
    public void destroy() {
        synchronized(controls) {
            LinkedList<Control> controlsToDelete =
                new LinkedList<Control>(controls.values());
            for(Control control : controlsToDelete) {
                control.dispose();
            }
            
            controls.clear();
        }
    }
    
    protected Control createControl(int id) throws Exception {
        TexturedControl texturedControl = new TexturedControl();
        texturedControl.setTexture(getClass().getResource(URL_IMAGE));
        texturedControl.setOpacity(0.7f);
        
        Color color = CURSOR_COLORS[0];
        if((id >= 0) && (id < CURSOR_COLORS.length)) {
            color = CURSOR_COLORS[id];
        }
        texturedControl.setColor(color);
        
        return texturedControl;
    }
}
