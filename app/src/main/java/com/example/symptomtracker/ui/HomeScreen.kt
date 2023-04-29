import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.symptomtracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddFoodClick: () -> Unit, onAddSymptomClick: () -> Unit, onAddMovementClick: () -> Unit,
) {
    Column {
        CenterAlignedTopAppBar(title = {
            Text(text = stringResource(R.string.home), style = MaterialTheme.typography.titleLarge)
        })
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp)
        ) {
            ExtendedFloatingActionButton(onClick = { onAddFoodClick() },
                text = { Text(text = stringResource(R.string.add_food_text)) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_food_cd)
                    )
                })
            ExtendedFloatingActionButton(onClick = { onAddSymptomClick() },
                text = { Text(text = stringResource(R.string.add_symptom_text)) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_symptom_cd)
                    )
                })
            ExtendedFloatingActionButton(onClick = { onAddMovementClick() },
                text = { Text(text = stringResource(R.string.add_movement_text)) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_movement_cd)
                    )
                })
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(onAddFoodClick = {}, onAddSymptomClick = {}, onAddMovementClick = {})
}