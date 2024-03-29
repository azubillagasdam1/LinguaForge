package com.example.linguaforge.models.entity

import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId

class User(
    var email: String? = null,
    var name: String? = null,
    var correo: String? = null,
    var fechaRegistro: LocalDateTime? = null
)