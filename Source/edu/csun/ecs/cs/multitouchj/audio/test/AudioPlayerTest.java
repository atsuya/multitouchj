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

import java.nio.ByteBuffer;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC10;
import org.lwjgl.util.WaveData;

import edu.csun.ecs.cs.multitouchj.audio.AudioPlayer;
import edu.csun.ecs.cs.multitouchj.audio.event.AudioPlayerEvent;
import edu.csun.ecs.cs.multitouchj.audio.event.AudioPlayerListener;
import edu.csun.ecs.cs.multitouchj.audio.openal.OpenAlAudioPlayer;

/**
 * @author Atsuya Takagi
 * 
 *         $Id: AudioPlayerTest.java 54 2009-02-09 20:30:44Z Atsuya Takagi $
 */
public class AudioPlayerTest implements AudioPlayerListener {
    private static final int BUFFER_SIZE = 502400;
    private static final int NUMBER_OF_BUFFERS = 2;
    private ByteBuffer data;
    private AudioPlayer.Format format;
    private int frequency;
    private boolean isDone;
    AudioPlayer audioPlayer;
    
    
    @Before
    public void setUp() throws Exception {
        AL.create();
        try {
            System.out.println("Default device: "
                    + ALC10.alcGetString(null,
                            ALC10.ALC_DEFAULT_DEVICE_SPECIFIER));
        } catch(Exception exception) {
            exception.printStackTrace();
        }
    }
    
    @After
    public void cleanUp() {
        AL.destroy();
    }
    
    @Test
    public void basicTest() throws Exception {
        WaveData waveData = WaveData.create("MeltShort.wav");
        data = waveData.data;
        format = AudioPlayer.Format.STEREO_16;
        frequency = waveData.samplerate;
        System.out.println("Freq: " + frequency);
        
        audioPlayer = new OpenAlAudioPlayer(this, NUMBER_OF_BUFFERS,
                BUFFER_SIZE);
        for(int i = 0; i < 2; i++) {
            System.out.println("::\n:: LOOP " + i + " ::\n::");
            try {
                play();
            } catch(Exception exception) {
                exception.printStackTrace();
            }
            
            data.position(0).limit(data.capacity());
            for(int j = 0; j < NUMBER_OF_BUFFERS; j++) {
                bufferEmptied(null);
            }
        }
        
        audioPlayer.destroy();
    }
    
    private void play() throws Exception {
        long currentTime = System.currentTimeMillis();
        Random random = new Random();
        
        audioPlayer.start();
        while(!isDone) {
            Thread.sleep(200);
            if((System.currentTimeMillis() - currentTime) > 5000) {
                currentTime = System.currentTimeMillis();
                int value = random.nextInt(3);
                if(value == 0) {
                    System.out.println("Stopping player.");
                    audioPlayer.stop();
                } else {
                    System.out.println("Pausing player.");
                    audioPlayer.pause();
                }
                Thread.sleep(500);
                audioPlayer.start();
            }
            
            audioPlayer.update();
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.orchestra.event.AudioPlayerListener#audioPaused(org.orchestra.event
     * .AudioPlayerEvent)
     */
    public void audioPaused(AudioPlayerEvent audioPlayerEvent) {
        System.out.println("Audio Paused.");
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.orchestra.event.AudioPlayerListener#audioStarted(org.orchestra.event
     * .AudioPlayerEvent)
     */
    public void audioStarted(AudioPlayerEvent audioPlayerEvent) {
        isDone = false;
        System.out.println("Audio Started.");
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
        System.out.println("Audio Stopped.");
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.orchestra.event.AudioPlayerListener#bufferEmptied(org.orchestra.event
     * .AudioPlayerEvent)
     */
    public void bufferEmptied(AudioPlayerEvent audioPlayerEvent) {
        System.out.println("Buffer Emptied.");
        
        int bufferSize = BUFFER_SIZE;
        if(data.remaining() < bufferSize) {
            bufferSize = data.remaining();
        }
        
        if(bufferSize > 0) {
            byte[] buffer = new byte[bufferSize];
            data.get(buffer, 0, bufferSize);
            
            AudioPlayer audioPlayer = this.audioPlayer;
            // if(audioPlayer == null)
            // {
            audioPlayer = audioPlayerEvent.getAudioPlayer();
            // }
            int byteUsed = audioPlayer.fillBuffer(buffer, format, frequency);
            
            System.out.println("Filling data: " + byteUsed + "/" + bufferSize
                    + ".");
        }
    }
}
