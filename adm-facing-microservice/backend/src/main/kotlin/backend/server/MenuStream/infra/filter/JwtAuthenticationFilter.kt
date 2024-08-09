package backend.server.MenuStream.infra.filter

import backend.server.MenuStream.infra.exception.custom.JwtIsExpired
import backend.server.MenuStream.infra.jwt.JwtValidationService
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.constraints.NotNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.nio.charset.StandardCharsets


@Component
class JwtAuthenticationFilter : OncePerRequestFilter() {
    private val jwtValidationService: JwtValidationService? = null
    private val userDetailsService: UserDetailsService? = null


    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        @NotNull request: HttpServletRequest,
        @NotNull response: HttpServletResponse,
        @NotNull filterChain: FilterChain
    ) {
        /*
         *  Coloca a requisição no encode UTF_8
         */
        response.characterEncoding = StandardCharsets.UTF_8.displayName()
        val authHeader = request.getHeader("Authorization")

        /*
         * Verifica o header da requisição referente ao token
         */
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        /*
         * Retira o Bearer do token
         */
        val jwt = authHeader.substring(7)
        val userName: String

        /*
         * Tratamento de erros para caso o token seja invalido
         */
        try {
            userName = jwtValidationService!!.extractUserName(jwt)
        } catch (exception: JwtIsExpired) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write(exception.message.toString())
            return
        } catch (exception: Exception) {
            response.status = HttpServletResponse.SC_BAD_REQUEST
            response.writer.write("O token inserido é invalido!")
            return
        }

        /*
         * Valida se o token foi assinado pelo servidor e se o usuário é valido
         */
        if (userName.isEmpty() && SecurityContextHolder.getContext().authentication == null) {
            val userDetails = userDetailsService!!.loadUserByUsername(userName)

            if (jwtValidationService.isValidToken(jwt, userDetails)) {
                val authenticationToken = UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.authorities
                )

                authenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)

                SecurityContextHolder.getContext().authentication = authenticationToken
            }
        }
        filterChain.doFilter(request, response)
    }
}