#!/bin/sh
set -x

QUEUE="-DQUEUE=queue://amqp-queue1"
DESTINATION="$QUEUE"

java \
  $DESTINATION \
  -jar target/amqp-example-0.1-SNAPSHOT-listener.jar

