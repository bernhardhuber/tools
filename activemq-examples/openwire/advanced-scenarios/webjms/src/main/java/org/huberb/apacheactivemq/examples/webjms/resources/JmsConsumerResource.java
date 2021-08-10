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
package org.huberb.apacheactivemq.examples.webjms.resources;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 *
 * @author
 */
@Path("jmsconsumer")
public class JmsConsumerResource {

    //------
    @POST
    public Response receiveQueueMessage() {
        return Response
                .ok("receiveQueueMessage")
                .build();
    }

    @POST
    public Response receiveTopicMessage() {
        return Response
                .ok("receiveTopicMessage")
                .build();
    }

    //------
    @POST
    public Response receiveAdvisoryTopicMessage() {
        return Response
                .ok("receiveAdvisoryTopicMessage")
                .build();
    }

    //------
    @POST
    public Response subscribeAsDurableSubscriber() {
        return Response
                .ok("subscribeAsDurableSubscriber")
                .build();
    }

    @POST
    public Response unsubscribeADurableSubscriber() {
        return Response
                .ok("unsubscribeADurableSubscriber")
                .build();
    }

    @POST
    public Response receiveTopicMessageAsDurableSubscriber() {
        return Response
                .ok("receiveTopicMessageAsDurableSubscriber")
                .build();
    }

    //------
}
