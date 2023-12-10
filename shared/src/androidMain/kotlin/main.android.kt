import androidx.compose.runtime.Composable
import model.state.CalculatorModel

actual fun getPlatformName(): String = "Android"

@Composable fun MainView(calculatorModel : CalculatorModel) = App(calculatorModel)
