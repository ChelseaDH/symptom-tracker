package com.example.symptomtracker.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.FloatingActionButtonMenuScope
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.symptomtracker.R

@ExperimentalMaterial3ExpressiveApi
@Composable
fun FloatingButtonMenu(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes buttonResId: Int = R.drawable.outline_add_24,
    content: @Composable (FloatingActionButtonMenuScope.() -> Unit),
) {
    FloatingActionButtonMenu(
        expanded = expanded,
        button = {
            ToggleFloatingActionButton(
                checked = expanded,
                onCheckedChange = onExpandedChange,
            ) {
                val iconResource by remember {
                    derivedStateOf {
                        if (checkedProgress > 0.5f) R.drawable.outline_close_24 else buttonResId
                    }
                }
                Icon(
                    painter = painterResource(id = iconResource),
                    contentDescription = null,
                    modifier = Modifier.animateIcon({ checkedProgress })
                )
            }
        },
        modifier = modifier.offset(x = 16.dp, y = 16.dp),
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true)
@Composable
fun FloatingButtonMenuNotExpandedPreview() {
    FloatingButtonMenu(
        expanded = false,
        onExpandedChange = {},
    ) {
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true)
@Composable
fun FloatingButtonMenuExpandedPreview() {
    FloatingButtonMenu(
        expanded = true,
        onExpandedChange = {},
    ) {
        FloatingActionButtonMenuItem(
            onClick = {},
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.outline_nutrition_24),
                    contentDescription = null
                )
            },
            text = { Text(text = "Food") },
        )
    }
}
