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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author pi
 */
public class EnumRepresentationsTest {

    public EnumRepresentationsTest() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testSomeMethod() {
    }

    @Test
    public void testDeliveryModeRepresentation() {
        assertEquals(DeliveryMode.NON_PERSISTENT, EnumRepresentations.DeliveryModeRepresentation.non_persistent.getIntegerValue());
        assertEquals("NON_PERSISTENT", EnumRepresentations.DeliveryModeRepresentation.non_persistent.getStringValue());

        assertEquals(DeliveryMode.PERSISTENT, EnumRepresentations.DeliveryModeRepresentation.persistent.getIntegerValue());
        assertEquals("PERSISTENT", EnumRepresentations.DeliveryModeRepresentation.persistent.getStringValue());
    }
    @Test
    public void testSessionAcknowledgeRepresentation() {
        assertEquals(Session.AUTO_ACKNOWLEDGE, EnumRepresentations.SessionAcknowledgeRepresentation.auto_acknowledge.getIntegerValue());
        assertEquals(Session.CLIENT_ACKNOWLEDGE, EnumRepresentations.SessionAcknowledgeRepresentation.client_acknowledge.getIntegerValue());
        assertEquals(Session.DUPS_OK_ACKNOWLEDGE, EnumRepresentations.SessionAcknowledgeRepresentation.dups_ok_acknowledge.getIntegerValue());
        assertEquals(Session.SESSION_TRANSACTED, EnumRepresentations.SessionAcknowledgeRepresentation.session_transacted.getIntegerValue());
    }
}
