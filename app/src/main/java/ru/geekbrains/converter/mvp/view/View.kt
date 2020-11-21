package ru.geekbrains.converter.mvp.view

import android.graphics.Bitmap
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface View : MvpView {
    fun convertImage()
    fun showConvertDialog()
    fun dismissConvertProgressDialog()
    fun showConvertationSuccessMessage()
    fun showConvertationCanceledMessage()
    fun showConvertationFailedMessage()
    fun showImage(image : Bitmap?)
}