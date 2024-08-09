package backend.server.MenuStream.infra.jwt

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSSigner
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.MacAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.nio.charset.Charset
import java.security.Key
import java.util.*
import javax.crypto.spec.SecretKeySpec

@Service
class JwtGeneratorService {
    @Value("\${secret.key.signature.jjwt}")
    private val secretKey: String? = null

    private final var signatureAlgorithm: MacAlgorithm = Jwts.SIG.HS256
    var secretKeySpec: SecretKeySpec =
        SecretKeySpec(secretKey!!.toByteArray(Charset.defaultCharset()), signatureAlgorithm.toString())

    /**
     * Método responsável por gerar o token baseado no userDetails que foi definido na entidade user
     *
     * @param userDetails referente aos detalhes do usuário
     * @return retorna o método que definitivamente cria o token
     */
    fun generateToken(userDetails: UserDetails): String {
        return generateToken(HashMap(), userDetails)
    }

    /**
     * Método que criar o token e setar no token suas credenciais e data de expiração
     *
     * @param extractClaims local onde os dados do usuário será inserido
     * @param userDetails   referente aos detalhes do usuário
     * @return retorna o token do usuário
     */
    fun generateToken(
        extractClaims: Map<String?, Any?>?,
        userDetails: UserDetails
    ): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, 1)
        val nextYear: Date = calendar.time

        val claimSet = JWTClaimsSet.Builder()
            .subject(userDetails.username)
            .issuer("MenuStream")
            .expirationTime(nextYear)
            .claim("claims", extractClaims)
            .build()

        val signer: JWSSigner = MACSigner(secretKey)
        val signedJWT = SignedJWT(JWSHeader(JWSAlgorithm.HS256), claimSet)

        signedJWT.sign(signer)

        return signedJWT.serialize()
    }

    fun getSecretKey(): Key {
        return secretKeySpec
    }
}