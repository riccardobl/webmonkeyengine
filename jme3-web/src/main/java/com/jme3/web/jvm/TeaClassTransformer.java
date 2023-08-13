package com.jme3.web.jvm;
 
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.Collection;
import org.teavm.model.ClassHolder;
import org.teavm.model.ClassHolderTransformer;
import org.teavm.model.ClassHolderTransformerContext;

import org.teavm.model.ClassReader;
import org.teavm.model.ClassReaderSource;
import org.teavm.model.ElementModifier;
import org.teavm.model.FieldHolder;
import org.teavm.model.MethodHolder;
import org.teavm.model.ReferenceCache;
import org.teavm.model.util.ModelUtils;
import org.teavm.parsing.ClassRefsRenamer;

import com.jme3.util.res.ResourcesLoaderJImpl;
import com.jme3.web.jvm.patches.ByteBufferPatch;
import com.jme3.web.jvm.patches.ByteOrderPatch;
import com.jme3.web.jvm.patches.ClassLoaderPatch;
import com.jme3.web.jvm.patches.ClassPatch;
import com.jme3.web.jvm.patches.InputStreamReaderPatch;
import com.jme3.web.jvm.patches.LoggerPatch;
import com.jme3.web.jvm.patches.ReaderPatch;
import com.jme3.web.jvm.patches.ReferenceQueuePatch;
import com.jme3.web.jvm.patches.RuntimePatch;
import com.jme3.web.jvm.patches.ThreadLocalPatch;

 
public class TeaClassTransformer implements ClassHolderTransformer {


    public TeaClassTransformer() {

    }
    
    private static ClassHolder getClassHolder(Class<?> clazz, ClassHolderTransformerContext context) {
        ClassReaderSource innerSource = context.getHierarchy().getClassSource();
        ClassReader classReader = innerSource.get(clazz.getName());
        ClassHolder classHolder = (ClassHolder) classReader;
        return classHolder;
    }
    
    private static ReferenceCache referenceCache = new ReferenceCache();
    private static void transferMethods( Class<?> clazz,ClassHolder a, ClassHolder b, ClassHolderTransformerContext context) {
        Collection<MethodHolder> methods = a.getMethods();
        Collection<MethodHolder> patchableMethods = new ArrayList<MethodHolder>();

        for (MethodHolder methodHolder : methods) {
            if (!methodHolder.hasModifier(ElementModifier.ABSTRACT) && !methodHolder.getName().equals("<init>")) {
                patchableMethods.add(methodHolder);
            }
        }
            
        ClassRefsRenamer refPatcher = new ClassRefsRenamer(referenceCache, n -> {
            if (n.equals(a.getName())) {
                return b.getName();
            }
            return n;
        });

        for (MethodHolder methodHolder : patchableMethods) {
             
            methodHolder = ModelUtils.copyMethod(methodHolder);
            methodHolder=refPatcher.rename(methodHolder);
            b.addMethod(methodHolder);
        }

    }
    
    private static void transferFields(ClassHolder a, ClassHolder b, ClassHolderTransformerContext context) {
        Collection<FieldHolder> fields = a.getFields();
        Collection<FieldHolder> patchableFields = new ArrayList<FieldHolder>();

        for (FieldHolder fieldHolder : fields) {
            if (!fieldHolder.hasModifier(ElementModifier.ABSTRACT)) {
                patchableFields.add(fieldHolder);
            }
        }
        for (FieldHolder fieldHolder : patchableFields) {
            if (b.getField(fieldHolder.getName()) != null) {
                b.removeField(b.getField(fieldHolder.getName()));
            }
            a.removeField(fieldHolder);
            b.addField(fieldHolder);
        }

    }


