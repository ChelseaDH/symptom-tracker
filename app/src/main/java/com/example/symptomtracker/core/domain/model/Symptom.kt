package com.example.symptomtracker.core.domain.model

data class Symptom(
    val id: Long,
    val name: String
) {
    constructor(name: String) : this(id = 0, name = name)
}
