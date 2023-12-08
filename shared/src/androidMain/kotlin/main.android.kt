import androidx.compose.runtime.Composable
import model.state.CalculatorState

actual fun getPlatformName(): String = "Android"

@Composable fun MainView(calculatorState  : CalculatorState) = App(calculatorState)
