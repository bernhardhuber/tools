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

import java.util.List;
import java.util.Optional;

/**
 *
 * @author pi
 */
public class ToConverters {

    enum SupportedConverters {
        toBoolean, toByte, toFloat, toDouble, toInteger, toLong, toObject, toShort, toString
    }

    Optional< ? extends Object> toSupported(SupportedConverters supportedConverters, Object v) {
        final Optional<? extends Object> optObject;
        if (supportedConverters == null || v == null) {
            optObject = Optional.empty();
        } else {
            switch (supportedConverters) {
                case toBoolean:
                    optObject = toBoolean(v);
                    break;
                case toByte:
                    optObject = toByte(v);
                    break;
                case toDouble:
                    optObject = toDouble(v);
                    break;
                case toFloat:
                    optObject = toFloat(v);
                    break;
                case toInteger:
                    optObject = toInteger(v);
                    break;
                case toLong:
                    optObject = toLong(v);
                    break;
                case toObject:
                    optObject = toObject(v);
                    break;
                case toShort:
                    optObject = toShort(v);
                    break;
                case toString:
                    optObject = toString(v);
                    break;
                default:
                    optObject = Optional.empty();
            }
        }
        return optObject;
    }

    Optional<Boolean> toBoolean(Object v) {
        final Optional<Boolean> optBoolean;
        if (v == null) {
            optBoolean = Optional.empty();
        } else if (Boolean.class.isAssignableFrom(v.getClass())) {
            optBoolean = Optional.ofNullable((Boolean) v);
        } else if (v instanceof String || v instanceof StringBuilder || v instanceof StringBuffer) {
            String s = v.toString();
            optBoolean = Optional.of(Boolean.parseBoolean(s));
        } else {
            optBoolean = Optional.empty();
        }
        return optBoolean;
    }

    Optional<Byte> toByte(Object v) {
        Optional<Byte> optByte;
        if (v == null) {
            optByte = Optional.empty();
        } else if (Number.class.isAssignableFrom(v.getClass())) {
            Number n = (Number) v;
            optByte = Optional.of(n.byteValue());
        } else if (v instanceof String || v instanceof StringBuilder || v instanceof StringBuffer) {
            String s = v.toString();
            try {
                optByte = Optional.of(Byte.parseByte(s));
            } catch (NumberFormatException nfex) {
                optByte = Optional.empty();
            }
        } else {
            optByte = Optional.empty();
        }
        return optByte;
    }

    Optional<Double> toDouble(Object v) {
        Optional<Double> optDouble;
        if (v == null) {
            optDouble = Optional.empty();
        } else if (Number.class.isAssignableFrom(v.getClass())) {
            Number n = (Number) v;
            optDouble = Optional.of(n.doubleValue());
        } else if (v instanceof String || v instanceof StringBuilder || v instanceof StringBuffer) {
            String s = v.toString();
            try {
                optDouble = Optional.of(Double.parseDouble(s));
            } catch (NumberFormatException nfex) {
                optDouble = Optional.empty();
            }
        } else {
            optDouble = Optional.empty();
        }
        return optDouble;
    }

    Optional<Float> toFloat(Object v) {
        Optional<Float> optFloat;
        if (v == null) {
            optFloat = Optional.empty();
        } else if (Number.class.isAssignableFrom(v.getClass())) {
            Number n = (Number) v;
            optFloat = Optional.of(n.floatValue());
        } else if (v instanceof String || v instanceof StringBuilder || v instanceof StringBuffer) {
            String s = v.toString();
            try {
                optFloat = Optional.of(Float.parseFloat(s));
            } catch (NumberFormatException nfex) {
                optFloat = Optional.empty();
            }
        } else {
            optFloat = Optional.empty();
        }
        return optFloat;
    }

    Optional<Integer> toInteger(Object v) {
        Optional<Integer> optInteger;
        if (v == null) {
            optInteger = Optional.empty();
        } else if (Number.class.isAssignableFrom(v.getClass())) {
            Number n = (Number) v;
            optInteger = Optional.of(n.intValue());
        } else if (v instanceof String || v instanceof StringBuilder || v instanceof StringBuffer) {
            String s = v.toString();
            try {
                optInteger = Optional.of(Integer.parseInt(s));
            } catch (NumberFormatException nfex) {
                optInteger = Optional.empty();
            }
        } else {
            optInteger = Optional.empty();
        }
        return optInteger;
    }

    Optional<Long> toLong(Object v) {
        Optional<Long> optLong;
        if (v == null) {
            optLong = Optional.empty();
        } else if (Number.class.isAssignableFrom(v.getClass())) {
            Number n = (Number) v;
            optLong = Optional.of(n.longValue());
        } else if (v instanceof String || v instanceof StringBuilder || v instanceof StringBuffer) {
            String s = v.toString();
            try {
                optLong = Optional.of(Long.parseLong(s));
            } catch (NumberFormatException nfex) {
                optLong = Optional.empty();
            }
        } else {
            optLong = Optional.empty();
        }
        return optLong;
    }

    Optional<Object> toObject(Object v) {
        final Optional<Object> optObject;
        if (v == null) {
            optObject = Optional.empty();
        } else {
            optObject = Optional.of(v);
        }
        return optObject;
    }

    Optional<Short> toShort(Object v) {
        Optional<Short> optShort;
        if (v == null) {
            optShort = Optional.empty();
        } else if (Number.class.isAssignableFrom(v.getClass())) {
            Number n = (Number) v;
            optShort = Optional.of(n.shortValue());
        } else if (v instanceof String || v instanceof StringBuilder || v instanceof StringBuffer) {
            String s = v.toString();
            try {
                optShort = Optional.of(Short.parseShort(s));
            } catch (NumberFormatException nfex) {
                optShort = Optional.empty();
            }
        } else {
            optShort = Optional.empty();
        }
        return optShort;
    }

    Optional<String> toString(Object v) {
        final Optional<String> optString;
        if (v == null) {
            optString = Optional.empty();
        } else if (v instanceof String) {
            String s = (String) v;
            optString = Optional.of(s);
        } else if (v instanceof StringBuilder || v instanceof StringBuffer) {
            String s = v.toString();
            optString = Optional.of(s);
        } else {
            optString = Optional.empty();
        }
        return optString;
    }

    public static interface EnumStringIntegerRepresentation {

        Enum<? extends Enum> getEnumValue();

        int getIntegerValue();

        String getStringValue();
    }

    Optional<Integer> toInteger(Object v, List<? extends EnumStringIntegerRepresentation> values) {
        final Optional<Integer> optInteger;

        if (v == null) {
            optInteger = Optional.empty();
        } else {
            Optional<Integer> optFound = Optional.empty();
            for (int i = 0; i < values.size() && optFound.isEmpty(); i += 1) {
                EnumStringIntegerRepresentation xxx = values.get(i);

                if (v.equals(xxx.getIntegerValue())) {
                    optFound = Optional.ofNullable(xxx.getIntegerValue());
                    break;
                } else if (v.equals(xxx.getStringValue())) {
                    optFound = Optional.ofNullable(xxx.getIntegerValue());
                    break;
                }
            }
            optInteger = optFound;
        }
        return optInteger;
    }
}
