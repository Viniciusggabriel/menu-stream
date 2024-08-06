package backend.server.MenuStream.application.service

import backend.server.MenuStream.model.entity.user.Role
import backend.server.MenuStream.model.entity.user.User
import backend.server.MenuStream.model.repository.user.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.Mock
import org.springframework.security.crypto.password.PasswordEncoder
import kotlin.test.Test

class UserServiceTest {
    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var userService: UserService
    private lateinit var passwordEncoder: PasswordEncoder

    @Test
    fun `The user is present in the database`() {
        val user: User = User(
            username = "Vinicius",
            password = passwordEncoder.encode("Vinicius"),
            Role.EMPLOYEE
        );

        createUser(user);
        val userInDatabase: User? = userService.userIsPresent(user.username)

        assertEquals(
            user,
            userInDatabase,
            "O resultado esperado era: ${user.username} + ${user.password} + ${user.role}" +
                    "\nPorem foi obtido: ${userInDatabase?.username} + ${userInDatabase?.password} + ${userInDatabase?.role}"
        )
    }

    private fun createUser(user: User) {
        userRepository.save(user);
    }
}