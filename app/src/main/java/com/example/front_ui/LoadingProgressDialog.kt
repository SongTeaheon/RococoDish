package com.example.front_ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import kotlinx.android.synthetic.main.dialog_loading.*
import org.jetbrains.anko.matchParent

class LoadingProgressDialog(context : Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_loading)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(window.attributes)
        lp.width = matchParent
        lp.height = matchParent
        lp.flags =WindowManager.LayoutParams.FLAG_DIM_BEHIND
        lp.dimAmount = 0.8f
        window.attributes = lp

        lottie_anim.apply {
            setAnimation("lottie_foods.json")
            loop(true)
            playAnimation()
        }
    }
}