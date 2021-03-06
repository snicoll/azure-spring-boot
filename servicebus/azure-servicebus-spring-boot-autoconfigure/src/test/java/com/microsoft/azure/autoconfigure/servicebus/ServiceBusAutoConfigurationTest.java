/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.autoconfigure.servicebus;

import com.microsoft.azure.servicebus.QueueClient;
import com.microsoft.azure.servicebus.SubscriptionClient;
import com.microsoft.azure.servicebus.TopicClient;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ServiceBusAutoConfigurationTest {
    @Test
    public void returnNullIfSetConnectionStringOnly() {
        System.setProperty(Constants.CONNECTION_STRING_PROPERTY, Constants.INVALID_CONNECTION_STRING);

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(ServiceBusAutoConfiguration.class);
            context.refresh();

            Exception queueException = null;
            try {
                context.getBean(QueueClient.class);
            } catch (Exception e) {
                queueException = e;
            }
            assertThat(queueException).isNotNull();
            assertThat(queueException.getMessage()).contains(
                    "No qualifying bean of type 'com.microsoft.azure.servicebus.QueueClient' available");
            assertThat(queueException).isExactlyInstanceOf(NoSuchBeanDefinitionException.class);

            Exception topicException = null;
            try {
                context.getBean(TopicClient.class);
            } catch (Exception e) {
                topicException = e;
            }
            assertThat(topicException).isNotNull();
            assertThat(topicException.getMessage()).contains(
                    "No qualifying bean of type 'com.microsoft.azure.servicebus.TopicClient' available");
            assertThat(topicException).isExactlyInstanceOf(NoSuchBeanDefinitionException.class);

            Exception subException = null;
            try {
                context.getBean(SubscriptionClient.class);
            } catch (Exception e) {
                subException = e;
            }
            assertThat(subException).isNotNull();
            assertThat(subException.getMessage()).contains(
                    "No qualifying bean of type 'com.microsoft.azure.servicebus.SubscriptionClient' available");
            assertThat(subException).isExactlyInstanceOf(NoSuchBeanDefinitionException.class);
        }

        System.clearProperty(Constants.CONNECTION_STRING_PROPERTY);
    }

    @Test
    public void cannotAutowireQueueClientWithInvalidConnectionString() {
        System.setProperty(Constants.CONNECTION_STRING_PROPERTY, Constants.INVALID_CONNECTION_STRING);
        System.setProperty(Constants.QUEUE_NAME_PROPERTY, Constants.QUEUE_NAME);
        System.setProperty(Constants.QUEUE_RECEIVE_MODE_PROPERTY, Constants.QUEUE_RECEIVE_MODE.name());

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(ServiceBusAutoConfiguration.class);
            context.refresh();

            QueueClient queueClient = null;

            Exception exception = null;
            try {
                queueClient = context.getBean(QueueClient.class);
            } catch (Exception e) {
                exception = e;
            }

            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).contains(
                    "IllegalConnectionStringFormatException: Connection String cannot be parsed");
            assertThat(exception).isExactlyInstanceOf(BeanCreationException.class);

            assertThat(queueClient).isNull();
        }

        System.clearProperty(Constants.CONNECTION_STRING_PROPERTY);
        System.clearProperty(Constants.QUEUE_NAME_PROPERTY);
        System.clearProperty(Constants.QUEUE_RECEIVE_MODE_PROPERTY);
    }

    @Test
    public void cannotAutowireTopicClientWithInvalidConnectionString() {
        System.setProperty(Constants.CONNECTION_STRING_PROPERTY, Constants.INVALID_CONNECTION_STRING);
        System.setProperty(Constants.TOPIC_NAME_PROPERTY, Constants.TOPIC_NAME);

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(ServiceBusAutoConfiguration.class);
            context.refresh();

            TopicClient topicClient = null;

            Exception exception = null;
            try {
                topicClient = context.getBean(TopicClient.class);
            } catch (Exception e) {
                exception = e;
            }

            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).contains(
                    "IllegalConnectionStringFormatException: Connection String cannot be parsed");
            assertThat(exception).isExactlyInstanceOf(BeanCreationException.class);

            assertThat(topicClient).isNull();
        }

        System.clearProperty(Constants.CONNECTION_STRING_PROPERTY);
        System.clearProperty(Constants.TOPIC_NAME_PROPERTY);
    }

    @Test
    public void cannotAutowireSubscriptionClientWithInvalidConnectionString() {
        System.setProperty(Constants.CONNECTION_STRING_PROPERTY, Constants.INVALID_CONNECTION_STRING);
        System.setProperty(Constants.TOPIC_NAME_PROPERTY, Constants.TOPIC_NAME);
        System.setProperty(Constants.SUBSCRIPTION_NAME_PROPERTY, Constants.SUBSCRIPTION_NAME);
        System.setProperty(Constants.SUBSCRIPTION_RECEIVE_MODE_PROPERTY, Constants.SUBSCRIPTION_RECEIVE_MODE.name());

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(ServiceBusAutoConfiguration.class);
            context.refresh();

            SubscriptionClient subscriptionClient = null;

            Exception exception = null;
            try {
                subscriptionClient = context.getBean(SubscriptionClient.class);
            } catch (Exception e) {
                exception = e;
            }

            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).contains(
                    "IllegalConnectionStringFormatException: Connection String cannot be parsed");
            assertThat(exception).isExactlyInstanceOf(BeanCreationException.class);

            assertThat(subscriptionClient).isNull();
        }

        System.clearProperty(Constants.CONNECTION_STRING_PROPERTY);
        System.clearProperty(Constants.TOPIC_NAME_PROPERTY);
        System.clearProperty(Constants.SUBSCRIPTION_NAME_PROPERTY);
        System.clearProperty(Constants.SUBSCRIPTION_RECEIVE_MODE_PROPERTY);
    }

    @Test
    public void cannotAutowireSubscriptionClientWithInvalidCredential() {
        System.setProperty(Constants.CONNECTION_STRING_PROPERTY, Constants.CONNECTION_STRING);
        System.setProperty(Constants.TOPIC_NAME_PROPERTY, Constants.TOPIC_NAME);
        System.setProperty(Constants.SUBSCRIPTION_NAME_PROPERTY, Constants.SUBSCRIPTION_NAME);
        System.setProperty(Constants.SUBSCRIPTION_RECEIVE_MODE_PROPERTY, Constants.SUBSCRIPTION_RECEIVE_MODE.name());

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(ServiceBusAutoConfiguration.class);
            context.refresh();

            SubscriptionClient subscriptionClient = null;

            Exception exception = null;
            try {
                subscriptionClient = context.getBean(SubscriptionClient.class);
            } catch (Exception e) {
                exception = e;
            }

            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).contains(
                    "BeanInstantiationException: Failed to instantiate [com.microsoft.azure.servicebus." +
                            "SubscriptionClient]: Factory method 'subscriptionClient' threw exception;");
            assertThat(exception).isExactlyInstanceOf(BeanCreationException.class);

            assertThat(subscriptionClient).isNull();
        }

        System.clearProperty(Constants.CONNECTION_STRING_PROPERTY);
        System.clearProperty(Constants.TOPIC_NAME_PROPERTY);
        System.clearProperty(Constants.SUBSCRIPTION_NAME_PROPERTY);
        System.clearProperty(Constants.SUBSCRIPTION_RECEIVE_MODE_PROPERTY);
    }
}
