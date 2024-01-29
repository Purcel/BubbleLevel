/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * copyright 2024, Purcel Iulian
 */

package com.iulu.bubblelevel

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.iulu.bubblelevel.bubblelevel.Coordinate
import com.iulu.bubblelevel.bubblelevel.MutableCoordinate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BubbleLevelViewModel(context: Application) : AndroidViewModel(context) {

    private val _accelerometer = MutableStateFlow(Coordinate(0f, 0f, 0f))
    val accelerometer = _accelerometer.asStateFlow()

    init {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val gravity = MutableCoordinate(0f, 0f, 0f)
        sensorManager.registerListener(object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val alpha = .97f //Low-pass filter constant
                gravity.x = (alpha * gravity.x + (1 - alpha) * event.values[0])
                gravity.y = (alpha * gravity.y + (1 - alpha) * event.values[1])
                gravity.z = (alpha * gravity.z + (1 - alpha) * event.values[2])

                viewModelScope.launch {
                    _accelerometer.emit(gravity.toCoordinate())
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }, accelerometer, SensorManager.SENSOR_DELAY_GAME)
    }
}