import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import model.state.CalculatorState
import viewModel.UIState
import viewModel.ImageViewModel

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
        val viewModel = getViewModel( Unit, viewModelFactory { ImageViewModel(calculatorState) } )
        imagePage( viewModel )
    }
}

@Composable
fun imagePage( viewModel : ImageViewModel) {
    val uiState : UIState = viewModel.uiState.collectAsState().value
    Column( Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
        Row( Modifier.fillMaxWidth().padding( 5.dp ) ) {
            Button(
                modifier = Modifier.aspectRatio(1.0f).fillMaxSize().weight(1.0f).padding(5.dp),
                onClick = { viewModel.click()
                }) { Text(
                        modifier = Modifier.fillMaxWidth().aspectRatio(1.0f),
                        text = "CLICK ME"
            ) }
        }
        AnimatedVisibility(uiState.strings.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.fillMaxSize().padding(horizontal = 5.dp),
                content = {
                    items(uiState.strings.size, itemContent = { k -> cell(uiState.strings[k]) })
                }
            )
        }
    }
}

@Composable
fun cell( str: String) {
    Text(
        modifier = Modifier.fillMaxWidth().aspectRatio(1.0f),
        text = str
    )
}

expect fun getPlatformName(): String

