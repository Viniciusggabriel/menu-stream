package backend.server.MenuStream.infra.validation.validator

import backend.server.MenuStream.infra.validation.annotation.UsernameValid
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class UsernameValidator : ConstraintValidator<UsernameValid, String> {
    private val regex = "^[\\w.]*$".toRegex()

    override fun isValid(string: String?, context: ConstraintValidatorContext?): Boolean {
        if (string == null || !regex.matches(string)) {
            context!!.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Username é invalido, contem caracteres especiais ou acentos!")
                .addConstraintViolation()
            return false;
        }

        return true
    }
}