    @Override
    public void transformClass(ClassHolder cls, ClassHolderTransformerContext context) {
        String clzName = cls.getName();

        String excludeClasses[] = new String[]{
            ResourcesLoaderJImpl.class.getName()
        };

        try {

            for(String exclucedClass : excludeClasses) {
                if (clzName.equals(exclucedClass)) {
                    
                }
            }

            Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(clzName);
            if (clzName.equals("java.nio.ByteBuffer")) {
                // System.out.println("Patch ByteBuffer");
                ClassHolder byteBufferPatchHolder = getClassHolder(ByteBufferPatch.class, context);
                transferMethods(clazz,byteBufferPatchHolder, cls, context);
                System.out.println(clazz.getName() + " patched");
            } else if (clzName.equals("java.util.logging.Logger")) {
                 ClassHolder byteBufferPatchHolder = getClassHolder(LoggerPatch.class, context);
                transferMethods(clazz,byteBufferPatchHolder, cls,context);
                System.out.println(clazz.getName()+" patched");
            } else if (clzName.equals("java.nio.ByteOrder")) {
                ClassHolder byteOrderPatchHolder = getClassHolder(ByteOrderPatch.class, context);
                transferMethods(clazz,byteOrderPatchHolder, cls, context);
                transferFields(byteOrderPatchHolder, cls, context);
                System.out.println(clazz.getName() + " patched");
            } else if (clzName.equals("java.lang.Runtime")) {
                ClassHolder runtimePatchHolder = getClassHolder(RuntimePatch.class, context);
                transferMethods(clazz,runtimePatchHolder, cls, context);
                System.out.println(clazz.getName() + " patched");
             }else if (clzName.equals("java.lang.Class")) {
                ClassHolder classPatchHolder = getClassHolder(ClassPatch.class, context);
                transferMethods(clazz,classPatchHolder, cls, context);
                System.out.println(clazz.getName() + " patched");
            } else if (clzName.equals("java.lang.ref.ReferenceQueue")) {
                ClassHolder classPatchHolder = getClassHolder(ReferenceQueuePatch.class, context);
                transferMethods(clazz,classPatchHolder, cls, context);
                System.out.println(clazz.getName() + " patched");
             } else if (clzName.equals("java.lang.ClassLoader")) {
                ClassHolder classPatchHolder = getClassHolder(ClassLoaderPatch.class, context);
                transferMethods(clazz,classPatchHolder, cls, context);
                System.out.println(clazz.getName() + " patched");
            } else if (clzName.equals("java.lang.ThreadLocal")) {
                ClassHolder classPatchHolder = getClassHolder(ThreadLocalPatch.class, context);
                transferMethods(clazz,classPatchHolder, cls, context);
                System.out.println(clazz.getName() + " patched");
            } else if (clzName.equals("java.io.Reader")) {
                ClassHolder classPatchHolder = getClassHolder(ReaderPatch.class, context);
                 cls.getInterfaces().add("java.lang.Readable");
                 
                transferMethods(clazz,classPatchHolder, cls, context);
                System.out.println(clazz.getName() + " patched");
            } else if (clzName.equals("java.io.InputStreamReader")) {
                // ClassHolder classPatchHolder = getClassHolder(InputStreamReaderPatch.class, context);
                // make class implement Readable
                // cls.getInterfaces().add("java.lang.Readable");
                // transferMethods(clazz,classPatchHolder, cls, context);
                // System.out.println(clazz.getName() + " patched");
            }
        
        } catch (ClassNotFoundException e) {
            // e.printStackTrace();
            // System.err.println("Can't patch ByteBuffer!");
        }

        
        

    
        
        
        
    }

 
    

    // private void emulateClass(ClassHolderTransformerContext context, ClassHolder cls, ClassReaderSource innerSource) {
    //     String className = cls.getName();
    //     if(updateCode.containsKey(className)) {
    //         ClassReader original = innerSource.get(className);
    //         Class<?> emulated = updateCode.get(className);
    //         ClassReader emulatedClassHolder = innerSource.get(emulated.getName());
    //         if(emulatedClassHolder != null) {
    //             replaceClassCode(emulated, cls, emulatedClassHolder);

    //             // teavm makes bunch of classreader copies. We also need to update the original class
    //             replaceClassCode(emulated, (ClassHolder)original, emulatedClassHolder);
    //         }
    //     }
        
    // }


    // private void replaceClassCode(Class<?> emulated, final ClassHolder cls, final ClassReader emuCls) {
    //     Predicate<AnnotatedElement> annotatedElementPredicate = ReflectionUtils.withAnnotation(Emulate.class);
    //     Set<Field> fields = ReflectionUtils.getFields(emulated, annotatedElementPredicate);
    //     Set<Method> methods = ReflectionUtils.getMethods(emulated, annotatedElementPredicate);

