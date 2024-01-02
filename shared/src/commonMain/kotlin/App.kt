import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
import androidx.compose.ui.unit.sp
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import kotlinx.coroutines.launch
import model.data.NumberDisplayMode
import model.layouts.ButtonDescription
import model.state.CalculatorModel
import model.state.EvalMode
import viewModel.UIState
import viewModel.CalculatorViewModel


@Composable
fun App(calculatorModel : CalculatorModel) {
    CalcAppTheme {
        val viewModel : CalculatorViewModel = getViewModel( Unit, viewModelFactory { CalculatorViewModel(calculatorModel) } )
        mainPage( viewModel )
    }
}

@Composable
private fun CalcAppTheme(
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
private fun mainPage(viewModel : CalculatorViewModel) {
    val uiState : UIState = viewModel.uiState.collectAsState().value

    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text("∀A")
                },
                actions =  {
                    // Eval mode picker
                    DropDownPicker( uiState.evalMode,
                        { viewModel.setEvalMode( it ) },
                        listOf( PickerOption( EvalMode.VALUE.toString(), EvalMode.VALUE),
                            PickerOption( EvalMode.FORMULA.toString(), EvalMode.FORMULA))
                    )
                    // Display mode Picker.
                    DropDownPicker( uiState.displayMode,
                        { viewModel.setDisplayMode( it ) },
                        listOf( PickerOption( NumberDisplayMode.Auto.toString(), NumberDisplayMode.Auto),
                            PickerOption( NumberDisplayMode.Engineering.toString(), NumberDisplayMode.Engineering),
                            PickerOption( NumberDisplayMode.Scientific.toString(), NumberDisplayMode.Scientific),
                            PickerOption( NumberDisplayMode.NoExponent.toString(), NumberDisplayMode.NoExponent))
                    )
                    // Base Picker.
                    DropDownPicker( uiState.base,
                        { viewModel.setBase( it ) },
                        listOf( PickerOption( "2", 2),
                            PickerOption( "7", 7),
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
                val capture = { str: String -> clipboardManager.setText(AnnotatedString(str)) }
                val pushVar = { name: String -> viewModel.makeVarRef(name) }
                LazyColumn(
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.fillMaxWidth()
                        .fillMaxHeight(0.25f)
                        .padding(horizontal = 5.dp),
                    state = listState
                ) {
                    itemsIndexed(uiState.envPairs) { _, item ->
                        MemoryItemView(
                            item,
                            pushVar,
                            capture
                        )
                    }
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
                ButtonPanel(uiState, viewModel)
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

@Composable
private fun ButtonPanel(uiState: UIState, viewModel: CalculatorViewModel) {
    BoxWithConstraints {
        // Buttons
        if (maxWidth < maxHeight)
            Column( // Of Button Rows
                verticalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp)
            )
            {
                uiState.buttons.forEach { rowOfStrings ->
                    Row( // of Buttons
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            .weight(1.0f)
                    )
                    {
                        rowOfStrings.forEach { desc ->
                            mkButton(viewModel, desc, uiState.shifted)
                        }
                    }
                }
            }
        else
            Row {
                val splitPoint = 4
                // First Column
                Column( // Of Button Rows
                    verticalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxHeight().fillMaxWidth(0.5f)
                        .padding(horizontal = 10.dp)
                )
                {
                    uiState.buttons.take(splitPoint).forEach { rowOfStrings ->
                        Row( // of Buttons
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                .weight(1.0f)
                        )
                        {
                            rowOfStrings.forEach { desc ->
                                mkButton(viewModel, desc, uiState.shifted)
                            }
                        }
                    }
                }
                Column( // Of Button Rows
                    verticalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp)
                )
                {
                    uiState.buttons.drop(4).forEach { rowOfStrings ->
                        Row( // of Buttons
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                .weight(1.0f)
                        )
                        {
                            rowOfStrings.forEach { desc ->
                                mkButton(viewModel, desc, uiState.shifted)
                            }
                        }
                    }
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
private fun RowScope.mkButton(
    viewModel: CalculatorViewModel,
    desc : ButtonDescription,
    shifted : Boolean
) {
    val primarySemantics = !shifted  || desc.secondaryOperation == null
    val disabled = if( primarySemantics ) ! desc.primaryOperation.enabled else ! desc.secondaryOperation!!.enabled

    fun onClick() {
        if( primarySemantics )  viewModel.click(desc.primaryOperation)
        else viewModel.click(desc.secondaryOperation!!)
    }
    val primaryLabelStr = desc.primaryOperation.name
    val primaryColor = if( primarySemantics) desc.enabledTextColor else desc.disabledTextColor
    val secondaryLabelStr = if(desc.secondaryOperation != null) desc.secondaryOperation.name else " "
    val secondaryColor =  if( !primarySemantics ) desc.enabledTextColor else desc.disabledTextColor
    val secondarySize =  if( primarySemantics) 0.4f else 0.6f

    val backgroundColor = if( disabled ) desc.disabledBGColor
                            else if( shifted ) desc.shiftedEnabledBGColor
                            else desc.enabledBGColor
    Button(
        modifier = Modifier
            .shadow(10.dp, spotColor = Color.Black)
            .padding(horizontal=4.dp, vertical = 0.dp)
            .fillMaxHeight()
            .fillMaxWidth().weight(desc.weight),
        contentPadding = PaddingValues(
            start = 10.dp,
            top = 0.dp,
            end = 10.dp,
            bottom = 0.dp,
        ),
        shape = AbsoluteCutCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(  backgroundColor = backgroundColor),
        onClick = {onClick()}
    ) {
        Column(
            modifier = Modifier.padding(0.dp)//.border(1.dp, Color.Yellow)
                .fillMaxHeight()
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            AutoResizedText(
                text = secondaryLabelStr,
                color = secondaryColor,
                modifier = Modifier.background(Color.Transparent)//.border(3.dp, Color.Red)
                    .fillMaxHeight(secondarySize)
                    .fillMaxWidth()
            )
            AutoResizedText(
                text = primaryLabelStr,
                color = primaryColor,
                modifier = Modifier.background(Color.Transparent)//.border(3.dp, Color.LightGray)
                    .fillMaxHeight(1.0f) // Use all remaining space.
                    .fillMaxWidth()
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
            pair.second,
            modifier = Modifier.fillMaxWidth()
                .clickable {valueAction( pair.second )}
                .padding(0.dp)
                .background(Color.hsl(146.0f, 0.11f, 0.65f)),
            color = Color.Black,
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontFamily = FontFamily.Monospace,
        )

    }
}

@Composable
fun AutoResizedText(
    text : String,
    style : TextStyle = MaterialTheme.typography.body1,
    modifier : Modifier = Modifier,
    color : Color = style.color)
{

    var resizedTextStyle by remember { mutableStateOf(style) }
    val defaultFontSize = MaterialTheme.typography.body1.fontSize
    if( resizedTextStyle.fontSize.isUnspecified )
        resizedTextStyle = resizedTextStyle.copy(fontSize = defaultFontSize*1.0)

    var shouldDraw by remember { mutableStateOf(false) }

    Text(
        text = text,
        color = color,
        modifier = modifier.padding(0.dp).drawWithContent {
                if( shouldDraw ) { drawContent() }
        },
        softWrap = false,
        style = resizedTextStyle,
        onTextLayout = { result ->
            if( result.didOverflowWidth ) {
                val newSize = resizedTextStyle.fontSize * 0.95
                resizedTextStyle = resizedTextStyle.copy(
                    fontSize = newSize ) }
            else
                shouldDraw = true
        }
    )
}

expect fun getPlatformName(): String

