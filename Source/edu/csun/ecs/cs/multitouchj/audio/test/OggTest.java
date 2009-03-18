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
package edu.csun.ecs.cs.multitouchj.audio.test;

import java.io.FileInputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lwjgl.openal.AL;

import edu.csun.ecs.cs.multitouchj.audio.AudioPlayer;
import edu.csun.ecs.cs.multitouchj.audio.event.AudioPlayerEvent;
import edu.csun.ecs.cs.multitouchj.audio.event.AudioPlayerListener;
import edu.csun.ecs.cs.multitouchj.audio.openal.OpenAlAudioPlayer;

import trb.sound.OggInputStream;

/**
 * @author Atsuya Takagi
 * 
 *         $Id: OggTest.java 54 2009-02-09 20:30:44Z Atsuya Takagi $
 */
public class OggTest implements AudioPlayerListener {
    private static final int BUFFER_SIZE = 502400;
    private static final int NUMBER_OF_BUFFERS = 2;
    private AudioPlayer audioPlayer;
    private AudioPlayer.Format format;
    private int frequency;
    private byte[] buffer;
    private OggInputStream ois;
    private boolean isDone;
    
    
    @Before
    public void setUp() throws Exception {
        AL.create();
    }
    
    @After
    public void cleanUp() {
        AL.destroy();
    }
    
    @Test
    public void oggTest() throws Exception {
        buffer = new byte[BUFFER_SIZE];
        FileInputStream fis = new FileInputStream("Resources/SIAM SHADE.ogg");
        ois = new OggInputStream(fis);
        
        format = AudioPlayer.Format.STEREO_16;
        if(ois.getFormat() == OggInputStream.FORMAT_MONO16) {
            format = AudioPlayer.Format.MONO_16;
        }
        frequency = ois.getRate();
        
        isDone = false;
        audioPlayer = new OpenAlAudioPlayer(this, NUMBER_OF_BUFFERS,
                BUFFER_SIZE);
        audioPlayer.start();
        while(!isDone) {
            audioPlayer.update();
            Thread.sleep(200);
        }
        audioPlayer.destroy();
        
        ois.close();
        fis.close();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.orchestra.event.AudioPlayerListener#audioPaused(org.orchestra.event
     * .AudioPlayerEvent)
     */
    public void audioPaused(AudioPlayerEvent audioPlayerEvent) {
        // TODO Auto-generated method stub
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.orchestra.event.AudioPlayerListener#audioStarted(org.orchestra.event
     * .AudioPlayerEvent)
     */
    public void audioStarted(AudioPlayerEvent audioPlayerEvent) {
        // TODO Auto-generated method stub
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.orchestra.event.AudioPlayerListener#audioStopped(org.orchestra.event
     * .AudioPlayerEvent)
     */
    public void audioStopped(AudioPlayerEvent audioPlayerEvent) {
        isDone = true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.orchestra.event.AudioPlayerListener#bufferEmptied(org.orchestra.event
     * .AudioPlayerEvent)
     */
    public void bufferEmptied(AudioPlayerEvent audioPlayerEvent) {
        // System.out.println("Buffer Emptied.");
        
        try {
            int byteRead = ois.read(buffer, 0, BUFFER_SIZE);
            if(byteRead > 0) {
                System.out.println("Read " + byteRead + " bytes.");
                
                byte[] data = new byte[byteRead];
                System.arraycopy(buffer, 0, data, 0, data.length);
                
                AudioPlayer audioPlayer = this.audioPlayer;
                // if(audioPlayer == null)
                // {
                audioPlayer = audioPlayerEvent.getAudioPlayer();
                // }
                audioPlayer.fillBuffer(data, format, frequency);
            }
        } catch(Exception exception) {
            exception.printStackTrace();
        }
    }
}
