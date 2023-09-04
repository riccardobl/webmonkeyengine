package com.jme3.web;

import java.lang.reflect.InvocationTargetException;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.app.state.AppState;
import com.jme3.app.state.ConstantVerifierState;
import com.jme3.audio.AudioListenerState;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeSystem;
import com.jme3.util.BufferAllocatorFactory;
import com.jme3.web.context.HeapAllocator;
import com.jme3.web.context.JmeWebSystem;
import com.jme3.web.context.NativeUtils;
import com.jme3.web.demo.TestShadows;
import com.jme3.web.demo.TestInstancing;
import com.jme3.web.demo.TestPBR2;
import com.jme3.web.demo.TestAudio;
import com.jme3.web.demo.TestDemo1;
import com.jme3.web.demo.TestPBRSimple;
import com.jme3.web.demo.TestPhysics;

public class WebApp {

    public static void main(String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException,
            InstantiationException, NoSuchFieldException {

        String implementation = BufferAllocatorFactory.PROPERTY_BUFFER_ALLOCATOR_IMPLEMENTATION;
        System.setProperty(implementation, HeapAllocator.class.getName());

        JmeSystem.setSystemDelegate(new JmeWebSystem());

        String demoToRun = NativeUtils.getPostedMessages()[0];

        AppSettings settings = new AppSettings(true);
        settings.setEmulateMouse(true);
        settings.setWidth(1024);
        settings.setResizable(true);
        settings.setHeight(768);
        settings.setGammaCorrection(true);
        settings.setSamples(0);
        settings.setFullscreen(false);
        
        AppState appStates[] = {

            new StatsAppState(), new FlyCamAppState(), new AudioListenerState(), new DebugKeysAppState(),
                new ConstantVerifierState()
         };

        SimpleApplication app = null;

        switch (demoToRun) {
            default:
            case "pbr":
                app = new TestPBRSimple(appStates);
                break;
            case "shadows":
                app = new TestShadows(appStates);
                break;
            case "physics":
                app = new TestPhysics(appStates);
                break;
            case "audio":
                app = new TestAudio(appStates);
                break;
            case "inst":
                app = new TestInstancing(appStates);
                break;
            case "demo1":
                app = new TestDemo1(appStates);
                break;
            case "pbr2":
                app = new TestPBR2(appStates);
                break;
        }

        app.setSettings(settings);
        app.start();

    }
}