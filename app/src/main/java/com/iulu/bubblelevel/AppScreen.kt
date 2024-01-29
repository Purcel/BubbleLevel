/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * copyright 2024, Purcel Iulian
 */

package com.iulu.bubblelevel

import android.Manifest
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.iulu.bubblelevel.bubblelevel.BubbleLevelScreen
import com.iulu.bubblelevel.cameralevel.CameraLevelScreen

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(
    modifier: Modifier = Modifier,
    viewModel: BubbleLevelViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    navigationController: NavHostController = rememberNavController()
) {
    var showPermissionRationale by rememberSaveable { mutableStateOf(false) }
    val cameraPermissionState =
        rememberPermissionState(permission = Manifest.permission.CAMERA, onPermissionResult = {
            if (it) {
                showPermissionRationale = false
                navigationController.navigate(ROUTE_CAMERA)
            } else showPermissionRationale = false
        })

    Scaffold(modifier = modifier, topBar = {
        TopAppBar(modifier = modifier)
    }) {
        val acceleration by viewModel.accelerometer.collectAsState()

        Box(modifier = modifier.padding(top = it.calculateTopPadding())) {
            NavHost(
                navController = navigationController,
                startDestination = ROUTE_BUBBLE,
                builder = {
                    composable(
                        route = ROUTE_BUBBLE,
                    ) {
                        BubbleLevelScreen(
                            modifier = modifier,
                            accelerometerCoordinate = acceleration,
                            fabClickListener = {
                                if (cameraPermissionState.status.isGranted)
                                    navigationController.navigate(ROUTE_CAMERA)
                                else if (cameraPermissionState.status.shouldShowRationale)
                                    showPermissionRationale = true
                                else
                                    cameraPermissionState.launchPermissionRequest()

                            })
                        if (showPermissionRationale) {
                            CameraPermissionRationaleScreen(
                                onGrant = { cameraPermissionState.launchPermissionRequest() },
                                onCancel = { showPermissionRationale = false }
                            )
                        }
                    }
                    composable(
                        route = ROUTE_CAMERA,
                        enterTransition = { EnterTransition.None },
                        exitTransition = { ExitTransition.None },
                        popEnterTransition = { EnterTransition.None },
                        popExitTransition = { ExitTransition.None }) {
                        CameraLevelScreen(
                            modifier = modifier,
                            accelerometerCoordinate = acceleration,
                            fabClickListener = { navigationController.popBackStack() }
                        )

                    }
                })

        }
    }
}

@Composable
private fun CameraPermissionRationaleScreen(
    modifier: Modifier = Modifier,
    onGrant: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        properties = DialogProperties(decorFitsSystemWindows = false),
        onDismissRequest = onCancel,
        title = { Text(text = stringResource(R.string.string_permission_rationale_title)) },
        text = {
            Text(
                text = stringResource(R.string.string_permission_rationale_description)
            )
        },
        confirmButton = {
            Button(onClick = onGrant) {
                Text(text = stringResource(R.string.string_permission_grant_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(text = stringResource(R.string.string_permission_rationale_close_button))
            }
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(modifier: Modifier = Modifier) {
    var showMenu by remember { mutableStateOf(false) }
    TopAppBar(
        modifier = modifier,
        title = { Text(text = stringResource(id = R.string.app_name)) },
        actions = {
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = stringResource(R.string.string_button_more)
                )
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.string_button_buy_me_a_coffee)) },
                    onClick = { /*Open an activity*/ })
                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.string_button_about)) },
                    onClick = { /*Open an activity*/ })
            }

        }
    )
}

private const val ROUTE_BUBBLE = "A"
private const val ROUTE_CAMERA = "B"