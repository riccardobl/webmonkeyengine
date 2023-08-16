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
    private static final int SAMPLE_RATE=44100;


    @Override
    public void initialize() {
    }

    private static class AudioEntry {
        private AudioBufferSourceNode _node;
        private WebAudioContext _ctx;
        private boolean loop = false;
        private boolean nodeStarted = false;
        

        PannerNode panner;
        GainNode gain;
        int channel = 0;
        double time = 0;

        double duration;
        AudioBuffer buffer;
        boolean positional = true;
 
        public AudioBufferSourceNode getNode(){//boolean rebuild) {
            if (_node == null) {
                _node = getContext().createBufferSource();
                _node.setBuffer(buffer);
                if (positional) {
                    reconnectPositional();
                } else {
                    reconnectStatic();
                }
            }
            return _node;

        }
        
        public WebAudioContext getContext() {
             
            if(_ctx==null)   {
                WebAudioContextOptions options=WebAudioContextOptions.create();
                options.setSampleRate(SAMPLE_RATE);
                options.setLatencyHint("interactive");
                _ctx = WebAudioContext.create(options);
            }
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
            while (id == NativeObject.INVALID_ID) {
                id = bufferId.incrementAndGet();
                if (buffersMap.containsKey(id)) id = NativeObject.INVALID_ID;
            }
            
            int numOfChannels = data.getChannels();
            WebAudioContext ctx = entry.getContext();
            int lengthInSamples = (int) (data.getDuration() * ctx.getSampleRate());

            logger.log(Level.FINE, "Create web audio buffer. channels={0} sampleRate={1} duration={2}", new Object[] { numOfChannels, ctx.getSampleRate(),lengthInSamples });
            AudioBuffer buffer = entry.getContext().createBuffer(numOfChannels, lengthInSamples,  ctx.getSampleRate());
         
            if (data instanceof AudioStream) {
                updateAudioStream((AudioStream) data, buffer, data.getSampleRate(), (int)ctx.getSampleRate(),lengthInSamples);
            } else if (data instanceof com.jme3.audio.AudioBuffer) {
                updateBufferStream((com.jme3.audio.AudioBuffer) data, buffer,data.getSampleRate(), (int)ctx.getSampleRate(),lengthInSamples);
            }

            
            data.setId(id);
            data.clearUpdateNeeded();

            buffersMap.put(id, buffer);

            return buffer;
        } else {
            return buffersMap.get(data.getId());
        }
    }
 

    private void reverseOrder(byte[] bytes) {
        for (int i = 0; i < bytes.length / 2; i++) {
            byte temp = bytes[i];
            bytes[i] = bytes[bytes.length - i - 1];
            bytes[bytes.length - i - 1] = temp;
        }
    }

    private void write(byte[] channelSample, int j, int bps, boolean swapOrder,  Float32Array out ) {
        if (swapOrder) reverseOrder(channelSample);
        double dcOffset = 0;
        if (bps == 8) {
            int n = channelSample[0];
            float fbe;
            if (n < 0) {
                fbe = (float)((((double) n)-dcOffset) / 128.);
            } else {
                fbe = (float)((((double) n)+dcOffset) / 127.);
            }
            out.set(j, fbe);
        } else if (bps == 16) {
            short sbe = (short) ((channelSample[1] & 0xFF) << 8 | (channelSample[0] & 0xFF));
            float fbe;
            if (sbe < 0) {
                fbe = (float) ((((double)sbe)-dcOffset) / 32768.);
            } else {
                fbe = (float) ((((double)sbe)+dcOffset) / 32767.);
            }
            out.set(j, fbe);
        } else if (bps == 24) {
            int ibe = (int) ((channelSample[2] & 0xFF) << 16 | (channelSample[1] & 0xFF) << 8 | (channelSample[0] & 0xFF));
            // Extend sign to int32
            if ((ibe & 0x00800000) > 0) ibe |= 0xFF000000;

            float fbe;
            if (ibe < 0) {
                fbe = (float) ((((double)ibe)-dcOffset) / 8388608.);
            } else {
                fbe = (float) ((((double)ibe)+dcOffset) / 8388607.);
            }
            out.set(j, fbe);
        } else {
            throw new UnsupportedOperationException("Unsupported bits per sample: " + bps);
        }
    }

    private void audioDataToF32(AudioData ab, ByteBuffer in, Float32Array outs[],int srcSampleRate, int dstSampleRate) {
        int bps = ab.getBitsPerSample();
        int channels = ab.getChannels();
        byte channelSample[] = new byte[bps/8];
        boolean swapOrder = in.order() != ByteOrder.nativeOrder();

        double samplePos = 0;
        double sampleInc = (double)srcSampleRate /(double) dstSampleRate;
 
        for (int outPos = 0; outPos < outs[0].getLength();outPos++) {
            for (int c = 0; c < channels; c++) { // interleaved
                
                int pos = (int) (samplePos) * channels * channelSample.length;
                pos += c * channelSample.length;
                
                in.position(pos);
                in.get(channelSample);

                write(channelSample, outPos, bps, swapOrder, outs[c]);
            }
            samplePos += sampleInc;
        }
    }

    private void updateAudioStream(AudioStream ab, AudioBuffer buffer, int srcSampleRate, int destSampleRate, int lengthInSamples) {
        ByteBuffer inputData = ByteBuffer.allocateDirect(lengthInSamples* (ab.getBitsPerSample()/8));
        byte chunk[] = new byte[1024];
        int read = 0;
        while ((read = ab.readSamples(chunk)) > 0) {
            inputData.put(chunk, 0, read);
        }
        inputData.rewind();
        Float32Array data[] = new Float32Array[ab.getChannels()];
        for (int i = 0; i < ab.getChannels(); i++) data[i] = Float32Array.create(lengthInSamples);
        audioDataToF32(ab, inputData, data,srcSampleRate,destSampleRate);
        for (int i = 0; i < ab.getChannels(); i++) buffer.copyToChannel(data[i], i, 0);
        inputData.rewind();

    }

    private void updateBufferStream(com.jme3.audio.AudioBuffer ab, AudioBuffer buffer, int srcSampleRate, int destSampleRate, int lengthInSamples) {
        ByteBuffer inputData = ab.getData();
        inputData.rewind();
        Float32Array data[] = new Float32Array[ab.getChannels()];
        for (int i = 0; i < ab.getChannels(); i++) data[i] = Float32Array.create(lengthInSamples);
        audioDataToF32(ab, inputData, data,srcSampleRate,destSampleRate);
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
        }  
        
        
        if (index == -1) index = nodes.size();
        if (nodes.size() > index) entry = nodes.get(index);
        

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
