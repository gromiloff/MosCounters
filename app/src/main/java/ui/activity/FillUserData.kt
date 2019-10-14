package ui.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.annotation.LayoutRes
import base.ApiPref
import com.google.android.material.textfield.TextInputEditText
import gromiloff.observer.EmptyBaseModel
import gromiloff.observer.event.OpenScreenWithModelData
import gromiloff.prefs.AppPref
import pro.gromiloff.mos.counters.R
import ui.BaseActivity

class FillUserData : BaseActivity<EmptyBaseModel>(), TextWatcher {
    private var save: View? = null
    private var paycodeEditText: TextInputEditText? = null
    private var flatEditText: TextInputEditText? = null
    
    @LayoutRes
    override fun layoutId() = R.layout.activity_fill_user_data

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        this.save = findViewById(R.id.save)
        this.paycodeEditText = findViewById(R.id.paycodeInput)
        this.flatEditText = findViewById(R.id.flatInput)

        this.paycodeEditText?.addTextChangedListener(this)
        this.flatEditText?.addTextChangedListener(this)

        this.save?.isEnabled = false
        this.save?.setOnClickListener {
            AppPref.save(ApiPref.PayCode, paycodeEditText?.text.toString().toLong())
            AppPref.save(ApiPref.Flat, flatEditText?.text.toString().toInt())
            OpenScreenWithModelData(Main::class.java, true).send()
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun afterTextChanged(s: Editable?) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        this.save?.isEnabled =
            this.paycodeEditText?.length() == 10 && this.flatEditText?.length() ?: 0 > 1
    }

}
