package backend.server.MenuStream.infra.config

import backend.server.MenuStream.infra.validation.validator.OAuth2TokenValidation
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import javax.crypto.SecretKey

@Configuration
class JwtConfig @Autowired constructor(
    private var oAuth2TokenValidator: OAuth2TokenValidation
) {
    @Value("\${message.secret.key.jwt.decode}")
    private lateinit var keyBytes: String;

    /**
     * Bean para gerenciar o Decoder do JWT
     * @return JwtDecoder - Configura o decoder para o JWT baseado em uma palavra inserida no application.properties
     */
    @Bean
    fun jwtDecoder(): JwtDecoder {
        val key: SecretKey = Keys.hmacShaKeyFor(keyBytes.toByteArray())
        val jwtDecoder = NimbusJwtDecoder.withSecretKey(key).build()
        jwtDecoder.setJwtValidator(oAuth2TokenValidator)
        return jwtDecoder
    }

    /**
     * Método privado para converter o Jwt e pegar as autoridades do token, será baseado nas roles do usuário
     * @return JwtAuthenticationConverter - Retorna o JWT com as credenciais definidas para o usuário
     */
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val grantedAuthorizationConverter: JwtGrantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()
        grantedAuthorizationConverter.setAuthoritiesClaimName("role")
        grantedAuthorizationConverter.setAuthorityPrefix("ROLE_")

        val jwtAuthenticationConverter: JwtAuthenticationConverter = JwtAuthenticationConverter()
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthorizationConverter)

        return jwtAuthenticationConverter;
    }
}