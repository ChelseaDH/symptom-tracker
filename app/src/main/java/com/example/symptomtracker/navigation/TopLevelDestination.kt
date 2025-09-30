package com.example.symptomtracker.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.symptomtracker.R

enum class TopLevelDestination(
    @param:DrawableRes val iconId: Int,
    @param:DrawableRes val activeIconId: Int,
    @param:StringRes val iconTextId: Int,
    @param:StringRes val titleTextId: Int,
) {
    HOME(
        iconId = R.drawable.outline_home_24,
        activeIconId = R.drawable.baseline_home_filled_24,
        iconTextId = R.string.home,
        titleTextId = R.string.app_name,
    ),
    LOGS(
        iconId = R.drawable.outline_list_24,
        activeIconId = R.drawable.semi_bold_list_24,
        iconTextId = R.string.feature_logs_title,
        titleTextId = R.string.feature_logs_title,
    ),
    SETTINGS(
        iconId = R.drawable.outline_settings_24,
        activeIconId = R.drawable.baseline_settings_24,
        iconTextId = R.string.feature_settings_title,
        titleTextId = R.string.feature_settings_title,
    )
}
