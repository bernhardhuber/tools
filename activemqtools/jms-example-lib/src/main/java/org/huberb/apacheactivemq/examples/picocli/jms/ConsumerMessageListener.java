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

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author pi
 */
class ConsumerMessageListener implements MessageListener {
    
    private static final Logger logger = LoggerFactory.getLogger(ConsumerMessageListener.class);
    private final int maxReceiveCount;
    private int receivedCount;
    private final CountDownLatch latch;
    private final Consumer<Message> messageConsumer;

    ConsumerMessageListener(Consumer<Message> messageConsumer, int maxReceiveCount, CountDownLatch latch) {
        this.messageConsumer = messageConsumer;
        this.maxReceiveCount = maxReceiveCount;
        this.receivedCount = 0;
        this.latch = latch;
    }

    @Override
    public void onMessage(Message message) {
        boolean countDownLatch = false;
        try {
            receivedCount += 1;
            countDownLatch = (this.maxReceiveCount >= 0 && receivedCount >= this.maxReceiveCount);
            messageConsumer.accept(message);
        } catch (Exception e) {
            logger.warn("onMessage", e);
        } finally {
            if (countDownLatch) {
                latch.countDown();
            }
        }
    }
    
}
