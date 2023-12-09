package viewModel

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import model.state.CalculatorState

data class UIState(
    val strings : List<String> = emptyList(),
    val buttons : List<List<String>> = listOf(
                        listOf( "STO", "RCL", "SIN", "COS", "TAN"),
                        listOf( "ENTER", "+/-", "SWAP", "<-"),
                        listOf( "7", "8", "9", "/"),
                        listOf( "4", "5", "6", "*"),
                        listOf( "1", "2", "3", "-"),
                        listOf( "0", ".", "E", "+")
        )
)

class CalculatorViewModel(private val calculatorState : CalculatorState ) : ViewModel() {
    private val _uiState : MutableStateFlow<UIState> = MutableStateFlow(UIState())
    val uiState : StateFlow<UIState> = _uiState.asStateFlow()

    init {
        calculatorState.connect { this.updateUIState() }
        updateUIState()
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun click(buttonName : String ) { calculatorState.pushSomething() }

    private fun updateUIState() {
        println( "Updating UI state")
        viewModelScope.launch{
            _uiState.update {
                val strs = calculatorState.renderStack()
                val strs1 = listOf( "", "", "") + strs
                val first3 = strs1.subList(strs1.size-3, strs1.size)
                it.copy( strings = first3 )
            }
        }
    }
}