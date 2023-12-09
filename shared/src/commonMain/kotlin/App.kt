import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
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
fun App(calculatorState : CalculatorState) {

    ImageAppTheme {
        val viewModel = getViewModel( Unit, viewModelFactory { CalculatorViewModel(calculatorState) } )
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

        AnimatedVisibility(uiState.strings.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.padding(horizontal = 5.dp),
                content = {
                    items(
                        uiState.strings.size,
                        itemContent = { k -> stackItemView(uiState.strings[k], k==uiState.strings.size-1) })
                }
            )
        }
        LazyColumn( // Of Button Rows
            verticalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            content = {
                items(count = uiState.buttons.size,
                    itemContent = { k ->
                        LazyRow( // of Buttons
                            horizontalArrangement =  Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth().weight(1.0f),
                            content = {
                                items(count = uiState.buttons[k].size,
                                    itemContent = { c ->
                                        mkButton(viewModel, uiState.buttons[k][c])
                                    })
                            })
                    })
            })
    }
}

@Composable
private fun LazyItemScope.mkButton(
    viewModel: CalculatorViewModel,
    desc : String
) {

    val w = 1.0f // when(desc){"enter" -> 2.0f ; else -> 1.0f}
    ElevatedButton(
          //modifier = Modifier.background(Color.hsl(241.0f, 0.50f, 0.34f)),
          colors = ButtonDefaults.buttonColors(
              containerColor = Color.hsl(220.0f, 0.62f, 0.34f) ),
        onClick = { viewModel.click(desc) }
    ) {
        Text(
            text = desc,
            color = Color.White,
            modifier = Modifier.background(Color.Transparent)
        )
    }
}

@Composable
fun stackItemView(str: String, isFinal : Boolean) {
    Text(
        modifier = Modifier.background(Color.hsl(146.0f, 0.11f, 0.65f)),
        text = str,
        color = Color.Black,
        fontSize = 5.0 .em,
        fontFamily = FontFamily.Monospace,
    )
}
expect fun getPlatformName(): String

