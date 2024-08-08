package backend.server.MenuStream.infra.validation.validator

import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimNames

class OAuth2TokenValidator : OAuth2TokenValidator<Jwt> {
    /**
     * Método para ter uma validação personalizada do JWT gerado pelo Oauth2
     * @return OAuth2TokenValidatorResult - Classe do OAuth2 que tem métodos para tratamento de erro
     */
    override fun validate(token: Jwt?): OAuth2TokenValidatorResult {
        if (token!!.getClaimAsString(JwtClaimNames.SUB) === null) {
            val error: OAuth2Error =
                OAuth2Error("${HttpStatus.UNAUTHORIZED}", "Erro ao buscar claims SUB do seu token!", "\\v1\\login");
            return OAuth2TokenValidatorResult.failure(error);
        }
        return OAuth2TokenValidatorResult.success();
    }
}