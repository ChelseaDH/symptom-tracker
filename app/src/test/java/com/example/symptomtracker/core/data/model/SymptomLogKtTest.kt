package com.example.symptomtracker.core.data.model

import com.example.symptomtracker.core.database.model.PopulatedSymptomLog
import com.example.symptomtracker.core.database.model.SymptomEntity
import com.example.symptomtracker.core.database.model.SymptomLogEntity
import com.example.symptomtracker.core.database.model.SymptomLogRecordWithSymptom
import com.example.symptomtracker.core.database.model.SymptomLogSymptomCrossRef
import com.example.symptomtracker.core.model.Severity
import com.example.symptomtracker.core.model.Symptom
import com.example.symptomtracker.core.model.SymptomLog
import com.example.symptomtracker.core.model.SymptomWithSeverity
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.OffsetDateTime

class SymptomLogKtTest {

    @Test
    fun symptomLog_canBeMappedToEntity() {
        val symptomLogModel = SymptomLog(
            id = 1,
            date = OffsetDateTime.parse("2023-03-02T12:15:00+00:00"),
            items = listOf(
                SymptomWithSeverity(
                    symptom = Symptom(id = 2, name = "bloating"),
                    severity = Severity.SEVERE,
                )
            )
        )
        val expectedEntity = PopulatedSymptomLog(
            symptomLogEntity = SymptomLogEntity(
                id = 1,
                date = OffsetDateTime.parse("2023-03-02T12:15:00+00:00")
            ),
            symptomLogRecords = listOf(
                SymptomLogRecordWithSymptom(
                    symptomLogSymptomCrossRef = SymptomLogSymptomCrossRef(
                        symptomLogId = 1,
                        symptomId = 2,
                        severity = Severity.SEVERE,
                    ),
                    symptomEntity = SymptomEntity(id = 2, name = "bloating")
                )
            )
        )

        assertEquals(expectedEntity, symptomLogModel.asEntity())
    }

    @Test
    fun symptomLog_canBeMappedToSymptomLogEntity() {
        val symptomLogModel = SymptomLog(
            id = 1,
            date = OffsetDateTime.parse("2023-03-02T12:15:00+00:00"),
            items = listOf(
                SymptomWithSeverity(
                    symptom = Symptom(id = 2, name = "bloating"),
                    severity = Severity.SEVERE,
                )
            )
        )
        val expectedEntity =
            SymptomLogEntity(id = 1, date = OffsetDateTime.parse("2023-03-02T12:15:00+00:00"))

        assertEquals(expectedEntity, symptomLogModel.asSymptomLogEntity())
    }
}
