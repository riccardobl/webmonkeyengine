package com.jme3.web.audio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.teavm.jso.typedarrays.Float32Array;
import org.teavm.jso.webaudio.AudioBuffer;
import org.teavm.jso.webaudio.AudioBufferSourceNode;
import org.teavm.jso.webaudio.AudioContext;
import org.teavm.jso.webaudio.AudioListener;
import org.teavm.jso.webaudio.GainNode;
import org.teavm.jso.webaudio.PannerNode;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioParam;
import com.jme3.audio.AudioRenderer;
import com.jme3.audio.AudioSource;
import com.jme3.audio.AudioStream;
import com.jme3.audio.Environment;
import com.jme3.audio.Filter;
import com.jme3.audio.Listener;
import com.jme3.audio.ListenerParam;
import com.jme3.math.Vector3f;
import com.jme3.util.NativeObject;

public class WebAudioRenderer implements AudioRenderer {
    private Logger logger = Logger.getLogger(WebAudioRenderer.class.getName());
    private Map<AudioSource, List<AudioEntry>> howlers = new WeakHashMap<>();

    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    private AtomicInteger bufferId = new AtomicInteger(1);
    private Map<Integer, AudioBuffer> buffersMap = new HashMap<>();


    @Override
    public void initialize() {
    }

    private static class AudioEntry {
        private AudioBufferSourceNode _node;
        private AudioContext _ctx;
        private boolean loop = false;
        private boolean nodeStarted = false;
        

        PannerNode panner;
        GainNode gain;
        int channel = 0;
        double time = 0;

        double duration;
        AudioBuffer buffer;
        boolean positional=true;

        public AudioBufferSourceNode getNode(){//boolean rebuild) {
            // if (rebuild && _node != null) {
            //     if (nodeStarted) {
            //         time = _node.getPlaybackRate().getValue() * duration;
            //     }
            //     _node.stop(0);
            //     _node.disconnect();
            //     panner.disconnect();
            //     gain.disconnect();
            //     _node = null;
            // }
            
            if (_node == null) {
                _node = getContext().createBufferSource();
                _node.setBuffer(buffer);
                if (positional) {
                    reconnectPositional();
                } else {
                    reconnectStatic();
                }
            }

            // if (rebuild && _node != null && nodeStarted) {
            //      _node.setLoopStart(0);
            //     _node.setLoopEnd(duration);
            //     _node.setLoop(loop);
            //     _node.start(0, time);
            // }

            return _node;

        }
        
        // public AudioBufferSourceNode getNode() {
        //     return getNode(false);
        // }


        public AudioContext getContext() {
            if(_ctx==null)   _ctx = AudioContext.create();
            return _ctx;
        }


        public void setLoop(boolean loop) {
            this.loop = loop;
            AudioBufferSourceNode node=getNode();
            node.setLoopStart(0);
            node.setLoopEnd(duration);
            node.setLoop(loop);
        }

        public void start() {
            AudioBufferSourceNode node=getNode();
            if (!nodeStarted) node.start(0,time);
            nodeStarted = true;
            getContext().resume();
            setLoop(loop);
        }

        public void destroy() {
            AudioBufferSourceNode node=getNode();
            node.stop(0);
            node.disconnect();
            panner.disconnect();
            gain.disconnect();

        }

        @Override
        public void finalize() {
            destroy();
        }
        
        public void stop() {
            time = 0;
            getContext().suspend();
        }
        
        public void pause() {
            getContext().suspend();
        }

        AudioEntry() {
        }

        private void reconnectPositional() {
            AudioBufferSourceNode node=getNode();
            node.disconnect();
            panner.disconnect();
            gain.disconnect();
            node.connect(panner);
            panner.connect(gain);
            gain.connect(getContext().getDestination());
            setLoop(loop);

        }

        private void reconnectStatic() {
            AudioBufferSourceNode node=getNode();
            node.disconnect();
            panner.disconnect();
            gain.disconnect();
            node.connect(gain);

            gain.connect(getContext().getDestination());
            setLoop(loop);
        }
    }

    private AudioBuffer updateAudioData(AudioEntry entry, AudioData data) {
        if (data.isUpdateNeeded() || data.getId() == NativeObject.INVALID_ID) {
            int id = NativeObject.INVALID_ID;
            while (id == NativeObject.INVALID_ID || id == 0) {
                id = bufferId.incrementAndGet();
                if (buffersMap.containsKey(id)) id = NativeObject.INVALID_ID;
            }
            int numOfChannels = data.getChannels();
            int length = (int) (data.getDuration() * data.getSampleRate());
            int sampleRate=data.getSampleRate();
            logger.log(Level.FINE, "Create web audio buffer. channels={0} sampleRate={1} duration={2}", new Object[] { numOfChannels,sampleRate,length });
            AudioBuffer buffer = entry.getContext().createBuffer(numOfChannels, length, sampleRate);
            if (data instanceof AudioStream) {
                updateAudioStream((AudioStream) data, buffer);
            } else if (data instanceof com.jme3.audio.AudioBuffer) {
                updateBufferStream((com.jme3.audio.AudioBuffer) data, buffer);
            }

            buffersMap.put(id, buffer);
            data.setId(id);
            data.clearUpdateNeeded();

            return buffer;
        } else {
            return buffersMap.get(data.getId());
        }
    }

