package com.jme3.web.input;

import java.util.HashMap;
import java.util.Map;

import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;

/**
 * The majority of this class was generated with Github Copilot and was not tested.
 */
public class KeyMapper {

    private static class JSKeyCodes {
        public static final int KEY_ESCAPE = 27;
        public static final int KEY_1 = 49;
        public static final int KEY_2 = 50;
        public static final int KEY_3 = 51;
        public static final int KEY_4 = 52;
        public static final int KEY_5 = 53;
        public static final int KEY_6 = 54;
        public static final int KEY_7 = 55;
        public static final int KEY_8 = 56;
        public static final int KEY_9 = 57;
        public static final int KEY_0 = 48;
        public static final int KEY_MINUS = 189;
        public static final int KEY_EQUALS = 187;
        public static final int KEY_BACK = 8;
        public static final int KEY_TAB = 9;
        public static final int KEY_Q = 81;
        public static final int KEY_W = 87;
        public static final int KEY_E = 69;
        public static final int KEY_R = 82;
        public static final int KEY_T = 84;
        public static final int KEY_Y = 89;
        public static final int KEY_U = 85;
        public static final int KEY_I = 73;
        public static final int KEY_O = 79;
        public static final int KEY_P = 80;
        public static final int KEY_LBRACKET = 219;
        public static final int KEY_RBRACKET = 221;
        public static final int KEY_RETURN = 13;
        public static final int KEY_LCONTROL = 17;
        public static final int KEY_A = 65;
        public static final int KEY_S = 83;
        public static final int KEY_D = 68;
        public static final int KEY_F = 70;
        public static final int KEY_G = 71;
        public static final int KEY_H = 72;
        public static final int KEY_J = 74;
        public static final int KEY_K = 75;
        public static final int KEY_L = 76;
        public static final int KEY_SEMICOLON = 186;
        public static final int KEY_APOSTROPHE = 222;
        public static final int KEY_GRAVE = 192;
        public static final int KEY_LSHIFT = 16;
        public static final int KEY_BACKSLASH = 220;
        public static final int KEY_Z = 90;
        public static final int KEY_X = 88;
        public static final int KEY_C = 67;
        public static final int KEY_V = 86;
        public static final int KEY_B = 66;
        public static final int KEY_N = 78;
        public static final int KEY_M = 77;
        public static final int KEY_COMMA = 188;
        public static final int KEY_PERIOD = 190;
        public static final int KEY_SLASH = 191;
        public static final int KEY_RSHIFT = 16;
        public static final int KEY_MULTIPLY = 106;
        public static final int KEY_LMENU = 18;
        public static final int KEY_SPACE = 32;
        public static final int KEY_CAPITAL = 20;
        public static final int KEY_F1 = 112;
        public static final int KEY_F2 = 113;
        public static final int KEY_F3 = 114;
        public static final int KEY_F4 = 115;
        public static final int KEY_F5 = 116;
        public static final int KEY_F6 = 117;
        public static final int KEY_F7 = 118;
        public static final int KEY_F8 = 119;
        public static final int KEY_F9 = 120;
        public static final int KEY_F10 = 121;
        public static final int KEY_NUMLOCK = 144;
        public static final int KEY_SCROLL = 145;
        public static final int KEY_NUMPAD7 = 103;
        public static final int KEY_NUMPAD8 = 104;
        public static final int KEY_NUMPAD9 = 105;
        public static final int KEY_SUBTRACT = 109;
        public static final int KEY_NUMPAD4 = 100;
        public static final int KEY_NUMPAD5 = 101;
        public static final int KEY_NUMPAD6 = 102;
        public static final int KEY_ADD = 107;
        public static final int KEY_NUMPAD1 = 97;
        public static final int KEY_NUMPAD2 = 98;
        public static final int KEY_NUMPAD3 = 99;
        public static final int KEY_NUMPAD0 = 96;
        public static final int KEY_DECIMAL = 110;
        public static final int KEY_F11 = 122;
        public static final int KEY_F12 = 123;
        public static final int KEY_F13 = 124;
        public static final int KEY_F14 = 125;
        public static final int KEY_F15 = 126;
        public static final int KEY_KANA = 0; // Kana is not directly mappable
                                              // in JavaScript
        public static final int KEY_CONVERT = 0; // Convert is not directly
                                                 // mappable in JavaScript
        public static final int KEY_NOCONVERT = 0; // NoConvert is not directly
                                                   // mappable in JavaScript
        public static final int KEY_YEN = 0; // Yen is not directly mappable in
                                             // JavaScript
        public static final int KEY_NUMPADEQUALS = 0; // NumpadEquals is not
                                                      // directly mappable in
                                                      // JavaScript
        public static final int KEY_CIRCUMFLEX = 0; // Circumflex is not
                                                    // directly mappable in
                                                    // JavaScript
        public static final int KEY_AT = 0; // At is not directly mappable in
                                            // JavaScript
        public static final int KEY_COLON = 57;

