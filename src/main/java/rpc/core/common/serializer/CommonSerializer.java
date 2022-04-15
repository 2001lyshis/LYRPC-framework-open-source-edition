package rpc.core.common.serializer;

public interface CommonSerializer {
    Integer JSON_SERIALIZER = 0;
    Integer KRYO_SERIALIZER = 1;

    Integer DEFAULT_SERIALIZER = KRYO_SERIALIZER;

    static CommonSerializer getByCode(int code) {
         switch (code) {
                    case 1:
                        return new KryoSerializer();
                    case 0:
                        return new JsonSerializer();
                    default:
                        return null;
         }
    }

    byte[] serializer(Object obj);

    Object deserialize(byte[] bytes, Class<?> clazz);

    int getCode();
}
