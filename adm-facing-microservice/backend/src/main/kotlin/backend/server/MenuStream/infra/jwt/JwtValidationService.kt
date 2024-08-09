package backend.server.MenuStream.infra.jwt


import backend.server.MenuStream.infra.exception.custom.JwtIsExpired
import com.nimbusds.jose.JWSVerifier
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtValidationService {
    private val jwtGeneratorService: JwtGeneratorService? = null

    /**
     * @param token       verifica se o token é valido testando o nome de usuário dentro do token e a validade
     * @param userDetails pega os detalhes do usuário para gerar um token baseado em suas roles e nome de usuário
     * @return boolean retorna se o usuário é valido ou não
     */
    fun isValidToken(token: String?, userDetails: UserDetails): Boolean {
        val userName = extractUserName(token)
        return (userName == userDetails.username) && !isTokenExpired(token)
    }

    private fun isTokenExpired(token: String?): Boolean {
        return extractExpiration(token).before(Date())
    }

    private fun extractExpiration(token: String?): Date {
        return (extractClaim(token) { obj: Map<String, Any?> -> obj["exp"] } as Long?)?.let { Date(it * 1000) }
            ?: throw Exception("Claim de expiração não encontrada!")
    }

    //INFO: Extração de valores do token
    /**
     * @param token           Extrai todos os dados do token
     * @param claimsTFunction função com claims e outro valor genérico
     * @return <T> Retorna um valor genérico
    </T> */
    fun <T> extractClaim(
        token: String?,
        claimsTFunction: (Map<String, Any?>) -> T
    ): T {
        val claims: Map<String, Any?>

        try {
            claims = extractAllClaims(token)
        } catch (exception: ExpiredJwtException) {
            throw JwtIsExpired("O token fornecido está expirado!")
        }

        return claimsTFunction(claims)
    }

    private fun extractAllClaims(token: String?): Map<String, Any?> {
        val signedJWT = SignedJWT.parse(token)
        val verifier: JWSVerifier = MACVerifier(jwtGeneratorService!!.secretKeySpec)

        if (!signedJWT.verify(verifier)) {
            throw Exception("Invalid token signature")
        }

        val claimsSet: JWTClaimsSet = signedJWT.payload as JWTClaimsSet
        val claims: Map<String, Any?> = claimsSet.claims
        return claims
    }

    /**
     * @param token Extrai o nome de usuário do token e retorna em formato de string
     * @return String
     */
    fun extractUserName(token: String?): String {
        return extractClaim(token) { obj: Map<String, Any?> -> obj["sub"] } as? String
            ?: throw Exception("Subject claim not found")
    }
}