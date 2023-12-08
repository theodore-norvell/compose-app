import androidx.compose.ui.window.ComposeUIViewController
import model.state.CalculatorState

actual fun getPlatformName(): String = "iOS"

fun MainViewController() = ComposeUIViewController {
    val calculatorState = CalculatorState()
    App(calculatorState)
}