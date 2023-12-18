package viewModel

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import model.state.ButtonDescription
import model.state.CalculatorModel

data class UIState(
    val top : String,
    val stackAndMemory : List<String>,
    val buttons : List<List<ButtonDescription>>,
    val base : String,
    val error : String? = null,
)

class CalculatorViewModel(private val calculatorModel : CalculatorModel) : ViewModel() {
    private val _uiState : MutableStateFlow<UIState>
        = MutableStateFlow(UIState( "", emptyList(), calculatorModel.buttons(), "", null))
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
                val topString = calculatorModel.renderTop()
                val strings = calculatorModel.renderStack()
                val error = calculatorModel.nextError()
                val base = calculatorModel.mode().base.toString()
                // Does not change buttons
                it.copy( top = topString, stackAndMemory = strings, base = base, error = error )
            }
        }
    }

    fun cancelError() {
        calculatorModel.cancelError()
    }

    fun setBase( newBase: Int ) = calculatorModel.setBase( newBase )
}