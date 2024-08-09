package backend.server.MenuStream.infra.config

import backend.server.MenuStream.application.dto.UserRequestDto
import backend.server.MenuStream.application.service.UserService
import backend.server.MenuStream.model.entity.user.Role
import backend.server.MenuStream.model.entity.user.User
import backend.server.MenuStream.model.repository.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment


@Configuration
class DefaultUserInDatabase {
    @Autowired
    private val environment: Environment? = null

    @Bean
    fun commandLineRunner(
        userRepository: UserRepository,
        userService: UserService
    ): CommandLineRunner {
        val username: String = environment?.getProperty("name.admin").toString()
        val password: String = environment?.getProperty("password.admin").toString()

        return CommandLineRunner { args: Array<String?>? ->
            if (userRepository.findByUsername(username).isEmpty) {
                val admin: User = User(
                    username = username,
                    password = password,
                    role = Role.ADM
                )

                val registerRequestDTO: UserRequestDto =
                    UserRequestDto(admin.username, admin.password, admin.role)

                userService.saveUser(registerRequestDTO)
            }
        }
    }
}
