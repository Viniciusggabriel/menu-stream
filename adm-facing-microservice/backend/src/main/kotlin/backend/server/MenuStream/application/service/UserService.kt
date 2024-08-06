package backend.server.MenuStream.application.service

import backend.server.MenuStream.infra.exception.user_not_found.UserNotFound
import backend.server.MenuStream.model.entity.user.User
import backend.server.MenuStream.model.repository.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService {
    @Autowired
    private lateinit var userRepository: UserRepository

    fun userIsPresent(username: String): User? {
        return userRepository.findByUsername(username).orElseThrow {
            UserNotFound("Usuário não encontrado", 404)
        };
    }
}