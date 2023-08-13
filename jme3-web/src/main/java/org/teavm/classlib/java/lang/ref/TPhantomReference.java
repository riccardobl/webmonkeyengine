package org.teavm.classlib.java.lang.ref;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import org.teavm.classlib.java.lang.ref.TReference;
import org.teavm.classlib.java.lang.ref.TReferenceQueue;
import org.teavm.classlib.java.lang.ref.TWeakReference;

public class TPhantomReference<T> extends WeakReference <T>{
    
    public TPhantomReference(T referent) {
        super(referent);
    }

     public TPhantomReference(T referent, ReferenceQueue<? super T> q) {
        super(referent, q);
    }
}
