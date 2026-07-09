package com.yas.commonlibrary.kafka.cdc;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Logger;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;

/**
 * Base class for CDC (Change Data Capture) Kafka consumers.
 * Provides common methods for processing messages and handling Dead Letter Topic (DLT) events.
 *
 * @param <K> Type of the message payload.
 */
public abstract class BaseCdcConsumer<K, V> {

    public static final Logger LOGGER = Logger.getLogger(BaseCdcConsumer.class.getName());
    public static final String RECEIVED_MESSAGE_HEADERS = "## Received message - headers: {}";
    public static final String PROCESSING_RECORD_KEY_VALUE = "## Processing record - Key: {} | Value: {}";
    public static final String RECORD_PROCESSED_SUCCESSFULLY_KEY = "## Record processed successfully - Key: {} \n";

    protected void processMessage(V record, MessageHeaders headers, Consumer<V> consumer) {
        LOGGER.fine(format(RECEIVED_MESSAGE_HEADERS, headers));
        LOGGER.fine(format(PROCESSING_RECORD_KEY_VALUE, headers.get(KafkaHeaders.RECEIVED_KEY), record));
        consumer.accept(record);
        LOGGER.fine(format(RECORD_PROCESSED_SUCCESSFULLY_KEY, headers.get(KafkaHeaders.RECEIVED_KEY)));
    }

    protected void processMessage(K key, V value, MessageHeaders headers, BiConsumer<K, V> consumer) {
        LOGGER.fine(format(RECEIVED_MESSAGE_HEADERS, headers));
        LOGGER.fine(format(PROCESSING_RECORD_KEY_VALUE, key, value));
        consumer.accept(key, value);
        LOGGER.fine(format(RECORD_PROCESSED_SUCCESSFULLY_KEY, key));
    }

    private String format(String template, Object... args) {
        String message = template;
        if (args == null) {
            return message;
        }
        for (Object arg : args) {
            message = message.replaceFirst("\\{}", arg == null ? "null" : arg.toString());
        }
        return message;
    }
}
