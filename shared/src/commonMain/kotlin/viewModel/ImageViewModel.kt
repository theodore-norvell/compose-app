package viewModel

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import model.state.CalculatorState

data class UIState(
    val strings : List<String> = emptyList()
)

class ImageViewModel(val calculatorState : CalculatorState ) : ViewModel() {
    private val _uiState : MutableStateFlow<UIState> = MutableStateFlow(UIState())
    val uiState : StateFlow<UIState> = _uiState.asStateFlow()

    init {
        calculatorState.connect { this.updateUIState() }
        updateUIState()
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun click() = calculatorState.pushSomething()

    private fun updateUIState() {
        println( "Updating UI state")
        viewModelScope.launch{
            val strings = calculatorState.stack().map {it.render( calculatorState.env() )}
            _uiState.update {
                it.copy( strings = strings )
            }
        }
    }
}