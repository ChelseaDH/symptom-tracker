package com.example.symptomtracker.core.data.model

import com.example.symptomtracker.core.database.model.SymptomEntity
import com.example.symptomtracker.core.model.Symptom
import org.junit.Assert.assertEquals
import org.junit.Test

class SymptomKtTest {
    @Test
    fun symptom_canBeMappedToEntity() {
        val symptomModel = Symptom(id = 1, name = "bloating")
        val expectedEntity = SymptomEntity(id = 1, name = "bloating")

        assertEquals(expectedEntity, symptomModel.asEntity())
    }
}
