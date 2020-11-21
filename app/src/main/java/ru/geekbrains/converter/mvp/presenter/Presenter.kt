package ru.geekbrains.converter.mvp.presenter

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import ru.geekbrains.converter.mvp.model.Conversion
import ru.geekbrains.converter.mvp.model.ImageConverter
import ru.geekbrains.converter.mvp.view.View

class Presenter(private val converter: ImageConverter) : MvpPresenter<View>() {
    var compositeDisposable = CompositeDisposable()

    fun convertImage() {
        viewState.convertImage()
    }

    fun imageSelected(conversion: Conversion) {
        viewState.showConvertDialog()

        converter.convertJpgToPng(conversion)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { compositeDisposable.addAll(it) }
            .subscribe(
                {
                    viewState.showConvertationSuccessMessage()
                    viewState.dismissConvertProgressDialog()
                    viewState.showImage(converter.imageBitmap)
                }, {
                    viewState.showConvertationFailedMessage()
                    viewState.dismissConvertProgressDialog()
                }
            )
    }

    fun onConvertationCanceledClick() {
        compositeDisposable.let {
            if (!it.isDisposed) {

                viewState.dismissConvertProgressDialog()
                viewState.showConvertationCanceledMessage()
            }
        }
    }
}