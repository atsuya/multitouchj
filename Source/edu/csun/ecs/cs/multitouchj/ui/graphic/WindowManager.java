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

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import edu.csun.ecs.cs.multitouchj.objectobserver.ObjectObserver;
import edu.csun.ecs.cs.multitouchj.objectobserver.event.Event;
import edu.csun.ecs.cs.multitouchj.objectobserver.event.ObjectObserverEvent;
import edu.csun.ecs.cs.multitouchj.objectobserver.event.ObjectObserverListener;
import edu.csun.ecs.cs.multitouchj.ui.control.Control;
import edu.csun.ecs.cs.multitouchj.ui.event.FloatEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.ObjectEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.ObjectEventManager;
import edu.csun.ecs.cs.multitouchj.ui.event.ObjectEventManagerListener;
import edu.csun.ecs.cs.multitouchj.ui.event.PositionEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.TouchEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.WindowManagerCalibratorEvent;
import edu.csun.ecs.cs.multitouchj.ui.event.WindowManagerCalibratorListener;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Point;
import edu.csun.ecs.cs.multitouchj.ui.geometry.Size;
import edu.csun.ecs.cs.multitouchj.ui.graphic.image.TextureManager;
import edu.csun.ecs.cs.multitouchj.ui.utility.OpenGlUtility;

/**
 * @author Atsuya Takagi
 *
 * $Id: WindowManager.java 79 2009-03-01 23:36:26Z Atsuya Takagi $
 */
