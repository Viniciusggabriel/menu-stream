package backend.server.MenuStream.application.dto

import org.springframework.http.HttpStatus

data class UserResponseDto(val token: String, private val errorCode: HttpStatus? = HttpStatus.BAD_REQUEST)
