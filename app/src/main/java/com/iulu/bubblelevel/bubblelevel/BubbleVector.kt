/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * copyright 2024, Purcel Iulian
 */

package com.iulu.bubblelevel.bubblelevel

import kotlin.math.acos
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sqrt

data class BubbleVector(var x: Float, var y: Float) {
    //val xSign: Float get() = x.sign
    val ySign: Float get() = y.sign
    val length: Float get() = sqrt(x.pow(2) + y.pow(2))
    fun dotProduct(v2: BubbleVector) = (x * v2.x + y * v2.y)
    fun angle(v2: BubbleVector): Float = acos(dotProduct(v2) / (this.length * v2.length))
}