package backend.server.MenuStream.infra.queue

import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class OrderConsumer {

    /**
     * Bean do RabbitMQ para receber as mensagens
     * @param message: String Recebe a mensagem por via desse parâmetro
     */
    @RabbitListener(queues = ["\${broker.queue.admin.name}"])
    fun listenOrderQueue(@Payload message: String) {
        println(message)
    }
}