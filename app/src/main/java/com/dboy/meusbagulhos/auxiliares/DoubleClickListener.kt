package com.dboy.meusbagulhos.auxiliares

import android.os.SystemClock
import android.view.View

abstract class DoubleClickListener : View.OnClickListener {
    private val delta: Long = DEFAULT_QUALIFICATION_SPAN
    private var deltaClick: Long

    override fun onClick(v: View) {
        v.handler.removeCallbacksAndMessages(null)
        v.handler.postDelayed({ onSingleClick() }, delta)
        if (SystemClock.elapsedRealtime() - deltaClick < delta) {
            v.handler.removeCallbacksAndMessages(null)
            onDoubleClick()
        }
        deltaClick = SystemClock.elapsedRealtime()
    }

    abstract fun onDoubleClick()
    abstract fun onSingleClick()

    companion object {
        private const val DEFAULT_QUALIFICATION_SPAN: Long = 300 //configurar aqui o tempo!
    }

    init {
        deltaClick = 0
    }
}