    //     ClassRefsRenamer renamer = new ClassRefsRenamer(referenceCache, preimage -> {
    //         String newName = emulations2.get(preimage);
    //         if(newName != null) {
    //             return newName;
    //         }
    //         else {
    //             return preimage;
    //         }
    //     });

    //     for(Field field : fields) {
    //         String emuFieldName = field.getName();
    //         FieldReader emulatedField = emuCls.getField(emuFieldName);
    //         FieldHolder originalField = cls.getField(emuFieldName);
    //         if(originalField != null) {
    //             cls.removeField(originalField);
    //         }
    //         FieldHolder fieldHolder = ModelUtils.copyField(emulatedField);
    //         FieldHolder fieldRename = renamer.rename(fieldHolder);
    //         cls.addField(fieldRename);
    //     }

    //     for(Method method : methods) {
    //         Class[] classes = new Class[method.getParameterTypes().length + 1];
    //         classes[classes.length - 1] = method.getReturnType();
    //         System.arraycopy(method.getParameterTypes(), 0, classes, 0, method.getParameterTypes().length);
    //         MethodDescriptor methodDescriptor = new MethodDescriptor(method.getName(), classes);
    //         MethodHolder originalMethod = cls.getMethod(methodDescriptor);
    //         if(originalMethod != null) {
    //             cls.removeMethod(originalMethod);
    //         }
    //         MethodReader emulatedMethodReader = emuCls.getMethod(methodDescriptor);
    //         MethodHolder methodHolderCopy = ModelUtils.copyMethod(emulatedMethodReader);
    //         MethodHolder methodRename = renamer.rename(methodHolderCopy);
    //         cls.addMethod(methodRename);
    //     }
    // }

    // private void replaceClass(ClassReaderSource innerSource, final ClassHolder cls, final ClassReader emuCls) {
    //     ClassRefsRenamer renamer = new ClassRefsRenamer(referenceCache, preimage -> {
    //         String newName = emulations2.get(preimage);
    //         if(newName != null) {
    //             return newName;
    //         }
    //         else {
    //             return preimage;
    //         }
    //     });

    //     cls.getInterfaces().clear();
    //     Set<String> interfaces = emuCls.getInterfaces();
    //     Iterator<String> interfaceIt = interfaces.iterator();
    //     while(interfaceIt.hasNext()) {
    //         String interfaceStr = interfaceIt.next();
    //         ClassReader interfaceClassHolder = innerSource.get(interfaceStr);
    //         if(interfaceClassHolder != null) {
    //             ClassHolder classHolder = ModelUtils.copyClass(interfaceClassHolder);
    //             ClassHolder interfaceRename = renamer.rename(classHolder);
    //             String name = interfaceRename.getName();
    //             cls.getInterfaces().add(name);
    //         }
    //     }

    //     String parent = emuCls.getParent();
    //     if(parent != null && !parent.isEmpty()) {
    //         ClassReader parentClassHolder = innerSource.get(parent);
    //         if(parentClassHolder != null) {
    //             ClassHolder classHolder = ModelUtils.copyClass(parentClassHolder);
    //             ClassHolder parentClassHolderRename = renamer.rename(classHolder);
    //             String name = parentClassHolderRename.getName();
    //             cls.setParent(name);
    //         }
    //     }

    //     for(FieldHolder field : cls.getFields().toArray(new FieldHolder[0])) {
    //         cls.removeField(field);
    //     }
    //     for(MethodHolder method : cls.getMethods().toArray(new MethodHolder[0])) {
    //         cls.removeMethod(method);
    //     }
    //     for(FieldReader field : emuCls.getFields()) {
    //         FieldHolder newfieldHolder = ModelUtils.copyField(field);
    //         FieldHolder renamedField = renamer.rename(newfieldHolder);
    //         cls.addField(renamedField);
    //     }
    //     for(MethodReader method : emuCls.getMethods()) {
    //         MethodHolder newMethodHolder = ModelUtils.copyMethod(method);
    //         MethodHolder renamedMethod = renamer.rename(newMethodHolder);
    //         cls.addMethod(renamedMethod);
    //     }
    // }
}
