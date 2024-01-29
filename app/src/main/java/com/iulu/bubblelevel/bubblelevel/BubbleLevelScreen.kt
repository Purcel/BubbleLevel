/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * copyright 2024, Purcel Iulian
 */

package com.iulu.bubblelevel.bubblelevel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.iulu.bubblelevel.R
import com.iulu.bubblelevel.ui.theme.BubbleLevelTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BubbleLevelScreen(
    modifier: Modifier = Modifier,
    accelerometerCoordinate: Coordinate,
    fabClickListener: () -> Unit,
    backgroundColor: Color = MaterialTheme.colorScheme.background
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = fabClickListener, modifier = modifier
            ) {
                val painter = painterResource(id = R.drawable.ic_camera_level_screen)
                Icon(modifier = Modifier, painter = painter, contentDescription = "")
            }
        }) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(top = it.calculateTopPadding())
        ) {
            BobbleLevel(
                modifier = modifier.align(Alignment.Center),
                backgroundColor = MaterialTheme.colorScheme.primary,
                signsColor = MaterialTheme.colorScheme.onPrimary,
                gravityX = accelerometerCoordinate.x,
                gravityY = accelerometerCoordinate.y,
                gravityZ = accelerometerCoordinate.z
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val accelerometer = Coordinate(x = 0.5f, y = 9.8f, z = 0f)
    BubbleLevelTheme {
        BubbleLevelScreen(accelerometerCoordinate = accelerometer, fabClickListener = {})
    }
}