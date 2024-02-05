/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * copyright 2024, Purcel Iulian
 */

package com.iulu.bubblelevel.bubblelevel

data class Coordinate(val x: Float, val y: Float, val z: Float)

data class MutableCoordinate(var x: Float, var y: Float, var z: Float) {
    fun toCoordinate() =
        Coordinate(x, y, z)
}