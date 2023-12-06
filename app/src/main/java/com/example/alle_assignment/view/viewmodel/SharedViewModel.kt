package com.example.alle_assignment.view.viewmodel

import android.app.Application
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.alle_assignment.data.model.ImageData
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    val galleryImages = MutableLiveData<List<String>>()

    fun syncGalleryImages() {
        val imageList = mutableListOf<String>()

        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " = ?"
        val selectionArgs = arrayOf("Screenshots")  // Assuming "Screenshots" is the folder name
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )

        cursor?.use {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            while (cursor.moveToNext()) {
                val imagePath = cursor.getString(columnIndex)
                imageList.add(imagePath)
            }
        }

        galleryImages.postValue(imageList)

    }
     val imageData = MutableLiveData<ImageData>()
     val isLoading = MutableLiveData<Boolean>()

    fun fetchData(imagePath: String) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val labelsDeferred = async { fetchCollection(imagePath) }
                val descriptionDeferred = async { fetchDescription(imagePath) }
                val labels = labelsDeferred.await()
                val description = descriptionDeferred.await()

                val image = ImageData(labels, imagePath, description)
                imageData.postValue(image)
            } catch (e: Exception) {
                // Handle exceptions, possibly update UI
                Log.d("checkError", "fetchData: ${e.message}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    private fun fetchCollection(imagePath: String): MutableList<String> {
        val image = File(imagePath)
        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
        val labelList : MutableList<String> = mutableListOf()

        if (image.exists()) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            val imageBit = InputImage.fromBitmap(bitmap, 0)
            labeler.process(imageBit)
                .addOnSuccessListener { labels ->
                    // Task completed successfully
                    // ...
                    for (label in labels) {

                        val text = label.text
                        val confidence = label.confidence
                        //we can also check for the confidence here
                        labelList.add(text)
                    }

                    Log.d("checkColl", "fetchDescription: and $labels")

                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                    // ...
                    Log.d("checkDes", "fetchDescription: ${e.message}")

                }
        }
        return labelList

    }

    private suspend fun fetchDescription(imagePath: String): String = suspendCoroutine { continuation ->
        val image = File(imagePath)
        if (image.exists()) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            val imageBit = InputImage.fromBitmap(bitmap, 0)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(imageBit)
                .addOnSuccessListener { visionText ->
                    val resultText = StringBuilder().apply {
                        visionText.textBlocks.take(50).forEach { block ->
                            append(block.text + "\n")
                        }
                    }.toString()
                    continuation.resume(resultText)
                }
                .addOnFailureListener { e ->
                    Log.d("checkDes", "error: ${e.message}")
                    continuation.resume("") // Resume with empty string or handle error appropriately
                }
        } else {
            continuation.resume("") // Resume with empty string if file doesn't exist
        }
    }


}

