package backend.server.MenuStream.infra.config

import backend.server.MenuStream.infra.filter.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
class SecurityConfig {
    private val jwtAuthFilter: JwtAuthenticationFilter? = null
    private val authenticationProvider: AuthenticationProvider? = null

    /**
     * Método para configuração de segurança geral da aplicação
     * @return SecurityFilterChain - Retorna a configuração de segurança definida pela classe HttpSecurity
     */
    @Bean
    fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity.csrf { csrf -> csrf.disable() }
            .authorizeHttpRequests { request ->
                request
                    .requestMatchers("/v1/login").permitAll()
                    .requestMatchers("/v1/register").hasRole("ADM")
                    .anyRequest().authenticated()
            }
            .sessionManagement { session ->
                session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
            .formLogin { form -> form.disable() }

        return httpSecurity.build()
    }
}