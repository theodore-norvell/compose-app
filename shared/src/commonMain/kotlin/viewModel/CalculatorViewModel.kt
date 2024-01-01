package viewModel

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import model.data.NumberDisplayMode
import model.layouts.ButtonDescription
import model.layouts.ButtonOperation
import model.state.CalculatorModel
import model.state.EvalMode

data class UIState(
    val top : String,
    val stackStrings : List<String>,
    val envPairs : List<Pair<String,String>>,
    val buttons : List<List<ButtonDescription>>,
    val base : String,
    val displayMode: String,
    val evalMode: String,
    val error : String? = null,
    val shifted : Boolean = false
)

class CalculatorViewModel(private val calculatorModel : CalculatorModel) : ViewModel() {
    private val _uiState : MutableStateFlow<UIState>
        = MutableStateFlow(UIState( "", emptyList(), emptyList(), calculatorModel.buttons(), "",
                                    displayMode = "", evalMode = "", error = null,
                                    shifted = false ) )
    val uiState : StateFlow<UIState> = _uiState.asStateFlow()

    init {
        calculatorModel.connect { this.updateUIState() }
        updateUIState()
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun click(operation : ButtonOperation) { operation.clickAction(calculatorModel) }

    private fun updateUIState() {
        println( "Updating UI state")
        viewModelScope.launch{
            _uiState.update {
                // Strings to display
                // Does not change buttons
                it.copy( top =  calculatorModel.renderTop(),
                    stackStrings = calculatorModel.renderStack(),
                    envPairs = calculatorModel.renderEnv(),
                    buttons = calculatorModel.buttons(),
                    base = calculatorModel.mode().base.toString(),
                    displayMode = calculatorModel.mode().displayMode.toString(),
                    evalMode = calculatorModel.mode().evalMode.toString(),
                    error = calculatorModel.nextError(),
                    shifted = calculatorModel.shifted())
            }
        }
    }

    fun cancelError() {
        calculatorModel.cancelError()
    }

    fun setBase( newBase: Int ) = calculatorModel.setBase( newBase )

    fun setDisplayMode( newMode : NumberDisplayMode ) = calculatorModel.setDisplayMode( newMode )
    fun setEvalMode( newMode : EvalMode) = calculatorModel.setEvalMode( newMode )

    fun makeVarRef(name: String) = calculatorModel.makeVarRef( name )
}