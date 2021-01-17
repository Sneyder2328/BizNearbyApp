package com.sneyder.biznearby.utils

import android.widget.EditText

class FieldsValidator {
    private var allValid = true
    fun add(isValid: Boolean) {
        if (!isValid) allValid = false
    }

    fun allInputsValid() = allValid
}

class InputValidator {
    class Builder(private val editText: EditText, transformation: (str: String) -> String = { it }) {
        private val fieldValue = transformation(editText.text.toString().trim())

        private val validations: ArrayList<Pair<String?, (fieldValue: String) -> Boolean>> =
            ArrayList()

        fun minLength(value: Int, message: String? = null): Builder {
            validations.add(message to { fieldValue -> fieldValue.length >= value })
            return this
        }

        fun maxLength(value: Int, message: String? = null): Builder {
            validations.add(message to { fieldValue -> fieldValue.length <= value })
            return this
        }

        fun required(value: Boolean = true, message: String? = null): Builder {
            validations.add(
                0,
                message to { fieldValue -> if (value) fieldValue.isNotEmpty() else true })
            return this
        }

        fun matches(value: Regex, message: String? = null): Builder {
            validations.add(message to { fieldValue -> fieldValue.matches(value) })
            return this
        }

        fun custom(condition: (fieldValue: String) -> Boolean, message: String? = null): Builder {
            validations.add(message to condition)
            return this
        }

        private fun isValid(): Boolean {
            for (validation in validations) {
                val errorMsg = validation.first
                val valid = validation.second(fieldValue)
                if (!valid) {
                    editText.error = errorMsg ?: "Valor no valido"
                    return false
                }
            }
            return true
        }

        fun build(fields: FieldsValidator): String {
            fields.add(isValid())
            return fieldValue
        }

        fun build(): Pair<String, Boolean> {
            return fieldValue to isValid()
        }

    }
}