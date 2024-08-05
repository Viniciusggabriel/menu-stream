package backend.server.MenuStream.infra.config

import org.springframework.amqp.core.Queue
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfig {
    /**
     * Busca o nome da fila
     */
    @Value("\${broker.queue.admin.name}")
    private val queue: String = ""

    /**
     * Declarando fila, recebe o nome de dentro do application.properties
     */
    @Bean
    fun queue(): Queue {
        return Queue(queue, true)
    }
}