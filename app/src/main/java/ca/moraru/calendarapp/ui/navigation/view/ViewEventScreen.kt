package ca.moraru.calendarapp.ui.navigation.view

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.moraru.calendarapp.ui.ViewModelProvider
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.moraru.calendarapp.R
import ca.moraru.calendarapp.data.doubleToHourString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewEventScreen(
    currentEventId: Int,
    viewModel: ViewEventViewModel,
    backNavigation: () -> Unit,
    changeEditView: () -> Unit,
) {
    val context = LocalContext
    viewModel.updateMenuUiState(currentEventId)
    val uiState by viewModel.eventViewUiState.collectAsState()

    Scaffold(
        topBar = {
             TopAppBar(
                 title = {
                     Text(
                         text = context.current.resources.getString(R.string.view_event),
                         fontWeight = FontWeight.Bold,
                         textAlign = TextAlign.Center,
                         modifier = Modifier.fillMaxWidth(),
                         fontSize = 25.sp,
                         color = Color.White
                     )
                 },
                 colors = TopAppBarDefaults.largeTopAppBarColors(
                     containerColor = Color(0.22f, 0.255f, 0.616f, 1.0f),
                 ),
             )
        },
        bottomBar = {
            BottomAppBar {
                Buttons(
                    uiState = uiState,
                    backNavigation = backNavigation,
                    changeEditView = changeEditView,
                    viewModel = viewModel,
                    context = context,
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        InputFields(
            uiState = uiState,
            innerPadding = innerPadding,
            context = context
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun InputFields(
    uiState: ViewEventUiState,
    innerPadding: PaddingValues,
    context: ProvidableCompositionLocal<Context>
) {
    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            val title = context.current.resources.getString(R.string.title_field)
            val description = context.current.resources.getString(R.string.description_field)
            val location = context.current.resources.getString(R.string.location_field)
            val date = context.current.resources.getString(R.string.date_field)
            val start = context.current.resources.getString(R.string.starttime_field)
            val end = context.current.resources.getString(R.string.endtime_field)

            OutlinedTextField(
                value = uiState.currentEvent.title,
                onValueChange = {},
                label = {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    )
                },
                readOnly = true,
                singleLine = true,
                modifier = Modifier
                    .width(350.dp)
                    .padding(15.dp)
            )
            OutlinedTextField(
                value = uiState.currentEvent.description,
                onValueChange = {},
                label = {
                    Text(
                        text = description,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    )
                },
                readOnly = true,
                singleLine = true,
                modifier = Modifier
                    .width(350.dp)
                    .padding(15.dp)
            )
            OutlinedTextField(
                value = uiState.currentEvent.location,
                onValueChange = {},
                label = {
                    Text(
                        text = location,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    )
                },
                readOnly = true,
                singleLine = true,
                modifier = Modifier
                    .width(350.dp)
                    .padding(15.dp)
            )
            OutlinedTextField(
                value = uiState.currentEvent.year.toString() + "-" + (uiState.currentEvent.month + 1).toString() + "-" + uiState.currentEvent.day.toString(),
                onValueChange = {},
                label = {
                    Text(
                        text = date,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    )
                },
                readOnly = true,
                singleLine = true,
                modifier = Modifier
                    .width(350.dp)
                    .padding(15.dp)
            )
            OutlinedTextField(
                value = doubleToHourString(uiState.currentEvent.startTime),
                onValueChange = {},
                label = {
                    Text(
                        text = start,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    )
                },
                readOnly = true,
                singleLine = true,
                modifier = Modifier
                    .width(350.dp)
                    .padding(15.dp)
            )
            OutlinedTextField(
                value = doubleToHourString(uiState.currentEvent.endTime),
                onValueChange = {},
                label = {
                    Text(
                        text = end,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    )
                },
                readOnly = true,
                singleLine = true,
                modifier = Modifier
                    .width(350.dp)
                    .padding(15.dp)
            )
        }
    }
}

@Composable
fun Buttons(
    uiState: ViewEventUiState,
    backNavigation: () -> Unit,
    changeEditView: () -> Unit,
    viewModel: ViewEventViewModel,
    context: ProvidableCompositionLocal<Context>,
) {
    var dialog by remember { mutableStateOf(false) }

    when {
        dialog -> {
            ConfirmationDialog(
                uiState = uiState,
                backNavigation = backNavigation,
                viewModel = viewModel,
                context = context,
                closeDialog = { dialog = false }
            )
        }
    }

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Button(
            onClick = { backNavigation() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0.216f, 0.447f, 0.847f, 1.0f)
            )
        ) {
            Text(context.current.resources.getString(R.string.back))
        }
        Button(
            onClick = { changeEditView() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0.216f, 0.447f, 0.847f, 1.0f)
            )
        ) {
            Text(context.current.resources.getString(R.string.edit))
        }
        Button(
            onClick = { dialog = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0.216f, 0.447f, 0.847f, 1.0f)
            )
        ) {
            Text(context.current.resources.getString(R.string.delete))
        }
    }
}

@Composable
fun ConfirmationDialog(
    uiState: ViewEventUiState,
    backNavigation: () -> Unit,
    viewModel: ViewEventViewModel,
    closeDialog: () -> Unit,
    context: ProvidableCompositionLocal<Context>,
) {
    val toastMessage = context.current.resources.getString(R.string.event_deleted)
    val currentContext = context.current

    AlertDialog(
        onDismissRequest = {},
        icon = { Icon(Icons.Default.Info, contentDescription = context.current.resources.getString(R.string.info_description)) },
        title = {
            Text(context.current.resources.getString(R.string.confirmation_title))
        },
        text = {
            Text(
                text = context.current.resources.getString(R.string.confirmation_message),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.deleteEvent(uiState.currentEvent)
                    backNavigation()
                    Toast.makeText(currentContext, toastMessage, Toast.LENGTH_SHORT).show()
                }
            ) {
                Text(
                    text = context.current.resources.getString(R.string.confirm),
                    modifier = Modifier.padding(horizontal = 40.dp)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = { closeDialog() }
            ) {
                Text(
                    text = context.current.resources.getString(R.string.cancel),
                )
            }
        }
    )
}