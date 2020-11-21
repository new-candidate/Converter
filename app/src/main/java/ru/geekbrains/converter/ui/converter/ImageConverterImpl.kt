package ru.geekbrains.converter.ui.converter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import io.reactivex.rxjava3.core.Completable
import ru.geekbrains.converter.mvp.model.Conversion
import ru.geekbrains.converter.mvp.model.ImageConverter


class ImageConverterImpl(private val context: Context) : ImageConverter {
    override var imageBitmap: Bitmap? = null

//    @RequiresApi(Build.VERSION_CODES.P)
    override fun convertJpgToPng(conversion: Conversion): Completable {
        return Completable.fromAction {

            val src = Uri.parse(conversion.getSrc())
            val dst = Uri.parse(conversion.getDst())

            val source = ImageDecoder.createSource(context.contentResolver, src)
            imageBitmap = ImageDecoder.decodeBitmap(source)

            imageBitmap!!.compress(
                Bitmap.CompressFormat.PNG,
                100,
                context.contentResolver.openOutputStream(dst)
            )
        }
    }
}