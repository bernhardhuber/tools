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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.jms.Message;

/**
 *
 * @author pi
 */
@ApplicationScoped
public class JmsMessageStore {

    public static class JmsMessagStoreEntry {

        private javax.jms.Message message;

        public JmsMessagStoreEntry(Message message) {
            this.message = message;
        }

        public javax.jms.Message getMessage() {
            return this.message;
        }
    }

    public enum JmsMessageStoreCategory {
        logMessage,
        messageListenerQueueA,
        messageListenerTopicA,
        adHocMessageA,
        adHocMessageB
    }

    private final Map<Enum, List<JmsMessagStoreEntry>> m;
    private int maxSizePerList;

    public JmsMessageStore() {
        this.m = Collections.synchronizedMap(new HashMap<>());
        this.maxSizePerList = 100;
    }

    public void addJmsMessage(JmsMessageStoreCategory c, JmsMessagStoreEntry e) {
        final List<JmsMessagStoreEntry> l = this.m.getOrDefault(e, new ArrayList<>());
        if (this.maxSizePerList > 0 && l.size() >= this.maxSizePerList) {
            l.remove(0);
        }
        l.add(e);
        this.m.put(c, l);
    }

    public List<JmsMessagStoreEntry> retrieveJmsMessageList(JmsMessageStoreCategory c) {
        final List<JmsMessagStoreEntry> l = this.m.getOrDefault(c, new ArrayList<>());
        final List<JmsMessagStoreEntry> lCopy = new ArrayList<>(l);
        final List<JmsMessagStoreEntry> lRet = Collections.unmodifiableList(lCopy);
        return lRet;
    }
}
