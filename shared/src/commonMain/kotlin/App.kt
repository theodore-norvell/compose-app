import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import kotlinx.coroutines.launch
import model.state.ButtonDescription
import model.state.CalculatorModel
import viewModel.UIState
import viewModel.CalculatorViewModel


@Composable
fun ImageAppTheme(
    content : @Composable () -> Unit
) {
    MaterialTheme(
        MaterialTheme.colors.copy( primary = Color.Black ), shapes = MaterialTheme.shapes.copy(
            small = AbsoluteCutCornerShape(0.dp),
            medium = AbsoluteCutCornerShape(0.dp),
            large = AbsoluteCutCornerShape(0.dp)
        )
        ) {
        content()
    }
}
@Composable
fun App(calculatorModel : CalculatorModel) {

    ImageAppTheme {
        val viewModel : CalculatorViewModel = getViewModel( Unit, viewModelFactory { CalculatorViewModel(calculatorModel) } )
        mainPage( viewModel )
    }
}

@Composable
private fun mainPage(viewModel : CalculatorViewModel) {
    val uiState : UIState = viewModel.uiState.collectAsState().value
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text("UA")
                },
                actions =  {
                    // Base Picker.
                    DropDownPicker( uiState.base,
                        { viewModel.setBase( it ) },
                        listOf( PickerOption( "2", 2),
                            PickerOption( "8", 8),
                            PickerOption( "10", 10),
                            PickerOption( "12", 12),
                            PickerOption( "16", 16))
                        )
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.background(Color.DarkGray).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // See https://medium.com/@gsaillen95/how-to-create-a-jump-to-top-feature-with-jetpack-compose-2ed487b30087
            val listState = rememberLazyListState()

            // See https://developer.android.com/jetpack/compose/side-effects
            LaunchedEffect(uiState.stackStrings) {
                if (uiState.stackStrings.isNotEmpty()) listState.scrollToItem(uiState.stackStrings.size - 1)
            }

            // Scrollable stuff
            val clipboardManager: ClipboardManager = LocalClipboardManager.current
            val capture = {str : String -> clipboardManager.setText( AnnotatedString(str))}
            val pushVar = {name : String -> viewModel.makeVarRef(name)}
            LazyColumn(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.fillMaxWidth()
                    .fillMaxHeight(0.25f)
                    .padding(horizontal = 5.dp),
                state = listState
            ) {
                itemsIndexed(uiState.envPairs) { _, item -> MemoryItemView(item, pushVar, capture) }
                itemsIndexed(uiState.stackStrings) { _, item -> StackItemView(item, capture) }
            }

            // Top
            Column(
                verticalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 5.dp),
                content = {
                    StackItemView(uiState.top, capture)
                }
            )

            // Buttons
            Column( // Of Button Rows
                verticalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp)
            )
            {
                uiState.buttons.forEach { rowOfStrings ->
                    Row( // of Buttons
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp).weight(1.0f)
                    )
                    {
                        rowOfStrings.forEach { desc ->
                            mkButton(viewModel, desc)
                        }
                    }
                }
            }
        }
    }
    val scope = rememberCoroutineScope()
    LaunchedEffect(uiState.error) {
        scope.launch {
            if( uiState.error != null ) {
                scaffoldState.snackbarHostState.showSnackbar(
                    message = uiState.error,
                    actionLabel = "(dismiss)",
                    duration = SnackbarDuration.Short
                )
                viewModel.cancelError()
            }
        }
    }
}

data class PickerOption<T>(
    val name : String,
    val value : T
)
@Composable
fun <T>DropDownPicker(currentPickName : String,
                      action : (T) ->Unit,
                      options : List<PickerOption<T>>) {
    var menuShowing : Boolean by remember { mutableStateOf(false) }
    Button( onClick = {menuShowing = true} ) { Text( currentPickName )}
    DropdownMenu(menuShowing, onDismissRequest = {menuShowing = false} ) {
        options.forEach{
            DropdownMenuItem(
                text = {Text( it.name ) },
                onClick = { action(it.value) ; menuShowing = false } )}
    }
}

@Composable
private fun mkButton(
    viewModel: CalculatorViewModel,
    desc : ButtonDescription
) {

    val w = 1.0f // Set to 2 for double wide.  Almost looks right.
    // TODO deal with shifts
    val primaryLabelStr = desc.primaryOperation.name
    val primaryColor = Color.White
    val secondaryLabelStr = desc.secondaryOperation.name
    val secondaryColor = Color.Yellow
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = secondaryLabelStr,
            color = secondaryColor,
            modifier = Modifier.background(Color.Transparent)
        )
        Button(
            modifier = Modifier.weight(w).shadow(20.dp, spotColor = Color.Black),
            shape = AbsoluteCutCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.hsl(220.0f, 0.50f, 0.34f) ),
            onClick = { viewModel.click(desc) }
        ) {
            Text(
                text = primaryLabelStr,
                color = primaryColor,
                modifier = Modifier.background(Color.Transparent)
            )
        }

    }
}

@Composable
fun StackItemView(str: String, valueAction : (String) -> Unit ) {
    Text(
        modifier = Modifier.fillMaxWidth()
            .clickable {valueAction( str )}
            .padding(2.dp)
            .background(Color.hsl(146.0f, 0.11f, 0.65f)),
        text = str,
        color = Color.Black,
        fontSize = 20.sp,
        fontFamily = FontFamily.Monospace,
    )
}

@Composable
fun MemoryItemView(pair : Pair<String, String>, nameAction : (String) -> Unit, valueAction : (String) -> Unit ) {
    Row {
        Text(
            modifier = Modifier.padding(2.dp)
                .clickable {nameAction( pair.first )}
                .background(Color.hsl(146.0f, 0.11f, 0.65f)),
            text = "${pair.first}: ",
            color = Color.Black,
            fontSize = 20.sp,
            fontFamily = FontFamily.Monospace,
        )
        Spacer( Modifier.width(5.dp))
        Text(
            modifier = Modifier.fillMaxWidth()
                .clickable {valueAction( pair.second )}
                .padding(2.dp)
                .background(Color.hsl(146.0f, 0.11f, 0.65f)),
            text = pair.second,
            color = Color.Black,
            fontSize = 20.sp,
            fontFamily = FontFamily.Monospace,
        )

    }
}
expect fun getPlatformName(): String

