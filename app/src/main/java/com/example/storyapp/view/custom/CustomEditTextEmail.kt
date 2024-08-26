package com.example.storyapp.view.custom

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class CustomEditTextEmail : TextInputEditText {

    private var textInputLayout: TextInputLayout? = null
    private var errorIcon: Drawable? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        textInputLayout = parent.parent as? TextInputLayout
        errorIcon = textInputLayout?.errorIconDrawable
    }

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val email = s?.toString() ?: ""
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    errorIcon?.setBounds(
                        0,
                        0,
                        errorIcon?.intrinsicWidth ?: 0,
                        errorIcon?.intrinsicHeight ?: 0
                    )
                    setCompoundDrawables(null, null, errorIcon, null)

                    textInputLayout?.error = "Format email salah"
                } else {
                    setCompoundDrawables(null, null, null, null)
                    textInputLayout?.error = null
                }
            }
        })
    }
}
