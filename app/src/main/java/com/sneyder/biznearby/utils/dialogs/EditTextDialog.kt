package com.sneyder.biznearby.utils.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sneyder.biznearby.R

class EditTextDialog : DialogFragment() {

    companion object {

        private const val ARG_TITLE = "title"
        private const val ARG_VALUE = "value"
        private const val ARG_HINT = "hint"

        fun newInstance(
            title: String,
            defaultValue: String,
            hint: String
        ): EditTextDialog {
            return EditTextDialog().apply {
                arguments = bundleOf(
                    ARG_TITLE to title,
                    ARG_VALUE to defaultValue,
                    ARG_HINT to hint
                )
            }
        }
    }

    private var listener: EditTextDialogListener? = null

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments?.getString(ARG_TITLE)
            ?: throw Exception("No value passed to argument ARG_TITLE in EditTextDialog")
        val value = arguments?.getString(ARG_VALUE)
            ?: throw Exception("No value passed to argument ARG_VALUE in EditTextDialog")
        val hint = arguments?.getString(ARG_HINT)
            ?: throw Exception("No value passed to argument ARG_HINT in EditTextDialog")

        val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_edit_text, null)

        val inputEditText = view.findViewById<EditText>(R.id.inputEditText)
        inputEditText.setText(value)
        if (value.isNotEmpty()) inputEditText.setSelection(value.length)
        inputEditText.hint = hint

        return MaterialAlertDialogBuilder(requireContext())
            .setView(view)
            .setTitle(title)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val textTyped = inputEditText.text.toString().trim()
                listener?.onTextEntered(textTyped)
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                targetFragment?.onActivityResult(
                    targetRequestCode,
                    Activity.RESULT_CANCELED,
                    Intent()
                )
                dialog.cancel()
            }
            .create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = when {
            context is EditTextDialogListener -> {
                context
            }
            parentFragment is EditTextDialogListener -> {
                parentFragment as EditTextDialogListener
            }
            else -> {
                throw RuntimeException("$context must implement EditTextDialogListener")
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface EditTextDialogListener {
        fun onTextEntered(text: String)
    }
}