package com.example.symptomtracker.core.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.symptomtracker.core.domain.model.MovementLog
import com.example.symptomtracker.core.domain.model.StoolType
import java.time.OffsetDateTime

class MovementLogPreviewParameterProvider : PreviewParameterProvider<MovementLog> {
    override val values = sequenceOf(
        MovementLog(
            id = 1,
            date = OffsetDateTime.parse("2023-03-02T09:10:00+00:00"),
            stoolType = StoolType.NORMAL_3,
        ),
    )
}
