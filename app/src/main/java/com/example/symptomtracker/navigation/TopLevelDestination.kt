package com.example.symptomtracker.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.symptomtracker.R

enum class TopLevelDestination(
    @DrawableRes val iconId: Int,
    @StringRes val iconTextId: Int,
    @StringRes val titleTextId: Int,
) {
    HOME(
        iconId = R.drawable.outline_home_24,
        iconTextId = R.string.home,
        titleTextId = R.string.app_name,
    ),
    LOGS(
        iconId = R.drawable.outline_list_24,
        iconTextId = R.string.feature_logs_title,
        titleTextId = R.string.feature_logs_title,
    ),
}
