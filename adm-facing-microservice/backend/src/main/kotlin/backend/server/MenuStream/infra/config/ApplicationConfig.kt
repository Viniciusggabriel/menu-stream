package backend.server.MenuStream.infra.config

import backend.server.MenuStream.model.repository.user.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder


/**
 * Essa classe já é testada no UserService e na hora de testar a criação do token para o usuário
 */
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

    @Bean
    fun authenticationProvider(): AuthenticationProvider {
        val daoAuthenticationProvider = DaoAuthenticationProvider()

        daoAuthenticationProvider.setUserDetailsService(userDetailsService())
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder())

        return daoAuthenticationProvider
    }

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
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