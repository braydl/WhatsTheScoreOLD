/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.whatsthescore.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.ambient.AmbientLifecycleObserver
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.example.whatsthescore.R
import com.example.whatsthescore.presentation.theme.WhatsTheScoreTheme


val mAmbientCallback: AmbientLifecycleObserver.AmbientLifecycleCallback = object : AmbientLifecycleObserver.AmbientLifecycleCallback {
    override fun onEnterAmbient(ambientDetails: AmbientLifecycleObserver.AmbientDetails) {}
    override fun onUpdateAmbient() {}
    override fun onExitAmbient() {}
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        val ambientObserver = AmbientLifecycleObserver(this, mAmbientCallback)
        lifecycle.addObserver(ambientObserver)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    var game = GameDoublesTraditional()
    WhatsTheScoreTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            TimeText()
            Court(game)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Court(game : Game) {
    // state of the game
    var score by remember { mutableStateOf<String>(game.scoreToString()) }
    var serverTeam by remember { mutableStateOf<Team>(game.serverTeam()) }
    var serverSide by remember { mutableStateOf<Side>(game.serverSide()) }
    var isGameOver by remember { mutableStateOf<Boolean>(game.isGameOver()) }
    var isUndoAvailable by remember { mutableStateOf<Boolean>(game.isUndoAvailable()) }

    val haptics = LocalHapticFeedback.current
    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .wrapContentSize(Alignment.Center),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            enabled = isUndoAvailable,
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                game.undo()
                score = game.scoreToString()
                serverTeam = game.serverTeam()
                serverSide = game.serverSide()
                isGameOver = game.isGameOver()
                isUndoAvailable = game.isUndoAvailable()
            },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                ImageVector.vectorResource(id = R.drawable.undo_black_24dp),
                contentDescription = "Undo",
                tint = MaterialTheme.colors.secondary
                )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            modifier = Modifier
                //               .fillMaxWidth()
                .wrapContentSize(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // draw two rows of court sides with the score text in between
            for (row in 0..2) {
                if (row == 0 || row == 2) {
                    // draw a row of two court sides
                    Row {
                        var team = Team.OPPONENT;
                        var side = Side.EVEN;
                        for (col in 0..1) {
                            if (row == 0) {
                                team = Team.OPPONENT
                                side = if (col == 0) Side.EVEN else Side.ODD
                            } else {
                                team = Team.YOU
                                side = if (col == 1) Side.EVEN else Side.ODD
                            }
                            // draw a court side square
                            Box(
                                modifier = Modifier
                                    .clip(RectangleShape)
                                    .border(1.dp, MaterialTheme.colors.primary, RectangleShape)
                                    .size(65.dp)
                                    .clickable(
                                        enabled = !isGameOver
                                    ) {
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        if (serverTeam == Team.NONE) {
                                            game.whoServesFirst(team)
                                        } else {
                                            game.rallyWonBy(team)
                                        }
                                        score = game.scoreToString()
                                        serverTeam = game.serverTeam()
                                        serverSide = game.serverSide()
                                        isGameOver = game.isGameOver()
                                        isUndoAvailable = game.isUndoAvailable()
                                    },
                                contentAlignment = Alignment.Center,
                            ) {
                                // draw the ball
                                if (!isGameOver && team == serverTeam && side == serverSide) {
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .border(1.dp, Color.Yellow, CircleShape)
                                            .size(40.dp),
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        textAlign = TextAlign.Center,
                        text = score,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .combinedClickable(
                                onLongClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    game.resetGame()
                                    score = game.scoreToString()
                                    serverTeam = game.serverTeam()
                                    serverSide = game.serverSide()
                                    isGameOver = game.isGameOver()
                                    isUndoAvailable = game.isUndoAvailable()
                                },
                                onClick = { /*....*/ })
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        IconButton(
            enabled = false,
            onClick = { /* ... */ },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                Icons.Filled.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colors.secondary
            )
        }
    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp()
}
