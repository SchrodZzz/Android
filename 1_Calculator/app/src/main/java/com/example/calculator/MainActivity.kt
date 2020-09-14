package com.example.calculator

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

// TODO: ask about bonus for scrollable 'label'
// hardcoding in small projects is fine ^_^
// rip 'guard' )-;
// force unwrap is a grave sin, mostly

class MainActivity : AppCompatActivity() {

    // MARK: - Private properties

    private var lastValue: Double? = null
    private var lastOperation: String? = null




    // MARK: - Lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calculationsLabel.setTextColor(Color.BLACK)
        curNumLabel.setTextColor(Color.BLACK)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("res", "${calculationsLabel.text}")
        outState.putString("resColor", parseColor(calculationsLabel.currentTextColor))
        outState.putString("num", "${curNumLabel.text}")
        outState.putString("numColor", parseColor(curNumLabel.currentTextColor))
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        calculationsLabel.text = savedInstanceState.getString("res")
        restoreColor(calculationsLabel, savedInstanceState.getString("resColor"))
        curNumLabel.text = savedInstanceState.getString("num")
        restoreColor(curNumLabel, savedInstanceState.getString("numColor"))
    }


    // MARK: - Actions

    fun performOperation(view: View) {
        if (lastOperation != null && lastValue != null) {
            performLastOperation()
        }
        lastOperation = (view as Button).text.toString()
        lastValue = calculationsLabel.text.toString().toDouble()
        if (lastOperation != "=") {
            calculationsLabel.text = "0"
        }
    }

    fun clear(view: View) {
        lastOperation = null
        lastValue = null
        calculationsLabel.text = "0"
        calculationsLabel.setTextColor(Color.BLACK)
        curNumLabel.text = "0"
        curNumLabel.setTextColor(Color.BLACK)
    }

    fun changeNum(view: View) {
        val num = curNumLabel.text.toString().toInt()
        if (view == inrcButton && num in 0..8) {
            curNumLabel.text = (num + 1).toString()
            curNumLabel.setTextColor(Color.BLACK)
        } else if (view == decrButton && num in 1..9) {
            curNumLabel.text = (num - 1).toString()
            curNumLabel.setTextColor(Color.BLACK)
        } else {
            curNumLabel.setTextColor(Color.RED)
        }
    }

    fun addPoint(view: View) {
        if (!calculationsLabel.text.toString().contains('.')) {
            calculationsLabel.append(".")
            calculationsLabel.setTextColor(Color.BLACK)
        } else {
            calculationsLabel.setTextColor(Color.RED)
        }
    }

    fun enterNum(view: View) {
        if (calculationsLabel.text.toString() == "0") {
            calculationsLabel.text = ""
        }
        calculationsLabel.append(curNumLabel.text)
        calculationsLabel.setTextColor(Color.BLACK)
        curNumLabel.text = "0"
        curNumLabel.setTextColor(Color.BLACK)
    }


    // MARK: - Private functions

    private fun restoreColor(view: TextView, colorStr: String?) {
        if (colorStr == "black") {
            view.setTextColor(Color.BLACK)
        } else {
            view.setTextColor(Color.RED)
        }
    }

    private fun parseColor(color: Int): String {
        if (color == Color.BLACK) {
            return "black"
        }
        return "red"
    }

    private fun performLastOperation() {
        val currentValue = calculationsLabel.text.toString().toDouble()
        var result = currentValue
        when (lastOperation) {
            "+" -> result = lastValue!! + currentValue
            "-" -> result = lastValue!! - currentValue
            "/" -> result = lastValue!! / currentValue
            "*" -> result = lastValue!! * currentValue
        }
        calculationsLabel.text = result.toString()
    }

}