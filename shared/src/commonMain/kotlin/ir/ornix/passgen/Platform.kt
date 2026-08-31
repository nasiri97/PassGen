package ir.ornix.passgen

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform