import androidx.compose.ui.window.ComposeUIViewController
import model.state.CalculatorState
import platform.UIKit.UIViewController

actual fun getPlatformName(): String = "iOS"

fun MainViewController() : UIViewController {

    val calculatorState = CalculatorState()
    return ComposeUIViewController { App(calculatorState) }
}