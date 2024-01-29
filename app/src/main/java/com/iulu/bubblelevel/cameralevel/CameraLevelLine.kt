/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * copyright 2024, Purcel Iulian
 */

package com.iulu.bubblelevel.cameralevel

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import com.iulu.bubblelevel.bubblelevel.BubbleTypography
import com.iulu.bubblelevel.bubblelevel.computeFlatOnTableAngleX
import com.iulu.bubblelevel.bubblelevel.computeFlatOnTableAngleY
import com.iulu.bubblelevel.bubblelevel.computeLandscapeLeftAngle
import com.iulu.bubblelevel.bubblelevel.computeLandscapeRightAngle
import com.iulu.bubblelevel.bubblelevel.computePortraitAngle
import com.iulu.bubblelevel.bubblelevel.computePortraitUpSideDown
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sqrt

@Composable
fun CameraLevelLine(
    modifier: Modifier = Modifier,
    gravityX: Float,
    gravityY: Float,
    gravityZ: Float,
    signsColor: Color = Color.LightGray,
    textStyle: TextStyle = BubbleTypography.copy(color = signsColor)
) {
    val textMeasurer = rememberTextMeasurer()

    val degSymbol = "°"
    val lineVisibilityAngle = 45f
    val bobbleSensibility = 6.89f

    val flatOnTheTableAngleX = computeFlatOnTableAngleX(gravityX, gravityZ)
    val flatOnTheTableAngleY = computeFlatOnTableAngleY(gravityY, gravityZ)

    val portraitAngle = computePortraitAngle(gravityX, gravityY)
    val portraitAngleSign = gravityX.sign
    val portraitUpSideDown = computePortraitUpSideDown(gravityX, gravityY)
    val landscapeLeftAngle = computeLandscapeLeftAngle(gravityX, gravityY)
    val landscapeRightAngle = computeLandscapeRightAngle(gravityX, gravityY)

    val isCloseToFlatOnTheTable =
        flatOnTheTableAngleX < lineVisibilityAngle && flatOnTheTableAngleY < lineVisibilityAngle
    val isPortrait = portraitAngle < lineVisibilityAngle && !isCloseToFlatOnTheTable
    val isUpSideDownPortrait = portraitUpSideDown < lineVisibilityAngle && !isCloseToFlatOnTheTable
    val isRightLandscape = landscapeRightAngle < lineVisibilityAngle && !isCloseToFlatOnTheTable
    val isLeftLandscape = landscapeLeftAngle < lineVisibilityAngle && !isCloseToFlatOnTheTable
    val isLandscape = isRightLandscape || isLeftLandscape

    val angle: Float = when {
        isPortrait -> portraitAngle
        isUpSideDownPortrait -> portraitUpSideDown
        isLeftLandscape -> landscapeLeftAngle
        isRightLandscape -> landscapeRightAngle
        else -> lineVisibilityAngle
    }
    val textAngle = "$angle$degSymbol"

    val lateralLineStroke = animateFloatAsState(
        targetValue = if (angle in 0.0f..1.0f) {
            24f
        } else 6f, label = "LateralLinesAnimation"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val minSize = minOf(size.width, size.height)
        val circleRadius = minSize * 0.1f
        val circlesDistance = lateralLineStroke.value + 8f
        val scaleX = (size.width / 2f - circleRadius) / bobbleSensibility
        val scaleY = (size.height / 2f - circleRadius) / bobbleSensibility
        val x = gravityX * scaleX
        val y = gravityY * scaleY

        if (isCloseToFlatOnTheTable) {
            drawCircle(
                signsColor,
                circleRadius,
                Offset(size.width / 2 + x, size.height / 2 - y),
                style = Stroke(
                    width = lateralLineStroke.value
                )
            )
            drawCircle(
                signsColor,
                circleRadius + circlesDistance,
                Offset(size.width / 2, size.height / 2),
                style = Stroke(
                    width = lateralLineStroke.value
                )
            )

        } else {

            rotate(degrees = portraitAngle * portraitAngleSign) {
                val lineHalfLength = sqrt((size.height / 2f).pow(2) + (size.width / 2f).pow(2))
                val lineLeft = Offset(-lineHalfLength + size.width / 2f, size.height / 2f)
                val lineRight = Offset(lineHalfLength + size.width / 2f, size.height / 2f)
                drawLine(color = signsColor, lineLeft, lineRight, strokeWidth = 6f)
                val textLength = textMeasurer.measure(text = textAngle, textStyle).size.width
                drawText(
                    textMeasurer = textMeasurer,
                    textAngle,
                    topLeft = Offset(size.width / 2f - textLength / 2f, size.height / 2f),
                    style = textStyle
                )
            }
            if (isPortrait || isUpSideDownPortrait) {
                drawLine(
                    color = signsColor,
                    Offset(0f, size.height / 2f),
                    Offset(100f + lateralLineStroke.value, size.height / 2f),
                    strokeWidth = lateralLineStroke.value,
                    blendMode = BlendMode.Xor
                )
                rotate(180f) {
                    drawLine(
                        color = signsColor,
                        Offset(0f, size.height / 2f),
                        Offset(100f + lateralLineStroke.value, size.height / 2f),
                        strokeWidth = lateralLineStroke.value,
                        blendMode = BlendMode.Xor
                    )
                }
            }
            if (isLandscape) {
                drawLine(
                    color = signsColor,
                    Offset(size.width / 2f, 0f),
                    Offset(size.width / 2f, 100f + lateralLineStroke.value),
                    strokeWidth = lateralLineStroke.value,
                    blendMode = BlendMode.Xor
                )
                rotate(180f) {
                    drawLine(
                        color = signsColor,
                        Offset(size.width / 2f, 0f),
                        Offset(size.width / 2f, 100f + lateralLineStroke.value),
                        strokeWidth = lateralLineStroke.value,
                        blendMode = BlendMode.Xor
                    )
                }
            }

        }
    }
}