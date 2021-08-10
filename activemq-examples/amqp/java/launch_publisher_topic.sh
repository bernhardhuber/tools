#!/bin/sh

TOPIC="-DTOPIC=topic://amqp-topic1"
DESTINATION="$TOPIC"

java \
  $DESTINATION \
  -DNUM_MESSAGES=1000 -DTIME_TO_LIVE=3600000 \
  -jar target/amqp-example-0.1-SNAPSHOT-publisher.jar

