package backend.server.MenuStream.application.service

import backend.server.MenuStream.infra.exception.user_not_found.UserNotFound
import backend.server.MenuStream.model.entity.user.Role
import backend.server.MenuStream.model.entity.user.User
import backend.server.MenuStream.model.repository.user.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.*
import kotlin.test.Test

class UserServiceTest {
    @Mock
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    /**
     * Teste para verificar se o serviço está salvando o usuário de forma correta, no momento não é feito o encrypt da senha devido ao mockito não carregar o @Bean do spring security
     */
    @Test
    fun `The user is present in the database`() {
        val user: User = User(
            username = "Vinicius",
            password = "Vinicius",
            role = Role.EMPLOYEE
        );

        Mockito.`when`(userRepository.findByUsername(user.username)).thenReturn(Optional.of(user))

        createUser(user);
        val userInDatabase: User? = userService.userIsPresent(user.getUsername())

        assertEquals(
            user,
            userInDatabase,
            "O resultado esperado era: ${user.username} + ${user.password} + ${user.role}" +
                    "\nPorem foi obtido: ${userInDatabase?.username} + ${userInDatabase?.password} + ${userInDatabase?.role}"
        )
    }

    /**
     * Teste que verifica o erro retornado ao não encontrar o usuário, espera um valor vázio e uma mensagem de erro ao buscar o "User🤷‍♂️"
     */
    @Test
    fun `The user is not present in the database`() {
        Mockito.`when`(userRepository.findByUsername("User🤷‍♂️")).thenReturn(Optional.empty())

        val exception = assertThrows(UserNotFound::class.java) {
            userService.userIsPresent("User🤷‍♂️")
        }

        assertEquals("Usuário não encontrado", exception.message)
    }

    /**
     * Função auxiliar para criar o usuário, será util caso tenhamos testes futuros
     */
    private fun createUser(user: User) {
        userRepository.save(user);
    }
}