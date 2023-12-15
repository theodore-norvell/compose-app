import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.BottomAppBar
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import kotlinx.coroutines.launch
import model.state.ButtonDescription
import model.state.CalculatorModel
import viewModel.UIState
import viewModel.CalculatorViewModel

import org.jetbrains.compose.resources.ExperimentalResourceApi

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
@OptIn(ExperimentalResourceApi::class)
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
                    var baseMenuShowing : Boolean by remember { mutableStateOf(false) }
                    var baseName : String by remember { mutableStateOf("10") }
                    Button( onClick = {baseMenuShowing = true} ) { Text( "10" )}
                    DropdownMenu(baseMenuShowing, onDismissRequest = {baseMenuShowing = false}, ) {
                        DropdownMenuItem( text = {Text("10")}, onClick = {baseMenuShowing = false})
                        DropdownMenuItem( text = {Text("16")}, onClick = {baseMenuShowing = false})
                        DropdownMenuItem( text = {Text("2")}, onClick = {baseMenuShowing = false})
                        DropdownMenuItem( text = {Text("8")}, onClick = {baseMenuShowing = false})
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.background(Color.hsl(0.17f, 0.17f, 0.31f)).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp )
        ) {
            // See https://medium.com/@gsaillen95/how-to-create-a-jump-to-top-feature-with-jetpack-compose-2ed487b30087
            val listState = rememberLazyListState()

            // See https://developer.android.com/jetpack/compose/side-effects
            LaunchedEffect(uiState.stackAndMemory) {
                if (uiState.stackAndMemory.isNotEmpty()) listState.scrollToItem(uiState.stackAndMemory.size - 1)
            }

            // Scrollable stuff
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.fillMaxWidth()
                    .fillMaxHeight(0.25f)
                    .padding(horizontal = 5.dp),
                state = listState
            ) { itemsIndexed(uiState.stackAndMemory) { _, item -> StackItemView(item) } }

            // Top
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 5.dp),
                content = {
                    StackItemView(uiState.top)
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
    if( uiState.errors.isNotEmpty() ) {
        scope.launch {
            scaffoldState.snackbarHostState.showSnackbar(
                message = uiState.errors[0],
                actionLabel = "(dismiss)",
                duration = SnackbarDuration.Short )
            viewModel.cancelError()
        }
    }
}

@Composable
private fun RowScope.mkButton(
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
            modifier = Modifier.weight(w),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.hsl(220.0f, 0.62f, 0.34f) ),
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
fun StackItemView(str: String) {
    Text(
        modifier = Modifier.fillMaxWidth().background(Color.hsl(146.0f, 0.11f, 0.65f)),
        text = str,
        color = Color.Black,
        fontSize = 20.sp,
        fontFamily = FontFamily.Monospace,
    )
}
expect fun getPlatformName(): String

