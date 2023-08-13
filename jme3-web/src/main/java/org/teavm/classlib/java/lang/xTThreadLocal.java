package org.teavm.classlib.java.lang;

import java.util.function.Supplier;

import org.teavm.classlib.java.util.function.TSupplier;

public class xTThreadLocal<T> extends TObject{
    Supplier<? extends T> supplier;
    T value;
    protected T initialValue() {
        return null;
    }

    public static <T> T withInitial(Supplier supplier){
        xTThreadLocal<T>  tl = new xTThreadLocal<T>();
        tl.supplier = supplier;
        return (T)tl;
    }

    public xTThreadLocal() {
    }

   
    public T get() {         
        setInitialValue();
        return this.value;
    }

   
    private T setInitialValue() {
        if (this.value != null) return this.value;
        T value = initialValue();
        if (value == null) value = (T) supplier.get();
        this.value = value;
        return value;
    }

    
    public void set(T value) {
        this.value = value;
    }

    
     public void remove() {
        
     }

    
}
