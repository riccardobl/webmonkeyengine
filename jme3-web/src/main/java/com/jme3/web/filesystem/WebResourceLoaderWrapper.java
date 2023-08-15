package com.jme3.web.filesystem;

import java.util.function.Consumer;

import org.teavm.interop.Async;
import org.teavm.interop.AsyncCallback;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;
import org.teavm.jso.typedarrays.Int8Array;

@JSFunctor
interface ResourcePrefetchCallback extends JSObject {
    public void call(boolean b);
}
@JSFunctor
interface ResourceGetCallback extends JSObject {
    public void call(Int8Array i8);
}

@JSFunctor
interface ResourceIndexCallback extends JSObject {
    public void call(String[] ss);
}

class WebResourceLoaderWrapper {
    @Async
    static native boolean jmeResourcesPrefetch();
    static void jmeResourcesPrefetch(AsyncCallback<Boolean> callback){
       jmeResourcesPrefetchAsync((b)->callback.complete(b));
    }
     @JSBody(params = {"callback"}, script = "window.jme.resources.prefetch(callback);")
    static native void jmeResourcesPrefetchAsync(ResourcePrefetchCallback callback);
 
    @Async
    static native Int8Array jmeResourcesGetEntry(String name);
    static void jmeResourcesGetEntry(String name, AsyncCallback<Int8Array> callback) {
        jmeResourcesGetEntryAsync(name, i8->callback.complete(i8));            
    }
    @JSBody(params = {"name","callback"}, script = "window.jme.resources.getEntry(name,callback);")
    static native void jmeResourcesGetEntryAsync(String name, ResourceGetCallback callback);

    @Async
    static native String[] jmeResourcesGetIndex();
    static void jmeResourcesGetIndex(AsyncCallback<String[]> callback) {
        jmeResourcesGetIndexAsync(ss->callback.complete(ss));
    }
    @JSBody(params = {"callback"}, script = "window.jme.resources.getIndex(callback);")
    static native void jmeResourcesGetIndexAsync(ResourceIndexCallback callback);


}
