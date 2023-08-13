package com.jme3.web.jvm.patches;

import java.util.function.Supplier;


public class ThreadLocalPatch<T> {

    public static <S> ThreadLocal<S> withInitial(Supplier<? extends S> supplier) {
       ThreadLocal<S> tl = new ThreadLocal<S>(){
            public S get() {
               S out = super.get();
               if (out == null) {
                   out = supplier.get();
                   if (out != null) set(out);
               }
                return out;                
            }
        };
        return  tl;
    }

}
