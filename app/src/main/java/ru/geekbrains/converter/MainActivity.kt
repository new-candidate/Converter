package ru.geekbrains.converter

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ru.geekbrains.converter.mvp.presenter.Presenter
import ru.geekbrains.converter.mvp.view.View
import ru.geekbrains.converter.ui.converter.ConversionImpl
import ru.geekbrains.converter.ui.converter.ImageConverterImpl
import kotlinx.android.synthetic.main.activity_main.*
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import java.io.File

class MainActivity : MvpAppCompatActivity(), View {
    private var convertDialog: Dialog? = null

    private val presenter: Presenter
            by moxyPresenter {
                Presenter(ImageConverterImpl(this))
            }

    companion object {
        val permissons = listOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        const val PERMISSIONS_REQUEST_ID = 0
        const val CONVERT_IMAGE_REQUEST_ID = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        onConvertButtonClick()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_ID -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionsGranted()
                } else {
                    showAlertDialog()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CONVERT_IMAGE_REQUEST_ID) {
            if (resultCode == Activity.RESULT_OK) {
                val scr = data?.data

                val dst = Uri.fromFile(
                    File(
                        getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        File.separator + "result.png"
                    )
                )

                val conversion = ConversionImpl(scr, dst)

                presenter.imageSelected(conversion)
            }
        }
    }

    private fun onConvertButtonClick() {
        convertor_button.setOnClickListener {
            presenter.convertImage()
        }
    }

    override fun convertImage() {
        if (!checkPermission()) {
            requestPermissions()
            return
        }

        onPermissionsGranted()
    }

    private fun checkPermission(): Boolean {
        permissons.map {
            if (ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }

        return true
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, permissons.toTypedArray(), PERMISSIONS_REQUEST_ID)
    }

    private fun onPermissionsGranted() {
        val intent = Intent()

        intent.apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }

        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            CONVERT_IMAGE_REQUEST_ID
        )
    }

    private fun showAlertDialog() = AlertDialog.Builder(this)
        .setTitle(getString(R.string.access_required))
        .setMessage(getString(R.string.grant_access))
        .setPositiveButton(getString(R.string.ok)) { dialog, which -> requestPermissions() }
        .setOnCancelListener { dialog -> requestPermissions() }
        .create()
        .show()

    private fun showConvertAlertDialog(): Dialog = AlertDialog.Builder(this)
        .setNegativeButton(getString(R.string.cancel)) { dialog, which -> presenter.onConvertationCanceledClick() }
        .setMessage(getString(R.string.convertation_in_progress))
        .create()


    override fun showConvertDialog() {
        if (convertDialog == null) {
            convertDialog = showConvertAlertDialog()
        }

        convertDialog!!.show()
    }

    override fun dismissConvertProgressDialog() {
        convertDialog.let {
            if (it!!.isShowing) {
                it.dismiss()
            }
        }
    }

    override fun showConvertationSuccessMessage() {
        Toast.makeText(this, R.string.convertation_success, Toast.LENGTH_SHORT).show()
    }

    override fun showConvertationCanceledMessage() {
        Toast.makeText(this, R.string.convertation_canceled, Toast.LENGTH_SHORT).show()
    }

    override fun showConvertationFailedMessage() {
        Toast.makeText(this, R.string.convertation_failed, Toast.LENGTH_SHORT).show()
    }

    override fun showImage(image: Bitmap?) {
        image_view.setImageBitmap(image)
    }
}