        public static final int KEY_UNDERLINE = 173;
        
        public static final int KEY_KANJI = 0; // Kanji is not directly mappable
                                               // in JavaScript
        public static final int KEY_STOP = 0; // Stop is not directly mappable
                                              // in JavaScript
        public static final int KEY_AX = 0; // Ax is not directly mappable in
                                            // JavaScript
        public static final int KEY_UNLABELED = 0; // Unlabeled is not directly
                                                   // mappable in JavaScript
        public static final int KEY_PRTSCR = 44;
        public static final int KEY_NUMPADENTER = 13;
        public static final int KEY_RCONTROL = 17;
        public static final int KEY_NUMPADCOMMA = 194;
        public static final int KEY_DIVIDE = 111;
        public static final int KEY_SYSRQ = 0; // SysRq is not directly mappable
                                               // in JavaScript
        public static final int KEY_RMENU = 18;
        public static final int KEY_PAUSE = 19;
        public static final int KEY_HOME = 36;
        public static final int KEY_UP = 38;
        public static final int KEY_PRIOR = 33;
        public static final int KEY_PGUP = KEY_PRIOR;
        public static final int KEY_LEFT = 37;
        public static final int KEY_RIGHT = 39;
        public static final int KEY_END = 35;
        public static final int KEY_DOWN = 40;
        public static final int KEY_NEXT = 34;
        public static final int KEY_PGDN = KEY_NEXT;
        public static final int KEY_INSERT = 45;
        public static final int KEY_DELETE = 46;
        public static final int KEY_LMETA = 91;
        public static final int KEY_RMETA = 92;
        public static final int KEY_APPS = 93;
        public static final int KEY_POWER = 0; // Power is not directly mappable
                                               // in JavaScript
        public static final int KEY_SLEEP = 0; // Sleep is not directly mappable
                                               // in JavaScript
        public static final int KEY_LAST = 0xE0;

        
    }

    private static final Map<Integer, Integer> KEY_MAP = new HashMap<>();