public class WindowManager implements
    ObjectEventManagerListener, WindowManagerCalibratorListener {
    private static Log log = LogFactory.getLog(WindowManager.class);
    private static WindowManager instance = null;
    private DisplayManager displayManager;
    private ObjectObserver objectObserver;
    // last one is the most front one
    private LinkedList<Control> controls;
    private CursorHandler cursorHandler;
    private ObjectEventManager objectEventManager;
    private WindowManagerCalibrator windowManagerCalibrator;
    private CalibrationHandler calibrationHandler;
    private Control backgroundControl;
    
    
    protected WindowManager(
        DisplayManager displayManager,
        ObjectObserver objectObserver
        ) throws Exception {
        controls = new LinkedList<Control>();
        objectEventManager = new ObjectEventManager();
        windowManagerCalibrator = new WindowManagerCalibratorDefault();
        calibrationHandler = new CalibrationHandlerPlanarHomography();
        cursorHandler = new CursorHandler();
        
        setDisplayManager(displayManager);
        setObjectObserver(objectObserver);
        setBackgroundControl(null);
        
        initialize();
    }
    
    public static void create(
        DisplayManager displayManager,
        ObjectObserver objectObserver
        ) throws Exception {
        if(instance == null) {
            instance = new WindowManager(displayManager, objectObserver);
        }
    }
    
    public static WindowManager getInstance() {
        return instance;
    }
    
    public DisplayManager getDisplayManager() {
        return displayManager;
    }
    
    public ObjectObserver getObjectObserver() {
        return objectObserver;
    }
    
    public WindowManagerCalibrator getWindowManagerCalibrator() {
        return windowManagerCalibrator;
    }
    
    /* (non-Javadoc)
     * @see edu.csun.ecs.cs.multitouchj.ui.event.ObjectEventManagerListener#floatEventGenerated(edu.csun.ecs.cs.multitouchj.ui.event.FloatEvent)
     */
    public void floatEventGenerated(FloatEvent floatEvent) {
        dispatchEvent(floatEvent);
    }

    /* (non-Javadoc)
     * @see edu.csun.ecs.cs.multitouchj.ui.event.ObjectEventManagerListener#touchEventGenerated(edu.csun.ecs.cs.multitouchj.ui.event.TouchEvent)
     */
    public void touchEventGenerated(TouchEvent touchEvent) {
        dispatchEvent(touchEvent);
    }
    
    /* (non-Javadoc)
     * @see edu.csun.ecs.cs.multitouchj.ui.event.WindowManagerCalibratorListener#calibrated(edu.csun.ecs.cs.multitouchj.ui.event.WindowManagerCalibratorEvent)
     */
    public void calibrated(WindowManagerCalibratorEvent event) {
        log.info("stl: "+event.getSourceTopLeftPosition().toString());
        log.info("str: "+event.getSourceTopRightPosition().toString());
        log.info("sbr: "+event.getSourceBottomRightPosition().toString());
        log.info("sbl: "+event.getSourceBottomLeftPosition().toString());
        
        log.info("dtl: "+event.getDestinationTopLeftPosition().toString());
        log.info("dtr: "+event.getDestinationTopRightPosition().toString());
        log.info("dbr: "+event.getDestinationBottomRightPosition().toString());
        log.info("dbl: "+event.getDestinationBottomLeftPosition().toString());
        
        calibrationHandler.setPositions(
            event.getSourceTopLeftPosition(),
            event.getSourceTopRightPosition(),
            event.getSourceBottomRightPosition(),
            event.getSourceBottomLeftPosition(),
            event.getDestinationTopLeftPosition(),
            event.getDestinationTopRightPosition(),
            event.getDestinationBottomRightPosition(),
            event.getDestinationBottomLeftPosition()
        );
    }
    
    public void calibrate() {
        DisplayMode displayMode = displayManager.getCurrentDisplayMode();
        windowManagerCalibrator.start(
            new Size(displayMode.getWidth(), displayMode.getHeight())
        );
    }
    
    public boolean registerControl(Control control) {
        boolean result = false;
        
        synchronized(controls) {
            if(!controls.contains(control)) {
                controls.add(control);
                result = true;
            }
        }
        
        return result;
    }
    
    public boolean unregisterControl(Control control) {
        boolean result = false;
        
        synchronized(controls) {
            if(controls.contains(control)) {
                controls.remove(control);
                result = true;
            }
        }
        
        return result;
    }
    
    public void update() {
        clearOpenGl();
        
        GLU.gluLookAt(
            0.0f, 0.0f, 3.0f,
            0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f
        );
        render();
        displayManager.update();
    }
    
    public void destroy() {
        try {
            windowManagerCalibrator.removeWindowManagerCalibratorListener(this);
            
            objectEventManager.removeObjectEventManagerListener(this);
            objectEventManager.removeObjectEventManagerListener(windowManagerCalibrator);
            
            objectEventManager.stop();
            objectObserver.stop();
        } catch(Exception exception) {
            log.error("Failed to destroy WindowManager.", exception);
        }
        
        synchronized(cursorHandler) {
            CursorCollection cursorCollection = cursorHandler.getCursorCollection();
            if(cursorCollection != null) {
                cursorCollection.destroy();
            }
        }
        
        synchronized(controls) {
            LinkedList<Control> elements = new LinkedList<Control>(controls);
            for(Control element : elements) {
                element.dispose();
            }
        }
        
        TextureManager.getInstance().destroy();
    }
    
    public CursorCollection getCursorCollection() {
        return cursorHandler.getCursorCollection();
    }
    
    public void setCursorCollection(CursorCollection cursorCollection) throws Exception {
        try {
            cursorCollection.initialize();
            cursorHandler.setCursorCollection(cursorCollection);
        } catch(Exception exception) {
            log.error("Failed to set CursorCollection.", exception);
            throw new Exception("Failed to set CursorCollection.");
        }
    }
    
    public void dispatchEvent(Event event) {
        // dispatch event from control in front to back
        synchronized(controls) {
            if(event instanceof ObjectEvent) {
                ObjectEvent objectEvent = (ObjectEvent)event;
                objectEvent.calibrate(calibrationHandler);

                log.debug("Ooes: ");
                for(ObjectObserverEvent ooe : objectEvent.getObjectObserverEvents()) {
                    log.debug("\t"+ooe.getId()+": "+ooe.getX()+", "+ooe.getY());
                }

                dispatchObjectEventControls(objectEvent);
                dispatchObjectEventCursors(objectEvent);
            } else {
                Control control = controls.getLast();
                control.dispatchEvent(event);
            }
        }
    }
    
    public Control getBackgroundControl() {
        return backgroundControl;
    }
    
    public void setBackgroundControl(Control control) {
        synchronized(controls) {
            backgroundControl = control;
            
            if(controls.contains(backgroundControl)) {
                controls.remove(backgroundControl);
                controls.addFirst(backgroundControl);
            }
        }
    }
    
    protected void render() {
        DisplayMode displayMode = displayManager.getCurrentDisplayMode();
        OpenGlUtility.orthoMode(
            new Size(displayMode.getWidth(), displayMode.getHeight())
        );
        
        renderBackground();
        renderControls();
        renderCursor();
        
        OpenGlUtility.perspectiveMode();
    }
    
    protected void renderBackground() {
        if(backgroundControl != null) {
            synchronized(backgroundControl) {
                renderControl(backgroundControl);
            }
        }
    }

    protected void renderControls() {
        List<Control> cursors = null;
        synchronized(cursorHandler) {
            cursors = cursorHandler.getCursors();
        }
        
        synchronized(controls) {
            for(Control control : controls) {
                if((!cursors.contains(control)) && (!control.equals(backgroundControl))) {
                    renderControl(control);
                }
            }
        }
    }
    
    protected void renderCursor() {
        synchronized(cursorHandler) {
            //log.info("Cursor: "+cursorHandler.getActiveCursors().size());
            
            //log.debug("Cursors: "+cursorHandler.getActiveCursors().size());
            for(Control control : cursorHandler.getActiveCursors()) {
                renderControl(control);
            }
        }
    }
    
    protected void renderControl(Control control) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        
        control.render();
        
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }
    
    protected void setDisplayManager(DisplayManager displayManager) {
        this.displayManager = displayManager;
    }
    
    protected void setObjectObserver(ObjectObserver objectObserver) {
        if(this.objectObserver != null) {
            this.objectObserver.removeObjectObserverListener(objectEventManager);
        }
        
        this.objectObserver = objectObserver;
        this.objectObserver.addObjectObserverListener(objectEventManager);
    }
    
    protected void initialize() throws Exception {
        initializeOpenGl();
        TextureManager.create();
        
        windowManagerCalibrator.addWindowManagerCalibratorListener(this);
        
        objectEventManager.addObjectEventManagerListener(this);
        objectEventManager.addObjectEventManagerListener(windowManagerCalibrator);
        
        objectEventManager.start();
        objectObserver.start();
    }
    
    protected void initializeOpenGl() {
        DisplayMode displayMode = displayManager.getCurrentDisplayMode();
        
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClearDepth(1.0f);
        GL11.glDepthFunc(GL11.GL_LEQUAL);

        GL11.glViewport(0, 0, displayMode.getWidth(), displayMode.getHeight());
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();

        //GLU.gluPerspective(45.0f, (float)SCREEN_WIDTH/(float)SCREEN_HEIGHT, 4.0f, 4000.0f);
        GLU.gluPerspective(45.0f, (float)displayMode.getWidth()/(float)displayMode.getHeight(), 1.0f , 100.0f);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();

        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        //GL11.glClearDepth(1.0f);

        //GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
        //GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }
    
    protected void clearOpenGl() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glLoadIdentity();
    }
    
    private void dispatchObjectEventControls(ObjectEvent objectEvent) {
        List<Control> cursors = null;
        synchronized(cursorHandler) {
            cursors = cursorHandler.getCursors();
        }
        
        Control activeControl = null;
        LinkedList<ObjectObserverEvent> consumedOoes = new LinkedList<ObjectObserverEvent>();
        ListIterator<Control> listIterator = controls.listIterator(controls.size());
        while(listIterator.hasPrevious()) {
            Control control = listIterator.previous();
            if(cursors.contains(control)) {
                continue;
            }
            
            ObjectEvent copiedObjectEvent = objectEvent.copy();
            // check how many of Ooes are within this control
            LinkedList<ObjectObserverEvent> ooes = new LinkedList<ObjectObserverEvent>();
            for(ObjectObserverEvent ooe : objectEvent.getObjectObserverEvents()) {
                if(!consumedOoes.contains(ooe)) {
                    if(control.isWithin(new Point(ooe.getX(), ooe.getY()))) {
                        ooes.add(ooe);
                        consumedOoes.add(ooe);
                    }
                }
            }
            copiedObjectEvent.setObjectObserverEvents(ooes);
            
            ObjectObserverEvent targetOoe = objectEvent.getTargetObjectObserverEvent();
            if(ooes.contains(targetOoe)) {
                activeControl = control;
            } else {
                targetOoe = null;
            }
            copiedObjectEvent.setTargetObjectObserverEvent(targetOoe);
            
            //log.debug("Ooes: "+ooes.size());
            if(ooes.size() > 0) {
                log.debug(control.hashCode()+": Sending "+ooes.size()+" ooes");
                control.dispatchEvent(copiedObjectEvent);
            }
        }
        
        if((activeControl != null) && (!activeControl.equals(backgroundControl))) {
            if(!activeControl.equals(controls.getLast())) {
                controls.remove(activeControl);
                controls.add(activeControl);
            }
        }
    }
    
    private void dispatchObjectEventCursors(ObjectEvent objectEvent) {
        synchronized(cursorHandler) {
            CursorCollection cursorCollection = cursorHandler.getCursorCollection();
            if(cursorCollection != null) {
                cursorHandler.clearActives();
                if(!ObjectEvent.Status.Ended.equals(objectEvent.getStatus())) {
                //} else {
                    for(ObjectObserverEvent ooe : objectEvent.getObjectObserverEvents()) {
                        Control control = cursorCollection.getCursor(ooe.getId());
                        Size size = control.getSize();
                        control.setPosition(new Point(
                            (ooe.getX() + (size.getWidth() / 2.0f)),
                            (ooe.getY() + (size.getHeight() / 2.0f))
                        ));
                        
                        cursorHandler.setActive(ooe.getId());
                    }
                }
            }
        }
    }
}
