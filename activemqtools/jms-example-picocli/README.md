# amqtools

A number of commandline tools for accessing Apache Activemq.

# Main0

```
Usage: Main0 [-hV] [--jms-transacted] [--activemq-brokerURL=<brokerURL>]
             [--activemq-host=<host>] [--activemq-password=<password>]
             [--activemq-port=<port>] [--activemq-user=<user>] -c=<command>
             [--jms-deliverymode=<deliveryMode>] [--jms-queue=<queueName>]
             [--jms-timetolive=<timeToLive>] [--jms-topic=<topicName>]
             [--nummessages=<numMessages>]
Invoke activemq jms operations as provided by activemq-exaplemes openwire
      --activemq-brokerURL=<brokerURL>
                            activemq brokerURL
                              Default: tcp://localhost:61616
      --activemq-host=<host>
                            activemq host
                              Default: localhost
      --activemq-password=<password>
                            activemq password
                              Default: password
      --activemq-port=<port>
                            activemq port
                              Default: 61616
      --activemq-user=<user>
                            activemq user
                              Default: admin
  -c, --command=<command>   command
                              [queueProducer|queueConsumer|topicPublisher|topicS
                              ubscriber|advisory|durSubPublisher|durSubSubscribe
                              r|browser|browserProducer|tempDestConsumer|tempDes
                              tProducerRequestReply]
  -h, --help                Show this help message and exit.
      --jms-deliverymode=<deliveryMode>
                            jms delivery mode [PERSISTENT|NON_PERSISTENT]
                              Default: NON_PERSISTENT
      --jms-queue=<queueName>
                            jms destination queue
                              Default: test-queue
      --jms-timetolive=<timeToLive>
                            jms timetolive [ms]
                              Default: 60000
      --jms-topic=<topicName>
                            jms destination topic
                              Default: test-topic
      --jms-transacted      jms transacted
      --nummessages=<numMessages>
                            count of messages to produce
                              Default: 100
  -V, --version             Print version information and exit.
```

# MainAdvisoryConsumer

```
Usage: MainAdvisoryConsumer [-hV] [--activemq-brokerURL=BROKER_URL]
                            [--activemq-password=<password>]
                            [--activemq-userName=<userName>]
                            [--jms-destination-advisory-topic=ADVISORY_TOPIC_NAM
                            E] [--jms-max-receive-count=MAX-RECEIVE-COUNT]
                            [--jms-max-waittime-seconds=MAX-WAITTIME-SECONDS]
                            [--jms-message-selector=MESSAGE-SELECTOR]
Consume messages from advisory topics
      --activemq-brokerURL=BROKER_URL
                  activemq brokerURL, format tcp://{host}:{port}, eg. `tcp://localost:61616'
                    Default: tcp://localhost:61616
      --activemq-password=<password>
                  activemq password
                    Default: password
      --activemq-userName=<userName>
                  activemq userName
                    Default: admin
  -h, --help      Show this help message and exit.
      --jms-destination-advisory-topic=ADVISORY_TOPIC_NAME
                  jms advisory topic name
                    Default: ActiveMQ.Advisory.>
      --jms-max-receive-count=MAX-RECEIVE-COUNT
                  terminate after receiving MAX_RECEIVE_COUNT advisory messages
                    Default: -1
      --jms-max-waittime-seconds=MAX-WAITTIME-SECONDS
                  max time waiting for a message
                    Default: 300
      --jms-message-selector=MESSAGE-SELECTOR
                  jms message-selector
  -V, --version   Print version information and exit.
```

# MainBrowserFactory
```
Usage: MainBrowserFactory [-hV] [--activemq-brokerURL=BROKER_URL]
                          [--activemq-password=<password>]
                          [--activemq-userName=<userName>]
                          --jms-destination-queue=QUEUE
                          [--jms-max-browse-count=MAX-COUNT]
                          [--jms-message-selector=MESSAGE-SELECTOR]
Browse messages from a queue
      --activemq-brokerURL=BROKER_URL
                  activemq brokerURL, format tcp://{host}:{port}, eg. `tcp://localost:61616'
                    Default: tcp://localhost:61616
      --activemq-password=<password>
                  activemq password
                    Default: password
      --activemq-userName=<userName>
                  activemq userName
                    Default: admin
  -h, --help      Show this help message and exit.
      --jms-destination-queue=QUEUE
                  jms destination queue name
      --jms-max-browse-count=MAX-COUNT
                  browse MAX-COUNT messages
                    Default: -1
      --jms-message-selector=MESSAGE-SELECTOR
                  jms message-selector
  -V, --version   Print version information and exit.
