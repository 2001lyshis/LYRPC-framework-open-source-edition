package rpc.core.common.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.SerializationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.core.common.enumeration.SerializerCode;

public class JsonSerializer implements CommonSerializer {

    private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);

    private ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public byte[] serializer(Object obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            logger.error("序列化时发生错误: ", e);
            throw new SerializationException(e);
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try {
            return objectMapper.readValue(bytes, clazz);
        } catch (Exception e) {
            logger.error("反序列化时发生错误: ", e);
            throw new SerializationException(e);
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.JSON.getCode();
    }
}
