package ru.geekbrains.converter.ui.converter

import android.net.Uri
import ru.geekbrains.converter.mvp.model.Conversion

class ConversionImpl(private val src: Uri?, private val dst: Uri) : Conversion {
    override fun getSrc() = src.toString()

    override fun getDst() = dst.toString()
}