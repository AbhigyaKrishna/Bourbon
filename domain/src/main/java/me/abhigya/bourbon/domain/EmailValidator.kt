package me.abhigya.bourbon.domain

private val EMAIL_REGEX: Regex = Regex("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")

class EmailValidator(
    private val email: String
) {

    fun validate(): Boolean {
        if (email.isEmpty()) {
            return false
        }

        return EMAIL_REGEX.matches(email)
    }

}

fun validateEmail(email: String): Boolean {
    return EmailValidator(email).validate()
}