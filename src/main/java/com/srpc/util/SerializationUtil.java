package com.srpc.util;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zynb0319 on 2016/1/23.
 */
public class SerializationUtil {
    private static Map<Class<?>,Schema<?>> cachedSchema = new ConcurrentHashMap<Class<?>, Schema<?>>();
    private static Objenesis objenesis = new ObjenesisStd(true);
    private SerializationUtil(){

    }
    private static <T> Schema<T> getSchema(Class<T> cls){
        Schema<T> schema = (Schema<T>)cachedSchema.get(cls);
        if(schema==null){
            schema = RuntimeSchema.getSchema(cls);
            if(schema!=null){
                cachedSchema.put(cls,schema);
            }
        }
        return schema;
    }
    public  static <T> byte[] serialize(T obj){
        Class<T> cls = (Class<T>)obj.getClass();
        LinkedBuffer linkedBuffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = getSchema(cls);
            return ProtobufIOUtil.toByteArray(obj,schema,linkedBuffer);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new IllegalStateException(ex.getMessage(), ex);
        } finally {
            linkedBuffer.clear();
        }
    }
    public static <T> T deserialize(byte[] data,Class<T> cls){
        try {
            T message = (T)objenesis.newInstance(cls);
            Schema<T> schema = getSchema(cls);
            ProtobufIOUtil.mergeFrom(data,message,schema);
            return message;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new IllegalStateException(ex.getMessage(), ex);
        } finally {

        }
    }

}