    static {
        KEY_MAP.put(JSKeyCodes.KEY_BACK, KeyInput.KEY_BACK);
        KEY_MAP.put(JSKeyCodes.KEY_TAB, KeyInput.KEY_TAB);
        KEY_MAP.put(JSKeyCodes.KEY_RETURN, KeyInput.KEY_RETURN);
        KEY_MAP.put(JSKeyCodes.KEY_LSHIFT, KeyInput.KEY_LSHIFT);
        KEY_MAP.put(JSKeyCodes.KEY_LCONTROL, KeyInput.KEY_LCONTROL);
        KEY_MAP.put(JSKeyCodes.KEY_LMENU, KeyInput.KEY_LMENU);
        KEY_MAP.put(JSKeyCodes.KEY_PAUSE, KeyInput.KEY_PAUSE);
        KEY_MAP.put(JSKeyCodes.KEY_CAPITAL, KeyInput.KEY_CAPITAL);
        KEY_MAP.put(JSKeyCodes.KEY_ESCAPE, KeyInput.KEY_ESCAPE);
        KEY_MAP.put(JSKeyCodes.KEY_SPACE, KeyInput.KEY_SPACE);
        KEY_MAP.put(JSKeyCodes.KEY_PGUP, KeyInput.KEY_PGUP);
        KEY_MAP.put(JSKeyCodes.KEY_PGDN, KeyInput.KEY_PGDN);
        KEY_MAP.put(JSKeyCodes.KEY_END, KeyInput.KEY_END);
        KEY_MAP.put(JSKeyCodes.KEY_HOME, KeyInput.KEY_HOME);
        KEY_MAP.put(JSKeyCodes.KEY_LEFT, KeyInput.KEY_LEFT);
        KEY_MAP.put(JSKeyCodes.KEY_UP, KeyInput.KEY_UP);
        KEY_MAP.put(JSKeyCodes.KEY_RIGHT, KeyInput.KEY_RIGHT);
        KEY_MAP.put(JSKeyCodes.KEY_DOWN, KeyInput.KEY_DOWN);
        KEY_MAP.put(JSKeyCodes.KEY_SYSRQ, KeyInput.KEY_SYSRQ);
        KEY_MAP.put(JSKeyCodes.KEY_INSERT, KeyInput.KEY_INSERT);
        KEY_MAP.put(JSKeyCodes.KEY_DELETE, KeyInput.KEY_DELETE);
        KEY_MAP.put(JSKeyCodes.KEY_0, KeyInput.KEY_0);
        KEY_MAP.put(JSKeyCodes.KEY_1, KeyInput.KEY_1);
        KEY_MAP.put(JSKeyCodes.KEY_2, KeyInput.KEY_2);
        KEY_MAP.put(JSKeyCodes.KEY_3, KeyInput.KEY_3);
        KEY_MAP.put(JSKeyCodes.KEY_4, KeyInput.KEY_4);
        KEY_MAP.put(JSKeyCodes.KEY_5, KeyInput.KEY_5);
        KEY_MAP.put(JSKeyCodes.KEY_6, KeyInput.KEY_6);
        KEY_MAP.put(JSKeyCodes.KEY_7, KeyInput.KEY_7);
        KEY_MAP.put(JSKeyCodes.KEY_8, KeyInput.KEY_8);
        KEY_MAP.put(JSKeyCodes.KEY_9, KeyInput.KEY_9);
        KEY_MAP.put(JSKeyCodes.KEY_A, KeyInput.KEY_A);
        KEY_MAP.put(JSKeyCodes.KEY_B, KeyInput.KEY_B);
        KEY_MAP.put(JSKeyCodes.KEY_C, KeyInput.KEY_C);
        KEY_MAP.put(JSKeyCodes.KEY_D, KeyInput.KEY_D);
        KEY_MAP.put(JSKeyCodes.KEY_E, KeyInput.KEY_E);
        KEY_MAP.put(JSKeyCodes.KEY_F, KeyInput.KEY_F);
        KEY_MAP.put(JSKeyCodes.KEY_G, KeyInput.KEY_G);
        KEY_MAP.put(JSKeyCodes.KEY_H, KeyInput.KEY_H);
        KEY_MAP.put(JSKeyCodes.KEY_I, KeyInput.KEY_I);
        KEY_MAP.put(JSKeyCodes.KEY_J, KeyInput.KEY_J);
        KEY_MAP.put(JSKeyCodes.KEY_K, KeyInput.KEY_K);
        KEY_MAP.put(JSKeyCodes.KEY_L, KeyInput.KEY_L);
        KEY_MAP.put(JSKeyCodes.KEY_M, KeyInput.KEY_M);
        KEY_MAP.put(JSKeyCodes.KEY_N, KeyInput.KEY_N);
        KEY_MAP.put(JSKeyCodes.KEY_O, KeyInput.KEY_O);
        KEY_MAP.put(JSKeyCodes.KEY_P, KeyInput.KEY_P);
        KEY_MAP.put(JSKeyCodes.KEY_Q, KeyInput.KEY_Q);
        KEY_MAP.put(JSKeyCodes.KEY_R, KeyInput.KEY_R);
        KEY_MAP.put(JSKeyCodes.KEY_S, KeyInput.KEY_S);
        KEY_MAP.put(JSKeyCodes.KEY_T, KeyInput.KEY_T);
        KEY_MAP.put(JSKeyCodes.KEY_U, KeyInput.KEY_U);
        KEY_MAP.put(JSKeyCodes.KEY_V, KeyInput.KEY_V);
        KEY_MAP.put(JSKeyCodes.KEY_W, KeyInput.KEY_W);
        KEY_MAP.put(JSKeyCodes.KEY_X, KeyInput.KEY_X);
        KEY_MAP.put(JSKeyCodes.KEY_Y, KeyInput.KEY_Y);
        KEY_MAP.put(JSKeyCodes.KEY_Z, KeyInput.KEY_Z);
        KEY_MAP.put(JSKeyCodes.KEY_NUMPAD0, KeyInput.KEY_NUMPAD0);
        KEY_MAP.put(JSKeyCodes.KEY_NUMPAD1, KeyInput.KEY_NUMPAD1);
        KEY_MAP.put(JSKeyCodes.KEY_NUMPAD2, KeyInput.KEY_NUMPAD2);
        KEY_MAP.put(JSKeyCodes.KEY_NUMPAD3, KeyInput.KEY_NUMPAD3);
        KEY_MAP.put(JSKeyCodes.KEY_NUMPAD4, KeyInput.KEY_NUMPAD4);
        KEY_MAP.put(JSKeyCodes.KEY_NUMPAD5, KeyInput.KEY_NUMPAD5);
        KEY_MAP.put(JSKeyCodes.KEY_NUMPAD6, KeyInput.KEY_NUMPAD6);
        KEY_MAP.put(JSKeyCodes.KEY_NUMPAD7, KeyInput.KEY_NUMPAD7);
        KEY_MAP.put(JSKeyCodes.KEY_NUMPAD8, KeyInput.KEY_NUMPAD8);
        KEY_MAP.put(JSKeyCodes.KEY_NUMPAD9, KeyInput.KEY_NUMPAD9);
        KEY_MAP.put(JSKeyCodes.KEY_MULTIPLY, KeyInput.KEY_MULTIPLY);
        KEY_MAP.put(JSKeyCodes.KEY_ADD, KeyInput.KEY_ADD);
        KEY_MAP.put(JSKeyCodes.KEY_SUBTRACT, KeyInput.KEY_SUBTRACT);
        KEY_MAP.put(JSKeyCodes.KEY_DECIMAL, KeyInput.KEY_DECIMAL);
        KEY_MAP.put(JSKeyCodes.KEY_DIVIDE, KeyInput.KEY_DIVIDE);
        KEY_MAP.put(JSKeyCodes.KEY_F1, KeyInput.KEY_F1);
        KEY_MAP.put(JSKeyCodes.KEY_F2, KeyInput.KEY_F2);
        KEY_MAP.put(JSKeyCodes.KEY_F3, KeyInput.KEY_F3);
        KEY_MAP.put(JSKeyCodes.KEY_F4, KeyInput.KEY_F4);
        KEY_MAP.put(JSKeyCodes.KEY_F5, KeyInput.KEY_F5);
        KEY_MAP.put(JSKeyCodes.KEY_F6, KeyInput.KEY_F6);
        KEY_MAP.put(JSKeyCodes.KEY_F7, KeyInput.KEY_F7);
        KEY_MAP.put(JSKeyCodes.KEY_F8, KeyInput.KEY_F8);
        KEY_MAP.put(JSKeyCodes.KEY_F9, KeyInput.KEY_F9);
        KEY_MAP.put(JSKeyCodes.KEY_F10, KeyInput.KEY_F10);
        KEY_MAP.put(JSKeyCodes.KEY_F11, KeyInput.KEY_F11);
        KEY_MAP.put(JSKeyCodes.KEY_F12, KeyInput.KEY_F12);
        KEY_MAP.put(JSKeyCodes.KEY_NUMLOCK, KeyInput.KEY_NUMLOCK);
        KEY_MAP.put(JSKeyCodes.KEY_SCROLL, KeyInput.KEY_SCROLL);
        KEY_MAP.put(JSKeyCodes.KEY_SEMICOLON, KeyInput.KEY_SEMICOLON);
        KEY_MAP.put(JSKeyCodes.KEY_EQUALS, KeyInput.KEY_EQUALS);
        KEY_MAP.put(JSKeyCodes.KEY_COMMA, KeyInput.KEY_COMMA);
        KEY_MAP.put(JSKeyCodes.KEY_MINUS, KeyInput.KEY_MINUS);
        KEY_MAP.put(JSKeyCodes.KEY_PERIOD, KeyInput.KEY_PERIOD);
        KEY_MAP.put(JSKeyCodes.KEY_SLASH, KeyInput.KEY_SLASH);
        KEY_MAP.put(JSKeyCodes.KEY_GRAVE, KeyInput.KEY_GRAVE);
        KEY_MAP.put(JSKeyCodes.KEY_LBRACKET, KeyInput.KEY_LBRACKET);
        KEY_MAP.put(JSKeyCodes.KEY_BACK, KeyInput.KEY_BACK);
        KEY_MAP.put(JSKeyCodes.KEY_RBRACKET, KeyInput.KEY_RBRACKET);
        KEY_MAP.put(JSKeyCodes.KEY_APOSTROPHE, KeyInput.KEY_APOSTROPHE);
    }
    public static int jsKeyCodeToJme(int jsKeyCode) {
        int jmeKeyCode = KeyInput.KEY_UNKNOWN;
        jmeKeyCode=KEY_MAP.get(jsKeyCode);
        return jmeKeyCode;
    }


    public static String getKeyNameJme(int jmeKeyCode) {
        String name = null;
        for (Map.Entry<Integer, Integer> entry : KEY_MAP.entrySet()) {
            if (entry.getValue() == jmeKeyCode) {            
                try {
                    String fieldName = JSKeyCodes.class.getDeclaredFields()[entry.getKey()].getName();
                    name = fieldName.replace("KEY_", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        if (name == null) {
            name = "UNKNOWN";
        }
        return name;
    }




    public static int jsMouseButtonToJme(int mouseButton) {
        switch (mouseButton) {
            case 0:
                return MouseInput.BUTTON_LEFT;
            case 1:
                return MouseInput.BUTTON_MIDDLE;
            case 2:
                return MouseInput.BUTTON_RIGHT;
            default:
                return -1;
        }

    }
    
}
