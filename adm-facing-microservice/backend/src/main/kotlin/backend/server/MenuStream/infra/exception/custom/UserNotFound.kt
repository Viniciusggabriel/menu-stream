package backend.server.MenuStream.infra.exception.custom

import org.springframework.http.HttpStatus

class UserNotFound(
    message: String,
    val username: String,
    private val errorCode: HttpStatus? = HttpStatus.BAD_REQUEST
) : RuntimeException(message) {
    override fun toString(): String {
        return "UserNotFound: (message='${super.message}', username='${username}', errorCode=$errorCode)"
    }
}