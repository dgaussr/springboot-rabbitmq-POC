# Spring Boot [Publisher- Subscriber] - Rabbit MQ using Docker



Docker is required to be installed on the machine.

## Installation
1) Clone the Repo & run the following commands.

```bash
docker-compose up
```

## Output

Post a request using Postman on:-

```
localhost:8081/order/asia.china.beijing
localhost:8081/order/asia.china
localhost:8081/order/asia.japan.tokyo
```
Send this in body:- 

```c
{
"name" : "red shirt",
"qty" : 2,
"price" : 212
}
```

## Usage
### MessagingConfig.java
```java
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
```
### OrderPublisher.java
```java
@RestController
@RequestMapping("/order")
public class OrderPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping("/{place}")
    public String bookOrder(@RequestBody Order order, @PathVariable String place ) {
        order.setOrderId(UUID.randomUUID().toString());
        OrderStatus orderStatus = new OrderStatus(order, "PROCESS", "Order Successfully Placed "+ place);

        rabbitTemplate.convertAndSend(Constants.EXCHANGE, place, orderStatus);
        return "success!!";
    }
}
```
### User.java (consumer)
```java
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
```
### Constants.java 
Constants are defined in the class.

```java
public class Constants {
	public static final String EXCHANGE = "rabbit_exchange";
}
```
## Configuration
### docker-compose.yml

```c
version: '3.3'

services:
    #service 1: definition of mysql database
    rabbitmq:
      image: rabbitmq:3-management
      container_name: rabbitmq  
      restart: always
      ports:
        - "15672:15672"
        - "5672:5672"
    
    #service 3: definition of your spring-boot app 
    orderservice:                        #it is just a name, which will be used only in this file.
      image: order-service               #name of the image after dockerfile executes
      container_name: order-service-app  #name of the container created from docker image
      build:
        context: .                          #docker file path (. means root directory)
        dockerfile: Dockerfile              #docker file name
      ports:
        - "8081:8080"                       #docker containter port with your os port
      restart: always
      environment:
        - SPRING_RABBITMQ_HOST=rabbitmq  
      depends_on:                           #define dependencies of this app
        - rabbitmq                                #dependency name (which is defined with this name 'db' in this file earlier)
```
### Dockerfile
```c
FROM openjdk:11 as rabbitmq
EXPOSE 8081
WORKDIR /app

# Copy maven executable to the image
COPY mvnw .
COPY .mvn .mvn

# Copy the pom.xml file
COPY pom.xml .

# Copy the project source
COPY ./src ./src
COPY ./pom.xml ./pom.xml

RUN chmod 755 /app/mvnw

RUN ./mvnw package -DskipTests
ENTRYPOINT ["java","-jar","target/springboot-demo-activemq-0.0.1-SNAPSHOT.jar"]
```
