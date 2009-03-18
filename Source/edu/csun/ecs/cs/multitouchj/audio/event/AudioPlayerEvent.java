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
package edu.csun.ecs.cs.multitouchj.audio.event;

import edu.csun.ecs.cs.multitouchj.audio.AudioPlayer;
import edu.csun.ecs.cs.multitouchj.objectobserver.event.Event;

/**
 * @author Atsuya Takagi
 * 
 *         $Id: AudioPlayerEvent.java 54 2009-02-09 20:30:44Z Atsuya Takagi $
 */
public class AudioPlayerEvent extends Event {
    public AudioPlayerEvent(AudioPlayer audioPlayer) {
        super(audioPlayer);
    }
    
    public AudioPlayer getAudioPlayer() {
        return (AudioPlayer) getSource();
    }
}
