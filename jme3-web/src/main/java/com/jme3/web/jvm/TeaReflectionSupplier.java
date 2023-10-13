package com.jme3.web.jvm;

import java.io.File;
 import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.teavm.classlib.ReflectionContext;
import org.teavm.classlib.ReflectionSupplier;
import org.teavm.model.ClassReader;
import org.teavm.model.FieldReader;
 
import org.teavm.model.MethodDescriptor;
import org.teavm.model.MethodReader;
import org.teavm.model.ValueType;

import com.jme.jmetx.JmeTxRoot;
import com.jme3.anim.util.JointModelTransform;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetLocator;
import com.jme3.asset.CloneableAssetProcessor;
import com.jme3.asset.cache.AssetCache;
import com.jme3.asset.cache.SimpleAssetCache;
import com.jme3.audio.Filter;
import com.jme3.bullet.gltf.BulletRigidbodyExtensionLoader;
import com.jme3.export.Savable;
import com.jme3.material.logic.TechniqueDefLogic;
import com.jme3.scene.plugins.gltf.ext.JME_speaker.SpeakerExtensionLoader;
import com.jme3.scene.plugins.gltf.ext.KHR_lights_punctual.LightsPunctualExtensionLoader;
import com.jme3.scene.plugins.gltf.ext.KHR_materials_pbrSpecularGlossiness.PBRSpecGlossExtensionLoader;
import com.jme3.scene.plugins.gltf.ext.KHR_materials_unlit.UnlitExtensionLoader;
import com.jme3.scene.plugins.gltf.ext.KHR_texture_transform.TextureTransformExtensionLoader;
import com.jme3.system.JmeSystem;
import com.jme3.system.JmeSystemDelegate;
import com.jme3.texture.Image;
import com.jme3.texture.TextureProcessor;
import com.jme3.util.SafeArrayList;
import com.jme3.util.clone.JmeCloneable;
import com.jme3.web.context.HeapAllocator;
import com.jme3.web.filesystem.WebLocator;
import com.jme3.web.filesystem.WebResourceLoaderImplNoCache;
import com.jme3.json.*;
import com.jme3.light.LightProbe;

public class TeaReflectionSupplier implements ReflectionSupplier {

    private static ArrayList<Class<?>> clazzList = new ArrayList<Class<?>>();
    static {
        init();
    }

    private static void init() {
        TeaReflectionSupplier.addReflectionClass(Savable.class);
        TeaReflectionSupplier.addReflectionClass(AssetCache.class);
        TeaReflectionSupplier.addReflectionClass(AssetLoader.class);
        TeaReflectionSupplier.addReflectionClass(AssetLocator.class);
        TeaReflectionSupplier.addReflectionClass(Filter.class);
        TeaReflectionSupplier.addReflectionClass(JointModelTransform.class);
        TeaReflectionSupplier.addReflectionClass(TechniqueDefLogic.class);
        TeaReflectionSupplier.addReflectionClass(JmeCloneable.class);
        TeaReflectionSupplier.addReflectionClass(JmeSystem.class);
        TeaReflectionSupplier.addReflectionClass(JmeSystemDelegate.class);
        TeaReflectionSupplier.addReflectionClass(SimpleAssetCache.class);
        TeaReflectionSupplier.addReflectionClass(TextureProcessor.class);
        TeaReflectionSupplier.addReflectionClass(CloneableAssetProcessor.class);
        TeaReflectionSupplier.addReflectionClass(SafeArrayList.class);
        TeaReflectionSupplier.addReflectionClass(HeapAllocator.class);
        TeaReflectionSupplier.addReflectionClass(TeaJSONParser.class);
        TeaReflectionSupplier.addReflectionClass(WebResourceLoaderImplNoCache.class);
                TeaReflectionSupplier.addReflectionClass(LightProbe.class);
       TeaReflectionSupplier.addReflectionClass(PBRSpecGlossExtensionLoader.class);
      TeaReflectionSupplier.addReflectionClass(LightsPunctualExtensionLoader.class);
       TeaReflectionSupplier.addReflectionClass(  UnlitExtensionLoader.class);
       TeaReflectionSupplier.addReflectionClass( TextureTransformExtensionLoader.class);
       TeaReflectionSupplier.addReflectionClass( SpeakerExtensionLoader.class);
       TeaReflectionSupplier.addReflectionClass( BulletRigidbodyExtensionLoader.class);
       TeaReflectionSupplier.addReflectionClass( WebLocator.class);
       TeaReflectionSupplier.addReflectionClass(JmeTxRoot.class);
                TeaReflectionSupplier.addReflectionClass(Image.class);

                
        // TeaReflectionSupplier.addReflectionClass(GenericConstraint.class);
        // TeaReflectionSupplier.addReflectionClass(AnimControl.class);

        


        // String currentDirectory = System.getProperty("user.dir");
        // String jbulletPath= currentDirectory + "/jme3-web/src/main/java/com/bulletphysics";
        // addReflectionClassesFromDir(currentDirectory+"/jme3-web/src/main/java/",jbulletPath);

        // List<String> allClassesInClassPath = new ArrayList<String>();

        

        



     }


