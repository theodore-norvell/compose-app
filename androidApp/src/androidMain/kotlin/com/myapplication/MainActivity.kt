package com.myapplication

import MainView
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import model.state.CalculatorModel

class MainActivity : AppCompatActivity() {
    private val calculatorModel = CalculatorModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainView(calculatorModel)
        }
    }
}