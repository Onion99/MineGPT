package org.onion.gpt

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform