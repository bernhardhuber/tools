/*
 * Copyright 2021 pi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.huberb.apacheactivemq.examples.picocli.jms;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.jms.JMSException;
import javax.jms.Message;
import org.huberb.apacheactivemq.examples.picocli.jms.MessagePropertyFactory.JmsPropertyRepresentation;

/**
 *
 * @author pi
 */
public class MessagePropertyFactory {

    //---
    final ToConverters toConverters = new ToConverters();

    /**
     *
     * @param message
     * @param messageMap
     */
    public void populateMessagePropertyWith(Message message, Map<String, Object> messageMap) {
        try {
            for (Map.Entry<String, Object> me : messageMap.entrySet()) {
                final String key = me.getKey();
                final Object v = me.getValue();
                populateMessagePropertyWithKeyValue(message, key, v);
            }
        } catch (JMSException jmsex) {
            throw new AutoCloseableSupport.JMSRuntimeException(jmsex);
        }
    }

    enum JmsPropertyRepresentation {
        boolean_prefix("boolean:"),
        byte_prefix("byte:"),
        double_prefix("double:"),
        float_prefix("float:"),
        int_prefix("int:"),
        long_prefix("long:"),
        object_prefix("object:"),
        short_prefix("short:"),
        string_prefix("string:");
        private final String prefix;

        JmsPropertyRepresentation(String s) {
            this.prefix = s;
        }

        String getPrefix() {
            return this.prefix;
        }

        boolean startsWithPrefix(String key) {
            return key != null && key.startsWith(this.prefix);
        }

        static Optional<JmsPropertyRepresentation> findPrefix(String key) {
            final Optional<JmsPropertyRepresentation> matchedPrefix;
            if (key != null && !key.isBlank()) {
                final Set<JmsPropertyRepresentation> es = EnumSet.allOf(JmsPropertyRepresentation.class);
                matchedPrefix = es.stream().filter((JmsPropertyRepresentation xxx) -> key.startsWith(xxx.getPrefix())).findFirst();
            } else {
                matchedPrefix = Optional.empty();
            }
            return matchedPrefix;
        }
    }

    enum JmsApiPropertyRepresentation {
        jms_prefix("JMS:");
        private final String prefix;

        JmsApiPropertyRepresentation(String s) {
            this.prefix = s;
        }

        String getPrefix() {
            return this.prefix;
        }

        boolean startsWithPrefix(String key) {
            return key != null && key.startsWith(this.prefix);
        }

        static Optional<JmsApiPropertyRepresentation> findPrefix(String key) {
            final Optional<JmsApiPropertyRepresentation> matchedPrefix;
            if (key != null && !key.isBlank()) {
                final Set<JmsApiPropertyRepresentation> es = EnumSet.allOf(JmsApiPropertyRepresentation.class);
                matchedPrefix = es.stream().filter((JmsApiPropertyRepresentation xxx) -> key.startsWith(xxx.getPrefix())).findFirst();
            } else {
                matchedPrefix = Optional.empty();
            }
            return matchedPrefix;
        }
    }

    void populateMessagePropertyWithKeyValue(Message message, String key, Object value) throws JMSException {
        //---
        final Optional<JmsPropertyRepresentation> matchedXXXByPrefix1 = JmsPropertyRepresentation.findPrefix(key);
        //---
        final Optional<JmsApiPropertyRepresentation> matchedYYYByPrefix2 = JmsApiPropertyRepresentation.findPrefix(key);
        //---
        if (matchedXXXByPrefix1.isPresent()) {
            handleJmsPropertyByTypePrefixGroup1(message, matchedXXXByPrefix1.get(), key, value);
        } else if (matchedYYYByPrefix2.isPresent()) {
            handleJmsPropertyByNamePrefixGroup2(message, matchedYYYByPrefix2.get(), key, value);
        }
    }

