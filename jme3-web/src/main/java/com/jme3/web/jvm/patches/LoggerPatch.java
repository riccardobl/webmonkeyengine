package com.jme3.web.jvm.patches;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.teavm.classlib.java.lang.TSystem;
import org.teavm.classlib.java.util.logging.TLevel;

public abstract class LoggerPatch {
    public abstract void log(TLevel level, String msg);

    public void log(Level level, String msg, Object param1) {
        try {
            msg += " " + param1;
            TSystem.out().println(msg);
        } catch (Exception e) {
            e.printStackTrace();
            // TSystem.out().println(msg);
        }

    }

    public void log(Level level, String msg, Throwable thrown) {
        try {

            thrown.printStackTrace();
            msg += " " + thrown.getMessage();
            TSystem.out().println(msg);
        } catch (Exception e) {
            e.printStackTrace();
            // TSystem.out().println(msg);
        }
    }

    public void log(Level level, String msg, Object args[]) {
        try {

            Object[] os = (Object[]) args;
            for (Object o : os) {
                msg += " " + o;
            }
            TSystem.out().println(msg);
        } catch (Exception e) {
            e.printStackTrace();
            // TSystem.out().println(msg);
        }
    }

    public void logp(Level level, String sourceClass, String sourceMethod, String msg, Throwable thrown) {
        msg += " " + sourceClass;
        msg += " " + sourceMethod;
        msg += " " + thrown.getMessage();
        TSystem.out().println(msg);
    }

    public void logp(Level level, String sourceClass, String sourceMethod, String msg) {
        msg += " " + sourceClass;
        msg += " " + sourceMethod;
        TSystem.out().println(msg);
    }

    public void throwing(String sourceClass, String sourceMethod, Throwable thrown) {
        String msg = "";
        msg += sourceClass;
        msg += " " + sourceMethod;
        msg += " " + msg;
        try {

            msg += " " + thrown.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            TSystem.out().println(msg);
        }
        TSystem.out().println(msg);
        thrown.printStackTrace();
    }

}
