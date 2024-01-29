/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * copyright 2024, Purcel Iulian
 */

package com.iulu.bubblelevel.cameralevel

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.iulu.bubblelevel.R
import com.iulu.bubblelevel.bubblelevel.Coordinate
import com.iulu.bubblelevel.ui.theme.BubbleLevelTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraLevelScreen(
    modifier: Modifier = Modifier,
    accelerometerCoordinate: Coordinate,
    fabClickListener: () -> Unit
) {
    var luminance by remember {
        mutableDoubleStateOf(0.0)
    }

    val signsColorChangeAnim: State<Float> = animateFloatAsState(
        targetValue = if (luminance >= 0.3) 0f else 1f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "SignsColorChangeAnim"
    )

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = fabClickListener, modifier = modifier
            ) {
                val painter = painterResource(id = R.drawable.ic_bubble_level_screen)
                Icon(modifier = Modifier, painter = painter, contentDescription = "")
            }
        }) { paddingValues ->

        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            CameraPreview(
                modifier = modifier.align(Alignment.BottomCenter),
                imageLuminosityListener = {
                    luminance = it
                })
            CameraLevelLine(
                modifier = modifier.align(Alignment.BottomCenter),
                gravityX = accelerometerCoordinate.x,
                gravityY = accelerometerCoordinate.y,
                gravityZ = accelerometerCoordinate.z,
                signsColor = Color(
                    signsColorChangeAnim.value,
                    signsColorChangeAnim.value,
                    signsColorChangeAnim.value
                )
            )

        }
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val accelerometer = Coordinate(x = 0.5f, y = 9.8f, z = 0f)
    BubbleLevelTheme {
        CameraLevelScreen(accelerometerCoordinate = accelerometer, fabClickListener = {})
    }
}