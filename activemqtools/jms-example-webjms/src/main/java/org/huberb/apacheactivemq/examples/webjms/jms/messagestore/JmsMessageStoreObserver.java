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
package org.huberb.apacheactivemq.examples.webjms.jms.messagestore;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.Message;

/**
 *
 * @author pi
 */
public class JmsMessageStoreObserver {
    
    @Inject
    private JmsMessageStore jmsMessageStore;
    
    public static class JmsMessageStoreEvent {
        
        javax.jms.Message message;
        String category;
        
        public JmsMessageStoreEvent(Message message, String category) {
            this.message = message;
            this.category = category;
        }
        
        String category() {
            return this.category;
        }
        
        javax.jms.Message message() {
            return this.message;
        }
    }
    
    public void observes(@Observes JmsMessageStoreEvent jmsMessageStoreEvent) {
        javax.jms.Message message = jmsMessageStoreEvent.message();
        JmsMessageStore.JmsMessagStoreEntry e = new JmsMessageStore.JmsMessagStoreEntry(message);
        
        this.jmsMessageStore.addJmsMessage(JmsMessageStore.JmsMessageStoreCategory.adHocMessageA, e);
    }
}
