package com.example.ekagra_infilect_assignment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions

data class ScannedObj(val id: Int, val x: Float, val y: Float)

class MainActivity : AppCompatActivity() {

    lateinit var camView: PreviewView
    lateinit var overlay: OverlayView

    val camPermission = Manifest.permission.CAMERA

    // store ids
    val scannedList = mutableMapOf<Int, ScannedObj>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        camView = findViewById(R.id.previewView)
        overlay = findViewById(R.id.overlayView)

        if (hasPermission()) {
            startCam()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(camPermission), 200)
        }
    }

    fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, camPermission) == PackageManager.PERMISSION_GRANTED
    }

    fun startCam() {
        val providerFuture = ProcessCameraProvider.getInstance(this)

        providerFuture.addListener({
            val provider = providerFuture.get()

            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(camView.surfaceProvider)

            val analysis = ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()

            // detector
            val options = ObjectDetectorOptions.Builder().setDetectorMode(ObjectDetectorOptions.STREAM_MODE).enableMultipleObjects().build()

            val detector = ObjectDetection.getClient(options)

            analysis.setAnalyzer(ContextCompat.getMainExecutor(this)) { img ->
                val frame = img.image
                if (frame != null) {
                    val rot = img.imageInfo.rotationDegrees
                    val input = InputImage.fromMediaImage(frame, rot)

                    detector.process(input)
                        .addOnSuccessListener { items ->
                            showTicks(items, input.width, input.height)
                        }
                        .addOnCompleteListener {
                            img.close()
                        }
                } else {
                    img.close()
                }
            }

            provider.unbindAll()
            provider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)

        }, ContextCompat.getMainExecutor(this))
    }

    fun showTicks(list: List<DetectedObject>, imgW: Int, imgH: Int) {

        val vw = overlay.width.toFloat()
        val vh = overlay.height.toFloat()

        val sx = vw / imgW
        val sy = vh / imgH

        val ticks = ArrayList<TickPoint>()

        for (obj in list) {
            val id = obj.trackingId ?: continue

            val box = obj.boundingBox
            val cx = ((box.left + box.right) / 2f) * sx
            val cy = ((box.top + box.bottom) / 2f) * sy

            // save once
            if (!scannedList.containsKey(id)) {
                scannedList[id] = ScannedObj(id, cx, cy)
            } else {
                scannedList[id] = scannedList[id]!!.copy(x = cx, y = cy)
            }

            ticks.add(TickPoint(cx, cy))
        }

        overlay.updateTicks(ticks)
    }
}
