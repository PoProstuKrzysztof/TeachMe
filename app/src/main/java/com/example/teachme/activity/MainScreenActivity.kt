package com.example.teachme.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.teachme.R
import com.example.teachme.screen.AboutActivity
import com.example.teachme.ui.theme.TeachMeTheme

class MainScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TeachMeTheme {
                MainScreen(
                    onStartQuiz = { startActivity(Intent(this, LessonSelectionActivity::class.java)) },
                    onSettings = { startActivity(Intent(this, SettingsActivity::class.java)) },
                    onAbout = { startActivity(Intent(this, AboutActivity::class.java)) }
                )
            }
        }
    }
}

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val fontName = GoogleFont("Lexend")

val fontFamily = FontFamily(Font(googleFont = fontName, fontProvider = provider))

@Composable
fun MainScreen(
    onStartQuiz: () -> Unit,
    onSettings: () -> Unit,
    onAbout: () -> Unit
) {

    val infiniteTransition = rememberInfiniteTransition()
    val buttonSize by infiniteTransition.animateValue(
        initialValue = 200.dp,
        targetValue = 215.dp,
        typeConverter = Dp.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(listOf(Color(0xFF6200EE), Color(0xFFBB86FC)))
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.TopEnd),
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Ustawienia",
                modifier = Modifier
                    .size(42.dp)
                    .clickable(onClick = onSettings)
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Button(
                onClick = onStartQuiz,
                modifier = Modifier
                    .size(buttonSize)
                    .clip(CircleShape),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(
                    text = "Zacznij naukę!",
                    color = Color.Black,
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Button(onClick = onAbout) {
                Text(text = "O Aplikacji")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    TeachMeTheme {
        MainScreen({}, {}, {})
    }
}
