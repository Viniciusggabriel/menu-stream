package backend.server.MenuStream.application.service

import backend.server.MenuStream.application.dto.UserRequestDto
import backend.server.MenuStream.application.dto.UserResponseDto
import backend.server.MenuStream.infra.exception.custom.UserNotFound
import backend.server.MenuStream.model.entity.user.Role
import backend.server.MenuStream.model.entity.user.User
import backend.server.MenuStream.model.repository.user.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*
import kotlin.test.Test

class UserServiceTest {
    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    private lateinit var userService: UserService

    /**
     * Inicializa o userService
     */
    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        userService = UserService(userRepository, passwordEncoder)
    }

    /**
     * Teste para a busca de um usuário com sucesso.
     *
     * Este teste valida o comportamento do serviço de busca de usuário quando o usuário existe. O teste segue os seguintes passos:
     *
     * 1. Cria uma entidade de usuário e um objeto de entrada com os parâmetros esperados.
     * 2. Utiliza o Mockito para configurar e verificar o comportamento das dependências mockadas, como o repositório de usuários.
     * 3. Chama o serviço de busca para recuperar o usuário.
     * 4. Verifica se o usuário retornado pelo serviço corresponde ao esperado.
     *
     * O objetivo é assegurar que o serviço de busca retorna o usuário corretamente quando ele existe.
     */
    @Test
    fun `The user is present in the database`() {
        val user: User = User(
            username = "Teste",
            password = "Teste",
            role = Role.EMPLOYEE
        )

        Mockito.`when`(userRepository.findByUsername(user.username)).thenReturn(Optional.of(user))

        val userInDatabase: User? = userService.userIsPresent(user.username)

        assertEquals(
            user, userInDatabase, "O resultado esperado era: ${user.username} + ${user.password} + ${user.role}" +
                    "\nPorém foi obtido: ${userInDatabase?.username} + ${userInDatabase?.password} + ${userInDatabase?.role}"
        )
    }

    /**
     * Teste para a busca de um usuário por meio do seu username com erro.
     *
     * Este teste valida o comportamento do serviço de busca de usuário quando o usuário não existe. O teste segue os seguintes passos:
     *
     * 1. Utiliza o Mockito para configurar e verificar o comportamento das dependências mockadas, como o repositório de usuários.
     * 2. Configura o repositório para garantir que o usuário a ser buscado não exista.
     * 3. Chama o serviço de busca e captura a mensagem retornada.
     * 4. Verifica se a mensagem retornada pelo serviço corresponde à mensagem esperada para um usuário não encontrado.
     *
     * O objetivo é assegurar que o serviço de busca retorna uma mensagem adequada quando o usuário não é encontrado.
     */
    @Test
    fun `The user is not present in the database`() {
        Mockito.`when`(userRepository.findByUsername("User")).thenReturn(Optional.empty())

        val exception = assertThrows(UserNotFound::class.java) {
            userService.userIsPresent("User")
        }

        assertEquals("Usuário não encontrado", exception.message)
        assertEquals("User", exception.username)
    }

    /**
     * Teste para a busca de um usuário por meio do seu username com erro.
     *
     * Este teste valida o comportamento do serviço de busca de usuário quando o usuário não existe. O teste segue os seguintes passos:
     *
     * 1. Utiliza o Mockito para configurar e verificar o comportamento das dependências mockadas, como o repositório de usuários.
     * 2. Configura o repositório para garantir que o usuário a ser buscado não exista.
     * 3. Chama o serviço de busca e captura a mensagem retornada.
     * 4. Verifica se a mensagem retornada pelo serviço corresponde à mensagem esperada para um usuário não encontrado.
     *
     * O objetivo é assegurar que o serviço de busca retorna uma mensagem adequada quando o usuário não é encontrado.
     */
    @Test
    fun `Create user within the database`() {
        val user = User(
            username = "Teste",
            password = "Teste",
            role = Role.EMPLOYEE
        )

        val userRequest = UserRequestDto(
            username = user.username,
            password = user.password,
            role = user.role
        )

        Mockito.`when`(userRepository.save(Mockito.any(User::class.java))).thenReturn(user)
        Mockito.`when`(passwordEncoder.encode(userRequest.password)).thenReturn("encodedPassword")

        val response: UserResponseDto = userService.saveUser(userRequest)

        assertEquals(UserResponseDto("Usuário salvo com sucesso!", user.username), response)
    }

    @Test
    fun `Username exists or contains an error`() {
        val user = User(
            username = "Teste",
            password = "Teste",
            role = Role.EMPLOYEE
        )

        val userRequest = UserRequestDto(
            username = user.username,
            password = user.password,
            role = user.role
        )
        // TODO: Fazer teste de usuário já existente
    }
}