package com.muhammadfahreza.storyapp.view.custom

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputLayout
import com.muhammadfahreza.storyapp.R

class CustomPasswordView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextInputLayout(context, attrs) {

    init {
        post {
            if (editText == null) {
                error = context.getString(R.string.password_error_message)
            } else {
                editText?.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                    override fun afterTextChanged(s: Editable?) {
                        validatePassword(s?.toString() ?: "")
                    }
                })
            }
        }
    }

    private fun validatePassword(password: String) {
        if (password.length < 8) {
            error = context.getString(R.string.password_error_message)
            isErrorEnabled = true
        } else {
            error = null
            isErrorEnabled = false
        }
    }
}
