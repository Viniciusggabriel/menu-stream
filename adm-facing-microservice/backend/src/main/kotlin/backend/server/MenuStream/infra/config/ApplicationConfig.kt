package backend.server.MenuStream.infra.config

import backend.server.MenuStream.model.repository.user.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class ApplicationConfig {
    private lateinit var userRepository: UserRepository;

    /**
     * Bean para gerenciar a forma de buscar usuário dentro do escopo do spring boot
     * @return UserDetailsService Retorna o usuário ou um erro utilizando a classe throw da propria biblioteca
     */
    @Bean
    fun userDetailsService(): UserDetailsService {
        return UserDetailsService { username ->
            userRepository.findByUsername(username)
                .orElseThrow { UsernameNotFoundException("Não foi possível encontrar o usuário solicitado!") }
        }
    }

    /**
     * Define qual a forma de criptografia será usado para as senhas do banco de dados
     * @return PasswordEncoder Classe de criptografias do spring boot
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder();
    }
}