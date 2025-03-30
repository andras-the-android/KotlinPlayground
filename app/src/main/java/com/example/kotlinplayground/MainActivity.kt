package com.example.kotlinplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.example.kotlinplayground.ui.theme.KotlinPlaygroundTheme
import com.example.playground.CoroutinesExceptions

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KotlinPlaygroundTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
        CoroutinesExceptions().exception()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Hello ")
            }
            append("$name!")
        },
        modifier = modifier,
    )
}

data class Callbacks(
    val onClick: () -> Unit
)

@Composable
fun whatever1(callbacks: Callbacks) {
    Box() {
        TextButton(onClick = callbacks.onClick, content =  { Text("hello") }, modifier = Modifier.matchParentSize())
    }
}

@Composable
fun whatever1(onClick: () -> Unit) {
    TextButton(onClick = onClick, content =  { Text("hello") })
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KotlinPlaygroundTheme {
        Greeting("Android")
    }
}
