package com.cryptocurrency.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.IOException;

/**
 * @author yuvaraj.sanjeevi
 */
public class TestUtil {


    private static ObjectMapper objectMapper = new ObjectMapper();

    public static ObjectMapper createObjectMapper() {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, Boolean.FALSE);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        return objectMapper;
    }


    public static String convertObjectToJson(Object object) {
        String response;
        try {
            response = createObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException jsonProcessingException) {
            response = ToStringBuilder.reflectionToString(object);
        }
        return response;
    }

    public static <T> T convertJsonStrToObject(String json, Class<T> toValueType) {
        T response = null;
        try {
            response = createObjectMapper().readValue(json, toValueType);
        } catch (IOException ioException) {

        }
        return response;
    }

    public static <T> T convertJsonStrToObject(String json, TypeReference<T> TypeReference) {
        T response = null;
        try {
            response = createObjectMapper().readValue(json, TypeReference);
        } catch (IOException ioException) {

        }
        return response;
    }


}
