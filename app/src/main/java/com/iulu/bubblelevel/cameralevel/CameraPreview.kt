/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * copyright 2024, Purcel Iulian
 */

package com.iulu.bubblelevel.cameralevel

import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import java.util.concurrent.Executor

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    executor: Executor = Dispatchers.Default.asExecutor(),
    imageLuminosityListener: (Double) -> Unit = {}
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentContext = LocalContext.current
    val cameraController = remember {
        LifecycleCameraController(currentContext)
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            PreviewView(context).apply {
                this.scaleType = scaleType
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }.also { previewView ->
                previewView.controller = cameraController
                cameraController.apply {
                    setImageAnalysisAnalyzer(executor, analyzeImageLuminance {
                        imageLuminosityListener(it)
                    })
                    bindToLifecycle(lifecycleOwner)
                }.cameraSelector = cameraSelector
            }
        }
    )
}

private fun analyzeImageLuminance(
    readingWindowWidth: Int = 140,
    readingWindowHeight: Int = 140,
    luminanceListener: (Double) -> Unit
): ImageAnalysis.Analyzer {
    val pixelsNr = readingWindowWidth * readingWindowHeight
    val pixels = IntArray(pixelsNr)
    return ImageAnalysis.Analyzer { imageProxy ->
        val xCenter = imageProxy.width / 2
        val yCenter = imageProxy.height / 2

        imageProxy.toBitmap().getPixels(
            pixels,
            0,
            readingWindowWidth,
            xCenter - readingWindowWidth / 2,
            yCenter - readingWindowHeight / 2,
            readingWindowWidth,
            readingWindowHeight
        )
        val averageLuminance = pixels.sumOf { it.toColor().luminance().toDouble() } / pixelsNr

        luminanceListener(averageLuminance)

        imageProxy.close()
    }
}