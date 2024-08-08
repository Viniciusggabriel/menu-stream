package backend.server.MenuStream.infra.exception.custom

import org.springframework.http.HttpStatus

class IncorrectUserRole(
    message: String,
    val role: String,
    private val errorCode: HttpStatus? = HttpStatus.BAD_REQUEST
) : RuntimeException(message) {
    override fun toString(): String {
        return "IncorrectCreationData: (message='${super.message}', role='${role}', errorCode=$errorCode)"
    }
}