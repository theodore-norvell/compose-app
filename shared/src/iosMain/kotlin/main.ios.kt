import androidx.compose.ui.window.ComposeUIViewController
import model.state.CalculatorModel
import platform.UIKit.UIViewController

actual fun getPlatformName(): String = "iOS"

fun MainViewController() : UIViewController {

    val calculatorModel = CalculatorModel()
    return ComposeUIViewController { App(calculatorModel) }
}