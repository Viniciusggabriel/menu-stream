package backend.server.MenuStream.model.repository.user

import backend.server.MenuStream.model.entity.user.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, UUID> {
    /**
     * Método que vai buscar usuário pelo nome
     * @param username: String Nome de usuário a ser buscado
     */
    fun findByUsername(username: String): Optional<User>;
}