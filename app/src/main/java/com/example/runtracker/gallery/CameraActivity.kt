package com.example.runtracker.gallery

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import androidx.camera.lifecycle.ProcessCameraProvider
import com.google.common.util.concurrent.ListenableFuture
import android.view.Surface
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.runtracker.R
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Activity for capturing photos using CameraX.
 */
class CameraActivity : AppCompatActivity() {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var savedFilePath: String
    private lateinit var captureButton: ImageButton
    private lateinit var cameraProvider: ListenableFuture<ProcessCameraProvider>
    private lateinit var previewView: PreviewView
    var latitude = 0.0
    var longitude = 0.0
    private var filename: String = ""
    var runID: Int = 0

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        // Initialize variables with intent data
        runID = intent.getIntExtra("runID", 0)
        latitude = intent.getDoubleExtra("latitude", 0.0)
        longitude = intent.getDoubleExtra("longitude", 0.0)
        filename = (kotlin.math.abs(latitude) + kotlin.math.abs(longitude)).toString().replace(".", "")
        filename += "${System.currentTimeMillis()}"
        savedFilePath = "${getExternalFilesDir(null)?.absolutePath}/${filename}.jpg"

        // Set up camera and UI elements
        cameraExecutor = Executors.newSingleThreadExecutor()
        captureButton = findViewById(R.id.imageButtonCapture)
        previewView = findViewById(R.id.camera)
        cameraProvider = ProcessCameraProvider.getInstance(this)

        // Request camera permission
        requestPermission()
    }

    private fun requestPermission() {
        requestCameraPermissionIfMissing { granted ->
            if (granted) {
                startCamera()
            }
        }
    }

    private fun startCamera() {
        cameraProvider.addListener({
            val camera = cameraProvider.get()
            bindPreview(camera)
            val imageCapture = ImageCapture.Builder().setTargetRotation(Surface.ROTATION_0).build()
            runOnUiThread {
                camera.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, imageCapture)
                captureButton.setOnClickListener {
                    takePicture(imageCapture)
                }
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview: Preview = Preview.Builder().build()
        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        preview.setSurfaceProvider(previewView.surfaceProvider)
        cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview)
    }

    private fun takePicture(imageCapture: ImageCapture) {
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(File(savedFilePath)).build()
        imageCapture.takePicture(outputFileOptions, cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(error: ImageCaptureException) {
                    Toast.makeText(
                        applicationContext,
                        "Error with initializing camera.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = outputFileResults.savedUri ?: Uri.fromFile(File(savedFilePath))
                    cameraExecutor.execute {
                        saveImage(savedUri)
                        runOnUiThread {
                            val intent = Intent()
                            intent.putExtra("latitude", latitude)
                            intent.putExtra("longitude", longitude)
                            intent.putExtra("image_path", "${filename}.jpg")
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                    }
                }
            })
    }

    private fun rotateBitmap(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            matrix.postRotate(0F)
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }
        matrix.postRotate(90F)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun saveImage(uri: Uri) {
        val bitmap =
            rotateBitmap(
                BitmapFactory.decodeStream(
                    applicationContext.contentResolver
                        .openInputStream(uri)
                )
            )

        val directory = ContextWrapper(this).getDir("imageDir", Context.MODE_PRIVATE)
        val subDirectory = File(directory, "$runID")
        subDirectory.mkdirs()

        val fileName = "${filename}.jpg"
        val myImageFile = File(subDirectory, fileName)

        FileOutputStream(myImageFile).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
        }
    }

    private fun requestCameraPermissionIfMissing(onResult: (Boolean) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            onResult(true)
        } else {
            val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                onResult(isGranted)
            }
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}
