package viewModel

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import model.data.NumberDisplayMode
import model.state.ButtonDescription
import model.state.CalculatorModel

data class UIState(
    val top : String,
    val stackStrings : List<String>,
    val envPairs : List<Pair<String,String>>,
    val buttons : List<List<ButtonDescription>>,
    val base : String,
    val displayMode: String,
    val error : String? = null,
)

class CalculatorViewModel(private val calculatorModel : CalculatorModel) : ViewModel() {
    private val _uiState : MutableStateFlow<UIState>
        = MutableStateFlow(UIState( "", emptyList(), emptyList(), calculatorModel.buttons(), "",
                                    displayMode = "", error = null))
    val uiState : StateFlow<UIState> = _uiState.asStateFlow()

    init {
        calculatorModel.connect { this.updateUIState() }
        updateUIState()
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun click(desc : ButtonDescription ) { desc.primaryOperation.clickAction(calculatorModel) }

    private fun updateUIState() {
        println( "Updating UI state")
        viewModelScope.launch{
            _uiState.update {
                // Strings to display
                // Does not change buttons
                it.copy( top =  calculatorModel.renderTop(),
                    stackStrings = calculatorModel.renderStack(),
                    envPairs = calculatorModel.renderEnv(),
                    base = calculatorModel.mode().base.toString(),
                    displayMode = calculatorModel.mode().displayMode.toString(),
                    error = calculatorModel.nextError() )
            }
        }
    }

    fun cancelError() {
        calculatorModel.cancelError()
    }

    fun setBase( newBase: Int ) = calculatorModel.setBase( newBase )

    fun setDisplayMode( newMode : NumberDisplayMode ) = calculatorModel.setDisplayMode( newMode )

    fun makeVarRef(name: String) = calculatorModel.makeVarRef( name )
}