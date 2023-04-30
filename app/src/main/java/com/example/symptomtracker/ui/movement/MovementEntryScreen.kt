import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.symptomtracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMovementScreen(navigateBack: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            SymptomTrackerTopAppBar(
                title = stringResource(R.string.log_movement_title),
                canNavigateBack = true,
                navigateUp = navigateBack,
            )
        },
        modifier = modifier
    ) { innerPadding ->
        MovementEntryBody(
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun MovementEntryBody(modifier: Modifier = Modifier) {
}

@Composable
@Preview(showSystemUi = true)
fun AddMovementScreenPreview() {
    AddMovementScreen(navigateBack = {})
}