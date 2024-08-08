package backend.server.MenuStream.model.entity.user

import backend.server.MenuStream.infra.validation.annotation.UsernameValid
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

@Entity
@Table(name = "TB_USER")
class User(
    /**
     * Dados para criação da tabela dentro do banco de dados
     */
    @Id
    @Column(name = "ID_USER", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    val idUser: UUID = UUID.randomUUID(),

    @Column(name = "DS_USERNAME", unique = true, nullable = false, length = 30)
    @UsernameValid(message = "O nome de usuário não pode conter caracteres especiais somente ponto '.'")
    private val username: String = "",

    @Column(name = "DS_PASSWORD", nullable = false, length = 150)
    private val password: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "DS_ROLE", nullable = false)
    val role: Role = Role.EMPLOYEE
) : UserDetails {
    /**
     * Userdetails para realizar a parte de autenticação
     */

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_${role.name}"));
    }

    override fun getPassword(): String {
        return password;
    }

    override fun getUsername(): String {
        return username
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true;
    }

    override fun isEnabled(): Boolean {
        return true;
    }
}
