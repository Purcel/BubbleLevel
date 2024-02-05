/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * copyright 2024, Purcel Iulian
 */

package com.iulu.bubblelevel.bubblelevel

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import kotlin.math.ln
import kotlin.math.sign

@Composable
fun BobbleLevel(
    modifier: Modifier = Modifier,
    gravityX: Float,
    gravityY: Float,
    gravityZ: Float,
    backgroundColor: Color,
    signsColor: Color,
    textStyle: TextStyle = BubbleTypography.copy(color = signsColor),
    backgroundCornerRadius: CornerRadius = BubbleLevelCornersRadius
) {
    val textMeasurer = rememberTextMeasurer()

    val lineVisibilityAngle = 45f
    val degSymbol = "°"
    val textAscentPercentage = 0.20f /*I Put the text ascent to be 20% fo the text height.*/
    val textToMiddleCircleSignDistance = 16f
    val lateralLinesLength = 50f
    val lineStrokeWidth = 4f
    val bobbleDiameter = 120f
    val bobbleSensibility = 3f

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

    val alphaMiddle: State<Float> =
        animateFloatAsState(
            targetValue = if (isCloseToFlatOnTheTable) 1f else 0f,
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
            label = "AlphaMiddleCircle"
        )
    val alphaLeft: State<Float> =
        animateFloatAsState(
            targetValue = if (isRightLandscape) 1f else 0f,
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
            label = "AlphaLeftLines"
        )
    val alphaRight: State<Float> =
        animateFloatAsState(
            targetValue = if (isLeftLandscape) 1f else 0f,
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
            label = "AlphaRightLines"
        )
    val alphaTop: State<Float> =
        animateFloatAsState(
            targetValue = if (isPortrait) 1f else 0f,
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
            label = "AlphaTopLines"
        )
    val alphaBottom: State<Float> =
        animateFloatAsState(
            targetValue = if (isUpSideDownPortrait) 1f else 0f,
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
            label = "AlphaBottomLines"
        )

    Canvas(modifier = modifier.size(300.dp, 300.dp)) {
        layoutDirection.ordinal
        val rectWidth = size.width
        val rectHeight = size.height
        val rectXPos = 0f
        val rectYPos = 0f

        val scaleX = (rectWidth / 2f - bobbleDiameter) / bobbleSensibility
        val scaleY = (rectHeight / 2f - bobbleDiameter) / bobbleSensibility
        var x = gravityX * scaleX
        var y = gravityY * scaleY

        var bobbleSqueezeR = 0f
        var bobbleSqueezeL = 0f
        var bobbleSqueezeT = 0f
        var bobbleSqueezeB = 0f

        if (x >= bobbleSensibility * scaleX) {
            bobbleSqueezeR = bobbleSqueezeEc(gravityX, bobbleSensibility, bobbleDiameter)
            x = bobbleSensibility * scaleX + bobbleSqueezeR / 2f
        } else if (x <= -bobbleSensibility * scaleX) {
            bobbleSqueezeL = -bobbleSqueezeEc(-gravityX, bobbleSensibility, bobbleDiameter)
            x = -bobbleSensibility * scaleX + bobbleSqueezeL / 2f
        }

        if (y >= bobbleSensibility * scaleY) {
            bobbleSqueezeT = bobbleSqueezeEc(gravityY, bobbleSensibility, bobbleDiameter)
            y = bobbleSensibility * scaleY + bobbleSqueezeT / 2f
        } else if (y <= -bobbleSensibility * scaleY) {
            bobbleSqueezeB = -bobbleSqueezeEc(-gravityY, bobbleSensibility, bobbleDiameter)
            y = -bobbleSensibility * scaleY + bobbleSqueezeB / 2f
        }

        val path = Path().apply {
            addOval(
                Rect(
                    Offset(
                        rectXPos + rectWidth / 2f - bobbleDiameter + x + bobbleSqueezeR,
                        rectYPos + rectHeight / 2f - bobbleDiameter - y - bobbleSqueezeB
                    ),
                    Offset(
                        rectXPos + rectWidth / 2f + bobbleDiameter + x + bobbleSqueezeL,
                        rectYPos + rectHeight / 2f + bobbleDiameter - y - bobbleSqueezeT
                    )
                )
            )
        }

        clipPath(path, clipOp = ClipOp.Difference) {
            drawRoundRect(
                backgroundColor,
                Offset(rectXPos, rectYPos),
                Size(rectWidth, rectHeight),
                cornerRadius = backgroundCornerRadius
            )
        }

        val lineDistance = bobbleDiameter + 12f
        val centerCircleDiameter = bobbleDiameter + 8f
        drawLine(
            signsColor,
            Offset(rectXPos, rectYPos + rectHeight / 2f - lineDistance),
            Offset(rectXPos + lateralLinesLength, rectYPos + rectHeight / 2f - lineDistance),
            lineStrokeWidth,
            alpha = alphaLeft.value
        )
        drawLine(
            signsColor,
            Offset(rectXPos, rectYPos + rectHeight / 2f + lineDistance),
            Offset(rectXPos + lateralLinesLength, rectYPos + rectHeight / 2f + lineDistance),
            lineStrokeWidth,
            alpha = alphaLeft.value
        )
        drawLine(
            signsColor,
            Offset(
                rectXPos + rectWidth - lateralLinesLength,
                rectYPos + rectHeight / 2f - lineDistance
            ),
            Offset(rectXPos + rectWidth, rectYPos + rectHeight / 2f - lineDistance),
            lineStrokeWidth,
            alpha = alphaRight.value
        )
        drawLine(
            signsColor,
            Offset(
                rectXPos + rectWidth - lateralLinesLength,
                rectYPos + rectHeight / 2f + lineDistance
            ),
            Offset(rectXPos + rectWidth, rectYPos + rectHeight / 2f + lineDistance),
            lineStrokeWidth,
            alpha = alphaRight.value
        )

        drawLine(
            signsColor,
            Offset(rectXPos + rectWidth / 2f - lineDistance, rectYPos),
            Offset(rectXPos + rectWidth / 2f - lineDistance, rectYPos + lateralLinesLength),
            lineStrokeWidth,
            alpha = alphaTop.value
        )
        drawLine(
            signsColor,
            Offset(rectXPos + rectWidth / 2f + lineDistance, rectYPos),
            Offset(rectXPos + rectWidth / 2f + lineDistance, rectYPos + lateralLinesLength),
            lineStrokeWidth,
            alpha = alphaTop.value
        )
        drawLine(
            signsColor,
            Offset(
                rectXPos + rectWidth / 2f - lineDistance,
                rectYPos + rectHeight - lateralLinesLength
            ),
            Offset(rectXPos + rectWidth / 2f - lineDistance, rectYPos + rectHeight),
            lineStrokeWidth,
            alpha = alphaBottom.value
        )
        drawLine(
            signsColor,
            Offset(
                rectXPos + rectWidth / 2f + lineDistance,
                rectYPos + rectHeight - lateralLinesLength
            ),
            Offset(rectXPos + rectWidth / 2f + lineDistance, rectYPos + rectHeight),
            lineStrokeWidth,
            alpha = alphaBottom.value
        )
        drawCircle(
            signsColor,
            centerCircleDiameter,
            Offset(rectXPos + rectWidth / 2f, rectYPos + rectHeight / 2f),
            style = Stroke(width = lineStrokeWidth),
            alpha = alphaMiddle.value
        )

        if (!isCloseToFlatOnTheTable) {
            val textAngle: String = when {
                isPortrait -> portraitAngle.toString()
                isUpSideDownPortrait -> portraitUpSideDown.toString()
                isLeftLandscape -> landscapeLeftAngle.toString()
                isRightLandscape -> landscapeRightAngle.toString()
                else -> lineVisibilityAngle.toString()
            } + degSymbol
            val textLayoutResult = textMeasurer.measure(text = textAngle, style = textStyle)
            val textHeight = textLayoutResult.size.height
            val textWidth = textLayoutResult.size.width
            rotate(portraitAngle * portraitAngleSign) {
                drawText(
                    textMeasurer = textMeasurer,
                    text = textAngle,
                    topLeft = Offset(
                        rectXPos + (rectWidth - textWidth) / 2f,
                        rectYPos + (rectHeight - textHeight) / 2f
                    ),
                    style = textStyle.copy(color = signsColor),
                )
            }
        } else {
            val textAngleX: String = flatOnTheTableAngleX.toString() + degSymbol
            val textHeight =
                textMeasurer.measure(text = textAngleX, style = textStyle).size.height
            val textAscent =
                textHeight * textAscentPercentage
            drawText(
                textMeasurer = textMeasurer,
                text = textAngleX,
                topLeft = Offset(
                    rectXPos + rectWidth / 2f + centerCircleDiameter + textToMiddleCircleSignDistance,
                    rectYPos + (rectHeight - textHeight) / 2f
                ),
                style = textStyle.copy(color = signsColor)
            )
            val textAngleY = flatOnTheTableAngleY.toString() + degSymbol
            val textWidth =
                textMeasurer.measure(text = textAngleY, style = textStyle).size.width
            drawText(
                textMeasurer = textMeasurer,
                text = textAngleY,
                topLeft = Offset(
                    rectXPos + (rectWidth - textWidth) / 2f,
                    rectYPos + rectHeight / 2f + centerCircleDiameter - textAscent + textToMiddleCircleSignDistance
                ),
                style = textStyle.copy(color = signsColor)
            )
        }
    }
}

@Suppress("SameParameterValue")
private fun bobbleSqueezeEc(gravity: Float, sensibility: Float, bobbleWidth: Float): Float =
    ((ln(gravity - (sensibility - 1f)) + sensibility) * bobbleWidth / 2f - sensibility * bobbleWidth / 2f)