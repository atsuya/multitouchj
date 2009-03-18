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
package edu.csun.ecs.cs.multitouchj.audio;

import edu.csun.ecs.cs.multitouchj.audio.event.AudioPlayerListener;
import edu.csun.ecs.cs.multitouchj.objectobserver.event.EventListenerManager;

/**
 * @author Atsuya Takagi
 * 
 *         $Id: AudioPlayer.java 54 2009-02-09 20:30:44Z Atsuya Takagi $
 */
public abstract class AudioPlayer extends EventListenerManager {
    public enum Status {
        STARTED, PLAYING, PAUSED, STOPPED, UNKNOWN
    }
    public enum Format {
        MONO_8, MONO_16, STEREO_8, STEREO_16
    }
    
    
    public AudioPlayer(AudioPlayerListener audioPlayerListener)
            throws Exception {
        if(audioPlayerListener == null) {
            throw new Exception("AudioPlayerListener cannot be null.");
        }
        
        addAudioPlayerListener(audioPlayerListener);
    }
    
    public abstract void destroy();
    
    public abstract void start();
    
    public abstract void pause();
    
    public abstract void stop();
    
    public abstract void update();
    
    public abstract Status getStatus();
    
    public abstract int fillBuffer(byte[] data, Format format, int frequency);
    
    protected boolean addAudioPlayerListener(
        AudioPlayerListener audioPlayerListener
        ) {
        addEventListener(AudioPlayerListener.class, audioPlayerListener);
        return true;
    }
}
