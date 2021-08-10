#!/bin/sh

TOPIC="-DTOPIC=topic://amqp-topic1"
DESTINATION="$TOPIC"

java \
  $DESTINATION \
  -jar target/amqp-example-0.1-SNAPSHOT-listener.jar

