package com.example.symptomtracker.core.data.model

import com.example.symptomtracker.core.database.model.MovementLogEntity
import com.example.symptomtracker.core.domain.model.MovementLog
import com.example.symptomtracker.core.domain.model.StoolType
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.OffsetDateTime

class MovementLogKtTest {

    @Test
    fun movementLog_canBeMappedToEntity() {
        val movementLogModel = MovementLog(
            id = 1,
            date = OffsetDateTime.parse("2023-03-02T12:15:00+00:00"),
            stoolType = StoolType.NORMAL_3,
        )
        val expectedEntity = MovementLogEntity(
            id = 1,
            date = OffsetDateTime.parse("2023-03-02T12:15:00+00:00"),
            stoolType = StoolType.NORMAL_3,
        )

        assertEquals(expectedEntity, movementLogModel.asEntity())
    }
}
