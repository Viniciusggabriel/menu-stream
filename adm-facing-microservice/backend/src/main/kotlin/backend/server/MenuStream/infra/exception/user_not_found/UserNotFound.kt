package backend.server.MenuStream.infra.exception.user_not_found

class UserNotFound(message: String, private val errorCode: Int? = null) : RuntimeException(message) {
    override fun toString(): String {
        return "ExNotFound(message='${super.message}', errorCode=$errorCode)"
    }
}