package com.bikebell.app

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import android.util.Log
import java.util.Date
import kotlin.math.abs

class MotionManager(private val context: Context) {
    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private var lastShakeTime = Date()
    
    var acceleration by mutableStateOf(0.0f)
        private set
    var isActive by mutableStateOf(false)
        private set
    var isMoving by mutableStateOf(false)
        private set
    
    // Add configurable thresholds
    var motionThreshold: Float = 8f  // Default value
    var sensitivityMultiplier: Float = 150f  // Default value

    private val sensorListener = object : SensorEventListener {
        private var lastX = 0f
        private var lastY = 0f
        private var lastZ = 0f
        private var lastUpdate = System.currentTimeMillis()

        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                val now = System.currentTimeMillis()
                
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val deltaX = abs(lastX - x)
                val deltaY = abs(lastY - y)
                val deltaZ = abs(lastZ - z)

                // Calculate change in acceleration
                val deltaAccel = deltaX + deltaY + deltaZ

                // Map UI threshold (1-50) to actual threshold (0.3-7.1)
                val mappedThreshold = ((motionThreshold - 1f) / 49f) * 6.8f + 0.3f
                
                // Map UI sensitivity (50-200) to multiplier (0.3-3.3)
                val mappedSensitivity = ((sensitivityMultiplier - 50f) / 150f) * 3.0f + 0.3f

                Log.d("BikeBell", "Delta: $deltaAccel, Threshold: $mappedThreshold, Sensitivity: $mappedSensitivity")

                when {
                    deltaAccel > mappedThreshold -> {
                        // Apply sensitivity to the acceleration
                        acceleration = (deltaAccel - mappedThreshold) * mappedSensitivity
                        isMoving = true
                        Log.d("BikeBell", "Shake detected! Acceleration: $acceleration")
                    }
                    deltaAccel > (mappedThreshold * 0.3f) -> {
                        isMoving = true
                        acceleration = 0.0f
                        Log.d("BikeBell", "Movement detected")
                    }
                    else -> {
                        isMoving = false
                        acceleration = 0.0f
                        Log.d("BikeBell", "At rest")
                    }
                }

                lastX = x
                lastY = y
                lastZ = z
                lastUpdate = now
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // Not needed for this implementation
        }
    }

    fun updateSettings(threshold: Float, sensitivity: Float) {
        motionThreshold = threshold
        sensitivityMultiplier = sensitivity
        Log.d("BikeBell", "Settings updated - Threshold: $threshold, Sensitivity: $sensitivity")
    }

    fun startUpdates() {
        if (accelerometer != null) {
            sensorManager.registerListener(
                sensorListener,
                accelerometer,
                SensorManager.SENSOR_DELAY_GAME
            )
            isActive = true
            isMoving = false
            Log.d("BikeBell", "Motion detection started")
        } else {
            Log.e("BikeBell", "No accelerometer sensor found")
        }
    }

    fun stopUpdates() {
        sensorManager.unregisterListener(sensorListener)
        isActive = false
        isMoving = false
        acceleration = 0.0f
        Log.d("BikeBell", "Motion detection stopped")
    }
} 