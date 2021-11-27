package org.huberb.apacheactivemq.examples.webjms.resources;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 *
 * @author
 */
@Path("javaee8")
public class JavaEE8Resource {

    @GET
    public Response ping() {
        return Response
                .ok("ping")
                .build();
    }

    //------
    @POST
    public Response produceQueueMessage() {
        return Response
                .ok("produceQueueMessage")
                .build();
    }

    @POST
    public Response produceTopicMessage() {
        return Response
                .ok("produceQueueMessage")
                .build();
    }

    //------
    @GET
    public Response browseQueueMessage() {
        return Response
                .ok("browseQueueMessage")
                .build();
    }

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
