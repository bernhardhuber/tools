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

import javax.jms.DeliveryMode;
import javax.jms.Session;

/**
 *
 * @author pi
 */
public class EnumRepresentations {

    //---
    public enum DeliveryModeRepresentation implements ToConverters.EnumStringIntegerRepresentation {
        non_persistent(DeliveryMode.NON_PERSISTENT, "NON_PERSISTENT"),
        persistent(DeliveryMode.PERSISTENT, "PERSISTENT");
        private final int integerValue;
        private final String stringValue;

        private DeliveryModeRepresentation(int i, String s) {
            this.integerValue = i;
            this.stringValue = s;
        }

        @Override
        public int getIntegerValue() {
            return this.integerValue;
        }

        @Override
        public String getStringValue() {
            return this.stringValue;
        }

        @Override
        public Enum<? extends Enum> getEnumValue() {
            return this;
        }
    }

    public enum SessionAcknowledgeRepresentation implements ToConverters.EnumStringIntegerRepresentation {
        auto_acknowledge(Session.AUTO_ACKNOWLEDGE, "AUTO_ACKNOWLEDGE"),
        client_acknowledge(Session.CLIENT_ACKNOWLEDGE, "CLIENT_ACKNOWLEDGE"),
        dups_ok_acknowledge(Session.DUPS_OK_ACKNOWLEDGE, "DUPS_OK_ACKNOWLEDGE"),
        session_transacted(Session.SESSION_TRANSACTED, "SESSION_TRANSACTED");
        private final int integerValue;
        private final String stringValue;

        private SessionAcknowledgeRepresentation(int i, String s) {
            this.integerValue = i;
            this.stringValue = s;
        }

        @Override
        public Enum<? extends Enum> getEnumValue() {
            return this;
        }

        @Override
        public int getIntegerValue() {
            return this.integerValue;
        }

        @Override
        public String getStringValue() {
            return this.stringValue;
        }
    }

}
