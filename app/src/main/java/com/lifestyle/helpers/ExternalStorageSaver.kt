package com.lifestyle.helpers

import android.graphics.Bitmap
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.*

class ExternalStorageSaver {

    companion object {
        fun saveBitmap(bitmap: Bitmap, width: Int = -1, height: Int = -1): String? {
            val externalDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val photoDir = File("${externalDir.absolutePath}/lifestyle/pfp/").apply {
                mkdirs()
            }
            val img = File("${photoDir}/${UUID.randomUUID()}.png")

            try {
                val outStream = FileOutputStream(img)
                var candidateBitmap: Bitmap = bitmap
                if (height != -1 && width != -1) {
                    candidateBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)
                }

                candidateBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
                outStream.apply {
                    flush()
                    close()
                }
                return img.toUri().toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }

}