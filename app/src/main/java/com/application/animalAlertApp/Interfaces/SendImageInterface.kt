package com.application.animalAlertApp.Interfaces

import android.net.Uri
import java.io.File

interface SendImageInterface {
    fun onImagesend(file: File,uri: Uri)
}