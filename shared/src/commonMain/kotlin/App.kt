import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import model.state.ButtonDescription
import model.state.CalculatorModel
import model.state.CalculatorState
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
    Column( modifier = Modifier.fillMaxSize().background( Color.DarkGray ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {

        val displayScrollState = rememberScrollState( )

        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.fillMaxWidth()
                                .fillMaxHeight(0.25f)
                                .padding(horizontal = 5.dp)
                                .scrollable(displayScrollState, orientation = Orientation.Vertical),
            content = {
                uiState.strings.forEach{ str -> stackItemView(str) }
            }
        )
        Column( // Of Button Rows
            verticalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp))
            {
                uiState.buttons.forEach{ rowOfStrings ->
                    Row( // of Buttons
                        horizontalArrangement =  Arrangement.SpaceBetween,
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
fun stackItemView(str: String) {
    Text(
        modifier = Modifier.fillMaxWidth().background(Color.hsl(146.0f, 0.11f, 0.65f)),
        text = str,
        color = Color.Black,
        fontSize = 20.sp,
        fontFamily = FontFamily.Monospace,
    )
}
expect fun getPlatformName(): String

