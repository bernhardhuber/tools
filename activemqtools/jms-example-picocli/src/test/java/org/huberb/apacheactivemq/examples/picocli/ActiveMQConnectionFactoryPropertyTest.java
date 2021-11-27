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
package org.huberb.apacheactivemq.examples.picocli;

import java.util.Properties;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.Test;

/**
 *
 * @author pi
 */
public class ActiveMQConnectionFactoryPropertyTest {

    public ActiveMQConnectionFactoryPropertyTest() {
    }

    @org.junit.jupiter.api.BeforeAll
    public static void setUpClass() throws Exception {
    }

    @org.junit.jupiter.api.AfterAll
    public static void tearDownClass() throws Exception {
    }

    @org.junit.jupiter.api.BeforeEach
    public void setUp() throws Exception {
    }

    @org.junit.jupiter.api.AfterEach
    public void tearDown() throws Exception {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void hello() {
        final String userName = "admin";
        final String password = "password";
        final String brokerURL = "tcp://localhost:61626";
        //---
        final ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(userName, password, brokerURL);

        Properties props = new Properties();
        activeMQConnectionFactory.populateProperties(props);
        System.out.println("activeMQConnectionFactory props ");
        props.list(System.out);
        System.out.flush();
    }
}
