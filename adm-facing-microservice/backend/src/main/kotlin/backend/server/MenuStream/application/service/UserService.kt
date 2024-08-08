package backend.server.MenuStream.application.service

import backend.server.MenuStream.application.dto.UserRequestDto
import backend.server.MenuStream.application.dto.UserResponseDto
import backend.server.MenuStream.infra.exception.custom.IncorrectUserRole
import backend.server.MenuStream.infra.exception.custom.UserAlreadyExists
import backend.server.MenuStream.infra.exception.custom.UserNotFound
import backend.server.MenuStream.model.entity.user.Role
import backend.server.MenuStream.model.entity.user.User
import backend.server.MenuStream.model.repository.user.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    /**
     * Função para buscar usuário dentro do banco de dados
     * @param username: String - Nome de usuário a ser buscado
     * @return User? - O usuário pode ser retornado ou um erro
     */
    fun userIsPresent(username: String): User? {
        return userRepository.findByUsername(username).orElseThrow {
            UserNotFound("Usuário não encontrado", username, HttpStatus.NOT_FOUND)
        };

        // TODO: Implementar nesse serviço a geração do token e retornar para o usuário um erro ou o token
    }

    /**
     * Função para salvar usuários dentro do banco de dados
     * @param userRequest: UserRequestDto - Dto para construir um objeto com os dados do usuário
     * @return UserResponseDto - Dto de resposta para o usuário, com uma mensagem e o nome de usuário
     */
    fun saveUser(userRequest: UserRequestDto): UserResponseDto {
        val user: User = User(
            username = userRequest.username,
            password = passwordEncoder.encode(userRequest.password),
            role = userRequest.role
        )

        if (user.role.equals(Role.entries.toTypedArray())) {
            throw IncorrectUserRole(
                "A role inserida é invalida para a criação de usuários, as disponíveis são: ${Role.entries.toTypedArray()}",
                user.role.toString(),
                HttpStatus.BAD_REQUEST
            )
        }

        if (userRepository.findByUsername(user.username).isPresent) {
            throw UserAlreadyExists(
                "O usuário inserido já existe!",
                HttpStatus.BAD_REQUEST
            )
        }

        val userSaved: User = userRepository.save(user)
        return UserResponseDto("Usuário salvo com sucesso!", userSaved.username)
        // TODO: Trocar retorno de sucesso para retornar o token
    }
}