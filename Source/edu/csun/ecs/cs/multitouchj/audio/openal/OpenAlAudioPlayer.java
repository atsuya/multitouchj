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
package edu.csun.ecs.cs.multitouchj.audio.openal;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import edu.csun.ecs.cs.multitouchj.audio.AudioPlayer;
import edu.csun.ecs.cs.multitouchj.audio.event.AudioPlayerEvent;
import edu.csun.ecs.cs.multitouchj.audio.event.AudioPlayerListener;

/**
 * @author Atsuya Takagi
 * 
 *         $Id: OpenAlAudioPlayer.java 54 2009-02-09 20:30:44Z Atsuya Takagi $
 */
public class OpenAlAudioPlayer extends AudioPlayer {
    private static Log log = LogFactory.getLog(OpenAlAudioPlayer.class);
    private IntBuffer sources;
    private IntBuffer buffers;
    private ByteBuffer dataBuffer;
    private IntBuffer unqueuedBuffers;
    private IntBuffer queuingBuffers;
    private Hashtable<Integer, Boolean> bufferStatus;
    private int bufferSize;
    private boolean isStoppedEventSent;
    
    
    public OpenAlAudioPlayer(AudioPlayerListener audioPlayerListener,
            int numberOfBuffers, int bufferSize) throws Exception {
        super(audioPlayerListener);
        
        // buffer related
        this.bufferSize = bufferSize;
        dataBuffer = BufferUtils.createByteBuffer(this.bufferSize);
        unqueuedBuffers = BufferUtils.createIntBuffer(numberOfBuffers);
        queuingBuffers = BufferUtils.createIntBuffer(numberOfBuffers);
        
        buffers = BufferUtils.createIntBuffer(numberOfBuffers);
        buffers.position(0).limit(buffers.capacity());
        
        sources = BufferUtils.createIntBuffer(1);
        sources.position(0).limit(sources.capacity());
        
        isStoppedEventSent = false;
        setUpOpenAl();
        initializeBufferStatus();
        
        // request to fill buffers
        for(int i = 0; i < numberOfBuffers; i++) {
            eventOccurred("bufferEmptied");
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.orchestra.audio.AudioPlayer#fillBuffer(byte[])
     */
    @Override
    public synchronized int fillBuffer(byte[] data, Format format, int frequency) {
        int numberOfBuffers = getNumberOfBuffers();
        int bufferIndex = -1;
        for(int i = 0; i < numberOfBuffers; i++) {
            if(!getBufferStatus(buffers.get(i))) {
                bufferIndex = i;
                break;
            }
        }
        
        int dataBuffered = 0;
        int bufferSize = getBufferSize();
        if(bufferIndex != -1) {
            dataBuffered = data.length;
            if(dataBuffered > bufferSize) {
                dataBuffered = bufferSize;
            }
            
            fillBuffer(buffers.get(bufferIndex), data, 0, dataBuffered, format,
                    frequency);
            
            queuingBuffers.clear();
            queuingBuffers.put(buffers.get(bufferIndex));
            queuingBuffers.flip();
            AL10.alSourceQueueBuffers(sources.get(0), queuingBuffers);
        }
        
        return dataBuffered;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.orchestra.audio.AudioPlayer#getStatus()
     */
    @Override
    public synchronized Status getStatus() {
        Status status = Status.UNKNOWN;
        
        int state = AL10.alGetSourcei(sources.get(0), AL10.AL_SOURCE_STATE);
        switch(state) {
            case AL10.AL_PLAYING:
                status = Status.PLAYING;
                break;
            case AL10.AL_PAUSED:
                status = Status.PAUSED;
                break;
            case AL10.AL_STOPPED:
                status = Status.STOPPED;
                break;
        }
        
        return status;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.orchestra.audio.AudioPlayer#pause()
     */
    @Override
    public synchronized void pause() {
        if(!getStatus().equals(Status.PAUSED)) {
            AL10.alSourcePause(sources.get(0));
            eventOccurred("audioPaused");
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.orchestra.audio.AudioPlayer#start()
     */
    @Override
    public synchronized void start() {
        if(!getStatus().equals(Status.PLAYING)) {
            AL10.alSourcePlay(sources.get(0));
            isStoppedEventSent = false;
            eventOccurred("audioStarted");
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.orchestra.audio.AudioPlayer#stop()
     */
    @Override
    public synchronized void stop() {
        if(!getStatus().equals(Status.STOPPED)) {
            AL10.alSourceStop(sources.get(0));
            // AL10.alSourceRewind(sources.get(0));
            isStoppedEventSent = true;
            eventOccurred("audioStopped");
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.orchestra.audio.AudioPlayer#destroy()
     */
    @Override
    public synchronized void destroy() {
        if(!Status.STOPPED.equals(getStatus())) {
            stop();
        }
        
        cleanUpOpenAl();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.orchestra.audio.AudioPlayer#update()
     */
    @Override
    public synchronized void update() {
        IntBuffer emptyBuffers = unqueueEmptyBuffers();
        for(int i = 0; i < emptyBuffers.limit(); i++) {
            setBufferStatus(emptyBuffers.get(i), false);
            eventOccurred("bufferEmptied");
        }
        
        if((!isStoppedEventSent) && (getStatus().equals(Status.STOPPED))) {
            eventOccurred("audioStopped");
        }
    }
    
    public synchronized int getNumberOfBuffers() {
        return buffers.limit();
    }
    
    public synchronized int getBufferSize() {
        return bufferSize;
    }
    
    private void setUpOpenAl() throws Exception {
        AL10.alGenBuffers(buffers);
        AL10.alGenSources(sources);
    }
    
    private void cleanUpOpenAl() {
        unqueueEmptyBuffers();
        
        AL10.alDeleteSources(sources);
        AL10.alDeleteBuffers(buffers);
    }
    
    private void initializeBufferStatus() {
        // areBuffersQueued = false;
        bufferStatus = new Hashtable<Integer, Boolean>();
        for(int i = 0; i < buffers.limit(); i++) {
            setBufferStatus(buffers.get(i), false);
        }
    }
    
    private boolean getBufferStatus(int bufferId) {
        boolean result = true;
        if(bufferStatus.containsKey(bufferId)) {
            result = bufferStatus.get(bufferId);
        }
        
        return result;
    }
    
    private void setBufferStatus(int bufferId, boolean value) {
        bufferStatus.put(bufferId, value);
    }
    
    private void fillBuffer(int bufferId, byte[] data, int dataStartIndex,
            int dataEndIndex, Format format, int frequency) {
        dataBuffer.clear();
        dataBuffer.put(data, dataStartIndex, (dataEndIndex - dataStartIndex));
        dataBuffer.flip();
        
        int openAlFormat = AL10.AL_FORMAT_MONO8;
        if(Format.MONO_16.equals(format)) {
            openAlFormat = AL10.AL_FORMAT_MONO16;
        } else if(Format.STEREO_8.equals(format)) {
            openAlFormat = AL10.AL_FORMAT_STEREO8;
        } else if(Format.STEREO_16.equals(format)) {
            openAlFormat = AL10.AL_FORMAT_STEREO16;
        }
        
        AL10.alBufferData(bufferId, openAlFormat, dataBuffer, frequency);
        setBufferStatus(bufferId, true);
    }
    
    private void eventOccurred(String methodName) {
        try {
            AudioPlayerEvent ape = new AudioPlayerEvent(this);
            notifyEventListeners(AudioPlayerListener.class, methodName,
                    new Class[] { AudioPlayerEvent.class },
                    new Object[] { ape });
        } catch(Exception exception) {
            exception.printStackTrace();
        }
    }
    
    private IntBuffer unqueueEmptyBuffers() {
        int processedBuffers = AL10.alGetSourcei(sources.get(0),
                AL10.AL_BUFFERS_PROCESSED);
        unqueuedBuffers.clear();
        unqueuedBuffers.limit(processedBuffers);
        
        if(processedBuffers > 0) {
            AL10.alSourceUnqueueBuffers(sources.get(0), unqueuedBuffers);
        }
        
        return unqueuedBuffers;
    }
}