    //  public static void addReflectionClassesFromDir(String root,String path) {
    //     File folder = new File(path);
    //     File[] listOfFiles = folder.listFiles();

    //     for (File file : listOfFiles) {
    //         if (file.isFile()) {
    //             String fileName = file.getAbsolutePath();
    //             if (fileName.endsWith(".java")) {
    //                 String className = fileName.substring(0, fileName.length() - 5);
    //                 className = className.substring(root.length());
    //                 className=className.replace("/", ".");
                    
    //                 try {
    //                     Class<?> clazz = Class.forName(className);
    //                     TeaReflectionSupplier.addReflectionClass(clazz);
    //                 } catch (ClassNotFoundException e) {
    //                     e.printStackTrace();
    //                 }
    //             }
    //         } else if (file.isDirectory()) {
    //             addReflectionClassesFromDir(root,file.getAbsolutePath());
    //         }
    //     }
    //  }

    public static List<Class<?>> getReflectionClasses() {
        return clazzList;
    }

    /**
     * package path or package path with class name
     */
    public static void addReflectionClass(Class<?> c) {
        System.out.println("Enable reflection for " + c.getName());
        clazzList.add(c);
    }

    public TeaReflectionSupplier() {

    }

    @Override
    public Collection<String> getAccessibleFields(ReflectionContext context, String className) {
        Set<String> fields = new HashSet<>();
        ClassReader cls = context.getClassSource().get(className);
        if (cls == null) {
            return Collections.emptyList();
        }
        try {

            Class<?> clazz = context.getClassLoader().loadClass(className);

            if (cls != null) {
                if (canHaveReflection(clazz)) {
                    for (FieldReader field : cls.getFields()) {
                        String name = field.getName();
                        fields.add(name);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            new RuntimeException(e);
        }

        return fields;
    }

    private boolean isWhitelistedType(ReflectionContext context, ValueType t) throws ClassNotFoundException{
        boolean canHaveReflection = false;
        
        
        if (t instanceof ValueType.Void) {
            return true;
        }

        if (t instanceof ValueType.Array) {
            ValueType.Array a = (ValueType.Array) t;
            t = a.getItemType();

        }

        if (t instanceof ValueType.Object) {
            ValueType.Object obj = (ValueType.Object) t;
            
            // if any of the standard types eg String Object Class etc... pass with true
            if(obj.getClassName().startsWith("java.lang")) {
                return true;
            } else {
                Class<?> sc = context.getClassLoader().loadClass(obj.getClassName());
                canHaveReflection = canHaveReflection(sc);
            }
        } else if (t instanceof ValueType.Primitive) {
            canHaveReflection = true;
        }
        return canHaveReflection;

    }

    @Override
    public Collection<MethodDescriptor> getAccessibleMethods(ReflectionContext context, String className) {
        Set<MethodDescriptor> methods = new HashSet<>();

        ClassReader cls = context.getClassSource().get(className);
        if (cls == null) {
            return Collections.emptyList();
        }
        try {
            Class<?> clazz = context.getClassLoader().loadClass(className);

            if (canHaveReflection(clazz)) {
                Collection<? extends MethodReader> methods2 = cls.getMethods();
                for (MethodReader method : methods2) {                   
                    boolean canHaveReflection = true;     
                    for (int i = 0; i < method.parameterCount(); i++) {
                        ValueType t = method.parameterType(i);
                        canHaveReflection = isWhitelistedType(context, t);
                        if (!canHaveReflection) {
                            break;
                        }
                    }
                    
                    if (canHaveReflection) {
                        ValueType t = method.getResultType();
                         canHaveReflection = isWhitelistedType(context, t);
                    }
                    
                    if (!canHaveReflection) {
                        continue;
                    }


                    
                    MethodDescriptor descriptor = method.getDescriptor();
                    methods.add(descriptor);
                }
            }
        } catch (ClassNotFoundException e) {
            new RuntimeException(e);
        }
        return methods;
    }

    private boolean canHaveReflection(Class<?> clazz) {
        for (Class<?> c : clazzList) {

            if (c.isAssignableFrom(clazz) || c.equals(clazz)) {
                return true;
            }
        }

        return false;
    }
}