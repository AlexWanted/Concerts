package ru.rewindforce.concerts.utils

import android.content.Context
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

open class Presenter<M>(var fragment: BaseFragment? = null) where M: Model {

    lateinit var model: M

    fun attach(fragment: BaseFragment) {
        this.fragment = fragment
    }

    fun detach() {
        model.onDestroy()
        fragment = null
    }

    @Suppress("UNCHECKED_CAST")
    fun <R> callback(onResponse: (response: R, code: Int) -> Unit, onFailure: () -> Unit): Model.ResponseCallback =
        object: Model.ResponseCallback {
            override fun <T> onResponse(response: T, code: Int) {
                if(fragment != null) response?.let { onResponse.invoke(response as R, code) }
            }

            override fun onError() { if(fragment != null) onFailure.invoke() }
        }

    fun createImagePart(ctx: Context, tempName: String, fieldName: String, byteArray: ByteArray?): MultipartBody.Part {
        val reqFile: RequestBody
        if (byteArray != null) {
            val file = File(ctx.cacheDir, tempName)
            if (file.exists()) file.delete()
            try {
                file.createNewFile()
                val fos = FileOutputStream(file)
                fos.write(byteArray)
                fos.flush()
                fos.close()
            } catch (e: IOException) { e.printStackTrace() }

            reqFile = RequestBody.create(MediaType.parse("image/*"), file)
            return MultipartBody.Part.createFormData(fieldName, file.name, reqFile)
        } else {
            reqFile = RequestBody.create(MultipartBody.FORM, "")
            return MultipartBody.Part.createFormData(fieldName, "", reqFile)
        }
    }
}