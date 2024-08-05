package backend.server.MenuStream.model.entity.user

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "TB_USER")
class User(
    @Id
    @Column(name = "ID_USER", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    val idUser: UUID,

    @Column(name = "DS_USERNAME", unique = true, nullable = false, length = 30)
    val username: String,

    @Column(name = "DS_PASSWORD", nullable = false, length = 150)
    val password: String,

    @Column(name = "DS_ROLE", nullable = false)
    val role: Role,
)