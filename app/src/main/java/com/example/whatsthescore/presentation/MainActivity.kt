/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.whatsthescore.presentation

import android.os.Bundle
import android.util.Log
import android.view.Surface
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.example.whatsthescore.R
import com.example.whatsthescore.presentation.theme.WhatsTheScoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

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

@Composable
fun CourtSide(
    game: Game,
    team: Team,
    side: Side
) {
    Box(
        modifier = Modifier
            .clip(RectangleShape)
            .border(1.dp, MaterialTheme.colors.primary, RectangleShape)
            .size(40.dp)
            .clickable {
                Log.d("Main", "clicked $team $side")
            },

        contentAlignment = Alignment.Center,
    ) {
        // draw the ball
        if (team == Team.YOU && side == Side.EVEN) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .border(1.dp, Color.Yellow, CircleShape)
                    .size(30.dp),
            )
        }
    }
}

@Composable
fun Court(game : Game) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
 //       var score by remember { mutableStateOf("") }
        var score = game.scoreToString()
        for (row in 0..2) {
            if (row == 0 || row == 2) {
                Row {
                    var team = Team.OPPONENT;
                    var side = Side.EVEN;
                    for (col in 0 .. 1) {
                        if (row == 0)
                        {
                            team = Team.OPPONENT
                            side = if (col == 0) Side.EVEN else Side.ODD
                        }
                        else
                        {
                            team = Team.YOU
                            side = if (col == 1) Side.EVEN else Side.ODD
                        }
                        CourtSide(game = game, team = team, side = side)
                    }
                }
            }
            else {
                Text(textAlign = TextAlign.Center, text = score)
            }
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp()
}