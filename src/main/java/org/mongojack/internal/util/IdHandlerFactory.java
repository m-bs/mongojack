/*
 * Copyright 2011 VZ Netzwerke Ltd
 * Copyright 2014 devbliss GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mongojack.internal.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;

/**
 * Creates an ID handler
 * 
 * @author James Roper
 * @since 1.0
 */
public class IdHandlerFactory {

    public static <K> IdHandler<K, ?> getIdHandlerForProperty(
            ObjectMapper objectMapper, JavaType type)
            throws JsonMappingException {
        JsonDeserializer deserializer = JacksonAccessor.findDeserializer(
                objectMapper, type);
        JsonDeserializer idDeserializer = null;
        JsonSerializer idSerializer = null;

        if (deserializer instanceof BeanDeserializer) {
            SettableBeanProperty property = ((BeanDeserializer) deserializer)
                    .findProperty("_id");
            if (property != null) {
                idDeserializer = property.getValueDeserializer();
            }
        }

        JsonSerializer serializer = JacksonAccessor.findValueSerializer(
                JacksonAccessor.getSerializerProvider(objectMapper), type);
        if (serializer instanceof BeanSerializerBase) {
            BeanPropertyWriter writer = JacksonAccessor.findPropertyWriter(
                    (BeanSerializerBase) serializer, "_id");
            if (writer != null) {
                idSerializer = writer.getSerializer();
            }
        }

        if (idDeserializer != null && idSerializer != null) {
            return new IdHandler.JacksonIdHandler(idSerializer, idDeserializer,
                    objectMapper);
        } else {
            return new IdHandler.NoopIdHandler<K>();
        }
    }
}
