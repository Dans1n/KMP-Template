package com.bystrov.sto

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform