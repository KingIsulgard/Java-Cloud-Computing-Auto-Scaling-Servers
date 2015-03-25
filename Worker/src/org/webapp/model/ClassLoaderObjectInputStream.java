package org.webapp.model;
import java.io.*;

public class ClassLoaderObjectInputStream extends ObjectInputStream {
/**
 * New ObjectInputStream-subclass that has access to my custom ClassLoader
 */
    private ClassLoader classLoader;

    public ClassLoaderObjectInputStream(ClassLoader classLoader, ByteArrayInputStream in) throws IOException {
         super(in);
         this.classLoader = classLoader;
    }
    
    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException{
         try{
              String name = desc.getName();
              return Class.forName(name, false, classLoader);
         }
         catch(ClassNotFoundException e){
              return super.resolveClass(desc);
         }
    }
}
