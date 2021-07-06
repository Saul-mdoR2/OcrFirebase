package com.example.ocrfirebase

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private val permissionCode = 100
    private val requestTakePicture = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermissions()
        val btnCamera = findViewById<ImageButton>(R.id.btnCamera)
        btnCamera.setOnClickListener {
            openCamera()
        }
    }

    private fun checkPermissions() {
        val permissionGrantedCamera = ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (!permissionGrantedCamera) {
            requestPermission()
        }
    }

    private fun requestPermission() {
        val showRequestRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            android.Manifest.permission.CAMERA
        )
        if (showRequestRationale) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                permissionCode
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                permissionCode
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            permissionCode -> {
                if (grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(
                        this,
                        getString(R.string.message_permission_denied),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    @SuppressLint("QueryPermissionsNeeded")
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, requestTakePicture)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            requestTakePicture -> {
                if (resultCode == Activity.RESULT_OK) {
                    val extras = data?.extras
                    val bitmap = extras?.get("data") as Bitmap
                    recognizeText(bitmap)
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun recognizeText(bitmap: Bitmap) {
        val firebaseVisionImage: FirebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap)
        val firebaseVision = FirebaseVision.getInstance()
        val firebaseVisionTextRecognizer = firebaseVision.onDeviceTextRecognizer
        val task: Task<FirebaseVisionText> =
            firebaseVisionTextRecognizer.processImage(firebaseVisionImage)
        task.addOnSuccessListener {
            findViewById<TextView>(R.id.tvResult).text = it.text
        }
        task.addOnFailureListener {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }
}