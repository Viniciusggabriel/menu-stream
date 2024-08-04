package backend.server.MenuStream.model.entity.user

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "tb_user", schema = "public")
class User(
    @Id
    @Column(name = "id_user", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    val idUser: UUID,

    @Column(name = "ds_username", unique = true, nullable = false, length = 30)
    val username: String,

    @Column(name = "ds_password", nullable = false, length = 150)
    val password: String,

    @Column(name = "ds_role", nullable = false)
    val role: Role,
)