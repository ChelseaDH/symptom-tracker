package com.example.symptomtracker.core.data.model

import com.example.symptomtracker.core.database.model.PopulatedSymptomLog
import com.example.symptomtracker.core.database.model.SymptomLogEntity
import com.example.symptomtracker.core.database.model.SymptomLogRecordWithSymptom
import com.example.symptomtracker.core.database.model.SymptomLogSymptomCrossRef
import com.example.symptomtracker.core.model.SymptomLog

fun SymptomLog.asEntity(): PopulatedSymptomLog = PopulatedSymptomLog(
    symptomLogEntity = asSymptomLogEntity(),
    symptomLogRecords = items.map {
        SymptomLogRecordWithSymptom(
            symptomLogSymptomCrossRef = SymptomLogSymptomCrossRef(
                symptomLogId = id,
                symptomId = it.symptom.id,
                severity = it.severity
            ),
            symptomEntity = it.symptom.asEntity()
        )
    }
)

fun SymptomLog.asSymptomLogEntity(): SymptomLogEntity = SymptomLogEntity(id = id, date = getDate())
