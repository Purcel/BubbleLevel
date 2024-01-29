/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * copyright 2024, Purcel Iulian
 */

package com.iulu.bubblelevel.bubblelevel

import kotlin.math.pow
import kotlin.math.round

fun Float.toDeg(): Float = this * (180 / Math.PI).toFloat()

fun Float.truncate(decimals: UInt): Float {
    return round(this * 10f.pow(decimals.toInt())) / (10f.pow(decimals.toInt()))
}

fun computePortraitAngle(gravityX: Float, gravityY: Float): Float =
    computeAngle(gravityX, gravityY, 0f, 1f)

fun computePortraitUpSideDown(gravityX: Float, gravityY: Float): Float =
    computeAngle(gravityX, gravityY, 0f, -1f)


fun computeLandscapeLeftAngle(gravityX: Float, gravityY: Float): Float =
    computeAngle(gravityX, gravityY, 1f, 0f)

fun computeLandscapeRightAngle(gravityX: Float, gravityY: Float): Float =
    computeAngle(gravityX, gravityY, -1f, 0f)

fun computeFlatOnTableAngleX(gravityY: Float, gravityZ: Float): Float =
    computeAngle(gravityY, gravityZ, 0f, 1f)

fun computeFlatOnTableAngleY(gravityX: Float, gravityZ: Float): Float =
    computeAngle(gravityX, gravityZ, 0f, 1f)

fun computeAngle(vector11: Float, vector12: Float, vector21: Float, vector22: Float) =
    BubbleVector(vector11, vector12).run {
        ySign
        angle(BubbleVector(vector21, vector22))
            .toDeg()
            .truncate(1u)
    }