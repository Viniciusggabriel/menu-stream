package backend.server.MenuStream.application.service

import backend.server.MenuStream.application.dto.UserRequestDto
import backend.server.MenuStream.application.dto.UserResponseDto
import backend.server.MenuStream.infra.exception.custom.UserAlreadyExists
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
     * Esse teste pretende buscar um usuário dentro do banco de dados e verificar se ele bate com o usuário anteriormente salvo pelo repositório.
     *
     * 1. Cria um usuário dentro do banco de dados, via repositório;
     * 2. Busca esse usuário via repositório e garante que ele foi salvo;
     * 3. Utiliza o serviço para buscar o usuário e compara o usuário achado pelo service com o usuário achado pelo Mockito.
     *
     * Pretende buscar um usuário dentro do banco de dados e obter resultados positivos.
     */
    @Test
    fun `The user is present in the database`() {
        val user: User = User(
            username = "Teste",
            password = "Teste",
            role = Role.EMPLOYEE
        )

        Mockito.`when`(userRepository.save(user)).thenReturn(user)
        Mockito.`when`(userRepository.findByUsername(user.username)).thenReturn(Optional.of(user))

        val userInDatabase: User? = userService.userIsPresent(user.username)

        assertEquals(
            user, userInDatabase, "O resultado esperado era: ${user.username} + ${user.password} + ${user.role}" +
                    "\nPorém foi obtido: ${userInDatabase?.username} + ${userInDatabase?.password} + ${userInDatabase?.role}"
        )
    }

    /**
     * Esse teste pretende buscar o usuário dentro do banco de dados, porem não encontrar e assim retornar um erro adequado.
     *
     * 1. Utiliza o Mockito para verificar o usuário realmente não existe;
     * 2. Chama o serviço para buscar o usuário;
     * 3. Compara o resultado do serviço com a exceção UserNotFound lançada no serviço.
     *
     * Pretende buscar um usuário inexistente e obter uma mensagem de erro adequada com a exceção.
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
     * Esse teste pretende salvar um usuário e verificar se ele foi realmente salvo e verificar a mensagem de retorno contendo o sucesso.
     *
     * 1. Instancia um objeto com os dados do usuário para salvar no banco de dados;
     * 2. Passa a senha do usuário por um algoritmo de criptografia de senhas e verifica se tudo ocorreu corretamente;
     * 3. Salva o usuário via repositório para verificar o retorno;
     * 4. Chama o serviço para salvar o usuário e guardar a mensagem de sucesso;
     * 5. Compara a mensagem obtida com a esperada.
     *
     * Pretende salvar o usuário e obter uma mensagem de sucesso ao realizar a operação.
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

        Mockito.`when`(passwordEncoder.encode(userRequest.password)).thenReturn("encodedPassword")
        Mockito.`when`(userRepository.save(Mockito.any(User::class.java))).thenReturn(user)

        val response: UserResponseDto = userService.saveUser(userRequest)

        assertEquals(UserResponseDto("Usuário salvo com sucesso!", user.username), response)
    }

    /**
     * Esse teste pretende salvar um usuário que já está salvo e verificar a mensagem de erro e se o exception está sendo retornado de forma correta
     *
     * 1. Instancia um objeto com os dados do usuário para salvar no banco de dados;
     * 2. Passa a senha do usuário por um algoritmo de criptografia de senhas e verifica se tudo ocorreu corretamente;
     * 3. Salva o usuário via repositório para verificar o retorno, e verifica se o retorno já vai ser um erro ou vai salvar o usuário;
     * 4. Chama o serviço para salvar o usuário e guardar a mensagem de erro;
     * 5. Compara a mensagem obtida com a esperada.
     *
     * Pretende salvar o usuário e obter uma mensagem de erro ao realizar a operação.
     */
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

        Mockito.`when`(passwordEncoder.encode(userRequest.password)).thenReturn("encodedPassword")

        Mockito.`when`(userRepository.save(Mockito.any(User::class.java)))
            .thenAnswer { invocation ->
                val userToSave = invocation.getArgument<User>(0)

                // Se o usuário já existir (compara o username), lança a exceção
                if (userToSave.username == user.username) {
                    throw UserAlreadyExists("O usuário inserido já existe!")
                }

                userToSave // Retorna o usuário salvo
            }

        Mockito.`when`(userRepository.findByUsername(user.username))
            .thenReturn(Optional.of(user))

        // Salva novamente o usuário para entrar em conflito com o que o bloco Mocki.`when`já hávia salvado
        val exception = assertThrows(UserAlreadyExists::class.java) {
            userService.saveUser(userRequest)
        }

        assertEquals("O usuário inserido já existe!", exception.message)
    }

    //TODO: Fazer testes para verificar role errada
}