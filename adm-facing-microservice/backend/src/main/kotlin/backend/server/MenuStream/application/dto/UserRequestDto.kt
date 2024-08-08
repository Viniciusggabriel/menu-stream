package backend.server.MenuStream.application.dto

import backend.server.MenuStream.infra.validation.annotation.UsernameValid
import backend.server.MenuStream.model.entity.user.Role

data class UserRequestDto(
    @UsernameValid(message = "O nome de usuário não pode conter caracteres especiais somente ponto '.'") val username: String,
    val password: String,
    val role: Role
)
