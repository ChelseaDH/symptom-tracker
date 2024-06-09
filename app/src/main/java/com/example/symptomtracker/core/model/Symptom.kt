package com.example.symptomtracker.core.model

/**
 * External data layer representation of a symptom.
 */
data class Symptom(
    val id: Long,
    val name: String
) {
    constructor(name: String) : this(id = 0, name = name)
}
