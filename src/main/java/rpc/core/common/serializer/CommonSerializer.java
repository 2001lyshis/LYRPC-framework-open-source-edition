package rpc.core.common.serializer;

public interface CommonSerializer {

    Integer KRYO_SERIALIZER = 0;

    Integer DEFAULT_SERIALIZER = KRYO_SERIALIZER;

    static CommonSerializer getByCode(int code) {
         return new KryoSerializer();
    }

    byte[] serializer(Object obj);

    Object deserialize(byte[] bytes, Class<?> clazz);

    int getCode();
}