    void handleJmsPropertyByTypePrefixGroup1(Message message, JmsPropertyRepresentation xxx, String key, Object value) throws JMSException {
        final String keyWithoutPrefix = key.substring(xxx.getPrefix().length());
        if (xxx == JmsPropertyRepresentation.boolean_prefix) {
            final Optional<Boolean> optBoolean = toConverters.toBoolean(value);
            if (optBoolean.isPresent()) {
                message.setBooleanProperty(keyWithoutPrefix, optBoolean.get());
            }
        } else if (xxx == JmsPropertyRepresentation.byte_prefix) {
            final Optional<Byte> optByte = toConverters.toByte(value);
            if (optByte.isPresent()) {
                message.setByteProperty(keyWithoutPrefix, optByte.get());
            }
        } else if (xxx == JmsPropertyRepresentation.double_prefix) {
            final Optional<Double> optDouble = toConverters.toDouble(value);
            if (optDouble.isPresent()) {
                message.setDoubleProperty(keyWithoutPrefix, optDouble.get());
            }
        } else if (xxx == JmsPropertyRepresentation.float_prefix) {
            final Optional<Float> optFloat = toConverters.toFloat(value);
            if (optFloat.isPresent()) {
                message.setFloatProperty(keyWithoutPrefix, optFloat.get());
            }
        } else if (xxx == JmsPropertyRepresentation.int_prefix) {
            final Optional<Integer> optInteger = toConverters.toInteger(value);
            if (optInteger.isPresent()) {
                message.setIntProperty(keyWithoutPrefix, optInteger.get());
            }
        } else if (xxx == JmsPropertyRepresentation.long_prefix) {
            final Optional<Long> optLong = toConverters.toLong(value);
            if (optLong.isPresent()) {
                message.setLongProperty(keyWithoutPrefix, optLong.get());
            }
        } else if (xxx == JmsPropertyRepresentation.short_prefix) {
            message.setObjectProperty(keyWithoutPrefix, value);
        } else if (key.startsWith("short:")) {
            final Optional<Short> optShort = toConverters.toShort(value);
            if (optShort.isPresent()) {
                message.setShortProperty(keyWithoutPrefix, optShort.get());
            }
        } else if (xxx == JmsPropertyRepresentation.string_prefix) {
            final Optional<String> optString = toConverters.toString(value);
            if (optString.isPresent()) {
                message.setStringProperty(keyWithoutPrefix, optString.get());
            }
        }
    }

    void handleJmsPropertyByNamePrefixGroup2(Message message, JmsApiPropertyRepresentation xxx, String key, Object value) throws JMSException {

        final String keyWithoutPrefix = key.substring(xxx.getPrefix().length());
        if (xxx == JmsApiPropertyRepresentation.jms_prefix) {
            if (keyWithoutPrefix.equals("JMSCorrelationID")) {
                Optional<String> optValueAsString = toConverters.toString(value);
                if (optValueAsString.isPresent()) {
                    message.setJMSCorrelationID(optValueAsString.get());
                }
            } else if (keyWithoutPrefix.equals("JMSDeliveryMode")) {
                final List<EnumRepresentations.DeliveryModeRepresentation> deliveryModeValuesAllowedValues = Arrays.asList(
                        EnumRepresentations.DeliveryModeRepresentation.non_persistent,
                        EnumRepresentations.DeliveryModeRepresentation.persistent);
                Optional<Integer> optValueAsInteger = toConverters.toInteger(value, deliveryModeValuesAllowedValues);
                if (optValueAsInteger.isPresent()) {
                    message.setJMSDeliveryMode(optValueAsInteger.get());
                }
            } else if (keyWithoutPrefix.equals("JMSDestination")) {

            } else if (keyWithoutPrefix.equals("JMSExpiration")) {
                Optional<Long> optValueAsLong = toConverters.toLong(value);
                if (optValueAsLong.isPresent()) {
                    message.setJMSExpiration(optValueAsLong.get());
                }
            } else if (keyWithoutPrefix.equals("JMSMessageID")) {
                Optional<String> optValueAsString = toConverters.toString(value);
                if (optValueAsString.isPresent()) {
                    message.setJMSMessageID(optValueAsString.get());
                }
            } else if (keyWithoutPrefix.equals("JMSPriority")) {
                Optional<Integer> optValueAsInteger = toConverters.toInteger(value);
                if (optValueAsInteger.isPresent()) {
                    message.setJMSExpiration(optValueAsInteger.get());
                }
            } else if (keyWithoutPrefix.equals("JMSRedelivered")) {
                Optional<Boolean> optValueAsBoolean = toConverters.toBoolean(value);
                if (optValueAsBoolean.isPresent()) {
                    message.setJMSRedelivered(optValueAsBoolean.get());
                }
            } else if (keyWithoutPrefix.equals("JMSReplyTo")) {

            } else if (keyWithoutPrefix.equals("JMSTimestamp")) {
                Optional<Long> optValueAsLong = toConverters.toLong(value);
                if (optValueAsLong.isPresent()) {
                    message.setJMSTimestamp(optValueAsLong.get());
                }
            } else if (keyWithoutPrefix.equals("JMSType")) {
                Optional<String> optValueAsString = toConverters.toString(value);
                if (optValueAsString.isPresent()) {
                    message.setJMSType(optValueAsString.get());
                }
            }
        }
    }
}
