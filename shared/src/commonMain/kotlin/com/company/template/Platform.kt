package com.company.template

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform