import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.symptomtracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMovementScreen(onBackClick: () -> Unit) {
    Column {
        CenterAlignedTopAppBar(title = {
            Text(
                text = stringResource(R.string.log_movement_title),
                style = MaterialTheme.typography.titleLarge
            )
        }, navigationIcon = {
            IconButton(onClick = { onBackClick() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back_button_cd)
                )
            }
        })
    }
}

@Composable
@Preview(showSystemUi = true)
fun AddMovementScreenPreview() {
    AddMovementScreen(onBackClick = {})
}