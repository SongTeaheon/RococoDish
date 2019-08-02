package com.rococodish.front_ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import kotlinx.android.synthetic.main.dialog_setting.*

class SettingActivity_Dialog(context : Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //다이얼로그 밖의 화면을 흐리게
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.apply {
            this.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            this.dimAmount = 0.8f
        }
        window.attributes = layoutParams

        setContentView(R.layout.dialog_setting)

        button.setOnClickListener {
            dismiss()
        }

    }
}