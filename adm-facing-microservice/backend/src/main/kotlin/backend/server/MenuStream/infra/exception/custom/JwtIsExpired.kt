package backend.server.MenuStream.infra.exception.custom

import org.springframework.http.HttpStatus

class JwtIsExpired(
    message: String,
    private val errorCode: HttpStatus? = HttpStatus.BAD_REQUEST
) : RuntimeException(message) {
    override fun toString(): String {
        return "JwtIsExpired: (message='${super.message}', errorCode=$errorCode)"
    }
}