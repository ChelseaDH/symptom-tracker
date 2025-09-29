package com.example.symptomtracker.core.designsystem.icon

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.symptomtracker.R

@Composable
fun EditIcon(contentDescription: String?, modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(id = R.drawable.outline_edit_24),
        contentDescription = contentDescription,
        modifier = modifier,
    )
}

@Composable
fun DeleteIcon(contentDescription: String?, modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(id = R.drawable.outline_delete_24),
        contentDescription = contentDescription,
        modifier = modifier,
    )
}

@Composable
fun AddIcon(contentDescription: String?, modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(id = R.drawable.outline_add_24),
        contentDescription = contentDescription,
        modifier = modifier,
    )
}

@Composable
fun ClearIcon(contentDescription: String?, modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(id = R.drawable.outline_close_24),
        contentDescription = contentDescription,
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
internal fun EditIconPreview() {
    EditIcon(contentDescription = null)
}

@Preview(showBackground = true)
@Composable
internal fun DeleteIconPreview() {
    DeleteIcon(contentDescription = null)
}

@Preview(showBackground = true)
@Composable
internal fun AddIconPreview() {
    AddIcon(contentDescription = null)
}

@Preview(showBackground = true)
@Composable
internal fun ClearIconPreview() {
    ClearIcon(contentDescription = null)
}