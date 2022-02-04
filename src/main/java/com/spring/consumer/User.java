package com.spring.consumer;

import com.spring.entity.OrderStatus;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class User {
	@RabbitListener(queues = "topic-queue-1" )
	public void consumeMessageFromQueue1(OrderStatus orderStatus) {
		System.out.println("Message Received from topic-queue-1(asia.china.*): " +orderStatus );
	}

	@RabbitListener(queues = "topic-queue-2" )
	public void consumeMessageFromQueue2(OrderStatus orderStatus) {
		System.out.println("Message Received from topic-queue-2(asia.china.#): " +orderStatus );
	}

	@RabbitListener(queues = "topic-queue-3" )
	public void consumeMessageFromQueue3(OrderStatus orderStatus) {
		System.out.println("Message Received from topic-queue-3(asia.*.*): " +orderStatus );
	}
}