    private void swapOrder(byte[] bytes) {
        for (int i = 0; i < bytes.length; i += 2) {
            byte b = bytes[i];
            bytes[i] = bytes[i + 1];
            bytes[i + 1] = b;
        }
    }

    private void audioDataToF32(AudioData ab, ByteBuffer in,  Float32Array outs[]) {
        int bps = ab.getBitsPerSample();
        int inc = bps / 8;
        int channels = ab.getChannels();
        byte channelSample[] = new byte[inc];
        boolean swapOrder = in.order() != ByteOrder.LITTLE_ENDIAN;
        int j = 0;
        for (int i = 0; i < in.limit(); i += inc * channels) {
            for (int c = 0; c < channels; c++) { // interleaved
                Float32Array out = outs[c];
                in.get(channelSample);
                if (swapOrder) swapOrder(channelSample);
                if (bps == 8) {
                    int n = channelSample[0];
                    float fbe;
                    if (n < 0) {
                        fbe = (float) n / 128f;
                    } else {
                        fbe = (float) n / 127f; 
                    }
                    out.set(j, fbe);
                } else if (bps == 16) {
                    short sbe = (short) ((channelSample[1] & 0xFF) << 8 | (channelSample[0] & 0xFF)); 
                    float fbe;
                    if (sbe < 0) {
                        fbe = (float) sbe / 32768f;
                    } else {
                        fbe = (float) sbe / 32767f;
                    }
                    out.set(j, fbe);
                } else if (bps == 24) {
                    int ibe = (int) ((channelSample[2] & 0xFF) << 16 | (channelSample[1] & 0xFF) << 8 | (channelSample[0] & 0xFF));
                    // Extend sign to int32
                    if ((ibe & 0x00800000) > 0) ibe |= 0xFF000000;

                    float fbe;
                    if (ibe < 0) {
                        fbe = (float) ibe / 8388608f;
                    } else {
                        fbe = (float) ibe / 8388607f;
                    }
                    out.set(j, fbe);
                } else {
                    throw new UnsupportedOperationException("Unsupported bits per sample: " + bps);
                }

            }
            j++;
        }

    }

    private void updateAudioStream(AudioStream ab, AudioBuffer buffer) {
        throw new UnsupportedOperationException("AudioStream are not supported yet.");
    }

    private void updateBufferStream(com.jme3.audio.AudioBuffer ab, AudioBuffer buffer) {
        ByteBuffer inputData = ab.getData();
        inputData.rewind();
        Float32Array data[] = new Float32Array[ab.getChannels()];
        for (int i = 0; i < ab.getChannels(); i++) data[i] = Float32Array.create(buffer.getLength());
        audioDataToF32(ab, inputData, data);
        for (int i = 0; i < ab.getChannels(); i++) buffer.copyToChannel(data[i], i, 0);

        inputData.rewind();
    }

    @Override
    public void deleteAudioData(AudioData ad) {
        buffersMap.remove(ad.getId());
    }

    private AudioEntry removeAudioEntry(AudioSource src, int index) {
        if (index == -1) return null;
        AudioEntry entry = null;
        List<AudioEntry> nodes = howlers.get(src);
        if (nodes != null) {
            if (nodes.size() > index) entry = nodes.remove(index);
            if (nodes.isEmpty()) howlers.remove(src);
        }
        entry.destroy();
        return entry;
    }

    private AudioEntry getAudioEntry(AudioSource src, int index) {

        AudioEntry entry = null;
        List<AudioEntry> nodes = howlers.get(src);
        if (nodes == null) {
            nodes = new ArrayList<>();
            howlers.put(src, nodes);
        } else {
            if (index == -1) index = nodes.size();
            if (nodes.size() > index) entry = nodes.get(index);
        }

        if (entry == null) {
            entry = new AudioEntry();
            
            AudioBuffer buff = updateAudioData(entry, src.getAudioData());
            
            while (nodes.size() <= index) nodes.add(null);
            entry.buffer = buff;
            entry.duration=src.getAudioData().getDuration();
            entry.panner = entry.getContext().createPanner();
            entry.gain = entry.getContext().createGain();
            entry.channel = index;
            entry.time = src.getTimeOffset();
            nodes.set(index, entry);

            entry.getNode();
            
          
            // if (src.isPositional()) entry.reconnectPositional();
            // else entry.reconnectStatic();
        } else {
            updateAudioData(entry,src.getAudioData());
        }

        return entry;
    }

