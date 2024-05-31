package com.example.teachme.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.teachme.R
import com.example.teachme.ui.theme.TeachMeTheme

class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TeachMeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AboutScreen()
                }
            }
        }
    }
}

@Composable
fun AboutScreen() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = "O Autorach", style = MaterialTheme.typography.headlineSmall)
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = "Logo WSEI",
            modifier = Modifier.size(100.dp).padding(vertical = 16.dp)
        )
        Text(text = "Informacje o autorach aplikacji...")
    }
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    TeachMeTheme {
        AboutScreen()
    }
}