```

# MainConsumerFactory

```
Usage: MainConsumerFactory [-hV] [--jms-session-transacted]
                           [--activemq-brokerURL=BROKER_URL]
                           [--activemq-password=<password>]
                           [--activemq-userName=<userName>]
                           [--jms-max-receive-count=MAX-RECEIVE-COUNT]
                           [--jms-max-waittime-seconds=MAX-WAITTIME-SECONDS]
                           [--jms-message-selector=MESSAGE-SELECTOR]
                           [--jms-session-acknowledgemode=<acknowledgeMode>]
                           [COMMAND]
Receive messages from a queue or a topic
      --activemq-brokerURL=BROKER_URL
                  activemq brokerURL, format tcp://{host}:{port}, eg. `tcp://localost:61616'
                    Default: tcp://localhost:61616
      --activemq-password=<password>
                  activemq password
                    Default: password
      --activemq-userName=<userName>
                  activemq userName
                    Default: admin
  -h, --help      Show this help message and exit.
      --jms-max-receive-count=MAX-RECEIVE-COUNT
                  terminate after receiving MAX_RECEIVE_COUNT advisory messages
                    Default: 1
      --jms-max-waittime-seconds=MAX-WAITTIME-SECONDS
                  max time waiting for a message
                    Default: 300
      --jms-message-selector=MESSAGE-SELECTOR
                  jms message-selector
      --jms-session-acknowledgemode=<acknowledgeMode>
                  jms session acknowledge mode
                    Default: AUTO_ACKNOWLEDGE
      --jms-session-transacted
                  jms session transacted or non-transacted
  -V, --version   Print version information and exit.
Commands:
  queue         Receive messages from a queue
  topic         Receive messages from a topic
  durableTopic  Receive messages from a durable subscribed topic
```

# MainProducerFactory

```
Usage: MainProducerFactory [-hV] [--jms-session-transacted] [--message-stdin]
                           [--activemq-brokerURL=BROKER_URL]
                           [--activemq-password=<password>]
                           [--activemq-userName=<userName>]
                           [--jms-message-property=<jmsMessageProperty>]
                           [--jms-producer-deliverymode=<deliveryMode>]
                           [--jms-producer-priority=<priority>]
                           [--jms-producer-timetolive=<timeToLive>]
                           [--jms-session-acknowledgemode=<acknowledgeMode>]
                           [--message-file=<messageFile>]
                           [--message-file-charset=<messageFileCharset>]
                           [--message-text=<messageText>] [COMMAND]
Send messages to a queue or a topic
      --activemq-brokerURL=BROKER_URL
                        activemq brokerURL, format tcp://{host}:{port}, eg.
                          `tcp://localost:61616'
                          Default: tcp://localhost:61616
      --activemq-password=<password>
                        activemq password
                          Default: password
      --activemq-userName=<userName>
                        activemq userName
                          Default: admin
  -h, --help            Show this help message and exit.
      --jms-message-property=<jmsMessageProperty>
                        jms message property, format {type}:key=value;... type=
                          [boolean|byte|double|float|int|long|object|string|short]
      --jms-producer-deliverymode=<deliveryMode>
                        jms producer deliverymode value
                          [PERSISTENT|NON_PERSISTENT]
                          Default: PERSISTENT
      --jms-producer-priority=<priority>
                        jms producer priortiy, eg. `4'
                          Default: 4
      --jms-producer-timetolive=<timeToLive>
                        jms producer timetolive value in ms, eg. `60000'
                          Default: 60000
      --jms-session-acknowledgemode=<acknowledgeMode>
                        jms session acknowledge mode
                          Default: AUTO_ACKNOWLEDGE
      --jms-session-transacted
                        jms session transacted or non-transacted
      --message-file=<messageFile>
                        read message text from this file
      --message-file-charset=<messageFileCharset>
                        read message text using this charset
                          Default: UTF-8
      --message-stdin   read message text from stdin
      --message-text=<messageText>
                        read message text from option value
                          Default: Hello, world!
  -V, --version         Print version information and exit.
Commands:
  queue  Send messages to a queue
  topic  Send messages to a topic
```

# MainUtil

```
Usage: MainUtil [-hV] [COMMAND]
some utility commands
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
Commands:
  longToDate                 Convert long values to Date value and print both
                               values
  systemProperties           print system properties
  envProperties              print env properties
  activeMqConnectionFactory  print activemq properties
```