    @Override
    public void setListener(Listener listener) {
        Vector3f pos = listener.getLocation();
        Vector3f vel = listener.getVelocity();
        Vector3f dir = listener.getDirection();
        Vector3f up = listener.getUp();
        Collection<List<AudioEntry>> entries = howlers.values();
        for (List<AudioEntry> entryList : entries) {
            for (AudioEntry entry : entryList) {
                AudioListener webListener = entry.getContext().getListener();
                webListener.setPosition(pos.x, pos.y, pos.z);
                // webListener.setVelocity(vel.x, vel.y, vel.z); UNSUPPORTED
                webListener.setOrientation(dir.x, dir.y, dir.z, up.x, up.y, up.z);
            }
        }
        listener.setRenderer(this);
        
    }

    @Override
    public void playSourceInstance(AudioSource src) {
        executor.execute(() -> {
            AudioEntry entry = getAudioEntry(src, -1);
            initPlayback(src, entry);
            entry.start();
        });
    }

    @Override
    public void playSource(AudioSource src) {
        if (src.getStatus() == AudioSource.Status.Playing) return;
        AudioEntry entry = getAudioEntry(src, 0);
        if (src.getStatus() == AudioSource.Status.Stopped) src.setChannel(entry.channel);
        initPlayback(src, entry);
        entry.start();
        src.setStatus(AudioSource.Status.Playing);

    }

    private void initPlayback(AudioSource src, AudioEntry entry){
        
        for (AudioParam p : AudioParam.values()) {
            updateSourceParam(src, p, entry);
        }
    }

    @Override
    public void pauseSource(AudioSource src) {
        if (src.getStatus() == AudioSource.Status.Paused) return;
        AudioEntry entry = getAudioEntry(src, src.getChannel());
        // src.setChannel(entry.channel);
        entry.time = src.getPlaybackTime();
        entry.pause();
        src.setStatus(AudioSource.Status.Paused);
    }

    @Override
    public void pauseAll() {
        for (Entry<AudioSource, List<AudioEntry>> entry : howlers.entrySet()) {
            AudioSource src = entry.getKey();
            for (AudioEntry e : entry.getValue()) {
                e.time = src.getPlaybackTime();
                e.pause();
            }
            src.setStatus(AudioSource.Status.Paused);
        }
    }

    @Override
    public void resumeAll() {
        for (Entry<AudioSource, List<AudioEntry>> entry : howlers.entrySet()) {
            for (AudioEntry e : entry.getValue()) {
                e.start();
            }
            entry.getKey().setStatus(AudioSource.Status.Playing);
        }
    }

    @Override
    public void stopSource(AudioSource src) {
        if (src.getStatus() == AudioSource.Status.Stopped) return;
        if (src.getChannel() == -1) return;

        AudioEntry entry = getAudioEntry(src, src.getChannel());
        entry.time = src.getTimeOffset();
        src.setChannel(entry.channel);
        entry.stop();
        removeAudioEntry(src, src.getChannel());
        src.setChannel(-1);

        src.setStatus(AudioSource.Status.Stopped);
        if (src.getAudioData() instanceof AudioStream) {
            AudioStream stream = (AudioStream) src.getAudioData();
            if (stream.isSeekable()) {
                stream.setTime(0);
            } else {
                stream.close();
            }
        }

    }

    @Override
    public void update(float tpf) {

    }

