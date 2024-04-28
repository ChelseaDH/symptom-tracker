package com.example.symptomtracker.core.model

enum class StoolType(val type: Int) {
    SEVERE_CONSTIPATION(1),
    MILD_CONSTIPATION(2),
    NORMAL_3(3),
    NORMAL_4(4),
    LACKING_FIBER(5),
    MILD_DIARRHEA(6),
    SEVERE_DIARRHEA(7),
}

fun StoolType.getDisplayName(): String {
    return when (this) {
        StoolType.SEVERE_CONSTIPATION -> "Severe constipation"
        StoolType.MILD_CONSTIPATION -> "Mild constipation"
        StoolType.NORMAL_3 -> "Normal"
        StoolType.NORMAL_4 -> "Normal"
        StoolType.LACKING_FIBER -> "Lacking fiber"
        StoolType.MILD_DIARRHEA -> "Mild diarrhea"
        StoolType.SEVERE_DIARRHEA -> "Severe diarrhea"
    }
}

fun StoolType.getDescription(): String {
    return when (this) {
        StoolType.SEVERE_CONSTIPATION -> "Separate hard lumps"
        StoolType.MILD_CONSTIPATION -> "Sausage-shaped, but lumpy"
        StoolType.NORMAL_3 -> "Like a sausage but with cracks on its surface"
        StoolType.NORMAL_4 -> "Like a sausage or snake, smooth and soft (average stool)"
        StoolType.LACKING_FIBER -> "Soft blobs with clear cut edges"
        StoolType.MILD_DIARRHEA -> "Fluffy pieces with ragged edges, a mushy stool"
        StoolType.SEVERE_DIARRHEA -> "Watery, no solid pieces, entirely liquid"
    }
}
