package com.spring.config;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.spring.Constants;

@Configuration
public class MessagingConfig {
	@Bean
	public TopicExchange exchange() {
		return new TopicExchange(Constants.EXCHANGE);
	}

	@Bean
	public Queue queue1() {
		 return new Queue("topic-queue-1");
	}
	@Bean	
	public Binding binding1(Queue queue1, TopicExchange exchange) {
		return BindingBuilder.bind(queue1).to(exchange).with("asia.china.*");
	}

	@Bean
	public Queue queue2() {
		return new Queue("topic-queue-2");
	}
	@Bean
	public Binding binding2(Queue queue2, TopicExchange exchange) {
		return BindingBuilder.bind(queue2).to(exchange).with("asia.china.#");
	}

	@Bean
	public Queue queue3() {
		return new Queue("topic-queue-3");
	}
	@Bean
	public Binding binding3(Queue queue3, TopicExchange exchange) {
		return BindingBuilder.bind(queue3).to(exchange).with("asia.*.*");
	}

	
	@Bean
	public MessageConverter converter() {
		return new Jackson2JsonMessageConverter();
	}
	
	 @Bean
	    public AmqpTemplate template(ConnectionFactory connectionFactory) {
	        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
	        rabbitTemplate.setMessageConverter(converter());
	        return rabbitTemplate;
	    }
}