    @Override
    public void updateSourceParam(AudioSource src, AudioParam param) {
        int chan = src.getChannel();
        AudioEntry entry = getAudioEntry(src, chan);
        updateSourceParam(src, param, entry);
    }
    public void updateSourceParam(AudioSource src, AudioParam param, AudioEntry entry) {
        switch (param) {
            case Position: {
                if (!src.isPositional()) {
                    return;
                }
                // int chan = src.getChannel();
                // AudioEntry entry = getAudioEntry(src, chan);
                PannerNode panner = entry.panner;
                Vector3f pos = src.getPosition();
                panner.setPosition(pos.x, pos.y, pos.z);

                break;
            }
            case Velocity: {
                if (!src.isPositional()) {
                    return;
                }
                // int chan = src.getChannel();
                // AudioEntry entry = getAudioEntry(src, chan);
                // PannerNode panner = entry.panner;
                // Vector3f vel = src.getVelocity();
                // panner.setVelocity(vel.x, vel.y, vel.z);
                break;
            }
            case MaxDistance: {
                if (!src.isPositional()) {
                    return;
                }
                // int chan = src.getChannel();
                // AudioEntry entry = getAudioEntry(src, chan);
                PannerNode panner = entry.panner;
                panner.setMaxDistance(src.getMaxDistance());
                break;
            }
            case RefDistance: {
                if (!src.isPositional()) {
                    return;
                }
                // int chan = src.getChannel();
                // AudioEntry entry = getAudioEntry(src, chan);
                PannerNode panner = entry.panner;
                panner.setRefDistance(src.getRefDistance());
                break;
            }
            case ReverbFilter: {

                break;
            }
            case ReverbEnabled: {

                break;
            }
            case IsPositional: {
                // int chan = src.getChannel();

                // AudioEntry entry = getAudioEntry(src, chan);
                if (src.isPositional()) {

                    entry.reconnectPositional();
                } else {
                    entry.reconnectStatic();
                }
                break;
            }
            case Direction: {
                if (!src.isDirectional()) {
                    return;
                }
                // int chan = src.getChannel();
                // AudioEntry entry = getAudioEntry(src, chan);
                Vector3f dir = src.getDirection();
                PannerNode panner = entry.panner;
                panner.setOrientation(dir.x, dir.y, dir.z);
                break;
            }
            case InnerAngle: {
                if (!src.isDirectional()) {
                    return;
                }
                // int chan = src.getChannel();
                // AudioEntry entry = getAudioEntry(src, chan);
                PannerNode panner = entry.panner;
                panner.setConeInnerAngle(src.getInnerAngle());
                break;
            }
            case OuterAngle: {
                if (!src.isDirectional()) {
                    return;
                }

                // int chan = src.getChannel();
                // AudioEntry entry = getAudioEntry(src, chan);
                PannerNode panner = entry.panner;
                panner.setConeOuterAngle(src.getOuterAngle());
                break;
            }
            case IsDirectional: {
                // int chan = src.getChannel();
                // AudioEntry entry = getAudioEntry(src, chan);
                PannerNode panner = entry.panner;
                if (src.isDirectional()) {
                    updateSourceParam(src, AudioParam.Direction,entry);
                    updateSourceParam(src, AudioParam.InnerAngle,entry);
                    updateSourceParam(src, AudioParam.OuterAngle,entry);
                    panner.setConeOuterGain(0);
                } else {
                    panner.setConeInnerAngle(360);
                    panner.setConeOuterAngle(360);
                    panner.setConeOuterGain(1);
                }
                break;
            }
            case DryFilter: {

                break;
            }
            case Looping: {
                // int chan = src.getChannel();
                // AudioEntry entry = getAudioEntry(src, chan);
                entry.setLoop(src.isLooping());
                
                // if (src.isLooping() && !(src.getAudioData() instanceof
                // AudioStream)) {
                // al.alSourcei(id, AL_LOOPING, AL_TRUE);
                // } else {
                // al.alSourcei(id, AL_LOOPING, AL_FALSE);
                // }
                break;
            }
            case Volume: {
                // int chan = src.getChannel();
                // AudioEntry entry = getAudioEntry(src, chan);
                entry.gain.getGain().setValue(src.getVolume());
                break;
            }
            case Pitch: {
                // int chan = src.getChannel();
                // AudioEntry entry = getAudioEntry(src, chan);
                entry.getNode().getPlaybackRate().setValue(src.getPitch());
                break;
            }
        }
    }

    @Override
    public void updateListenerParam(Listener listener, ListenerParam param) {
        setListener(listener);

    }

    @Override
    public float getSourcePlaybackTime(AudioSource src) {
        int chan = src.getChannel();
        AudioEntry entry = getAudioEntry(src, chan);
        double duration = 0;
        if (src.getAudioData() instanceof AudioStream) {
            AudioStream stream = (AudioStream) src.getAudioData();
            duration = stream.getDuration();
        } else if (src.getAudioData() instanceof com.jme3.audio.AudioBuffer) {
            com.jme3.audio.AudioBuffer buffer = (com.jme3.audio.AudioBuffer) src.getAudioData();
            duration = buffer.getDuration();
        } else {
            throw new UnsupportedOperationException("Unimplemented method 'getSourcePlaybackTime' for "+src.getAudioData().getClass().getName());
        }

        return (float) (entry.getNode().getPlaybackRate().getValue() * duration);
    }

    @Override
    public void deleteFilter(Filter filter) {
    }

    @Override
    public void setEnvironment(Environment env) {
    }

    @Override
    public void cleanup() {
        howlers.clear();
        buffersMap.clear();
        bufferId.set(1);
    }

}
