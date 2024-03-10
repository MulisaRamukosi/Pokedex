package com.core.example.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.core.example.ui.R
import com.core.example.ui.theme.PokedexTheme

@Composable
fun ErrorFailedView(
    modifier: Modifier,
    message: String?,
    isLoading: Boolean,
    onClickTryAgain: () -> Unit
) {

    Surface(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier.padding(all = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier
                        .widthIn(max = 400.dp)
                        .fillMaxWidth(), painter = painterResource(
                        id = R.drawable.sad_pikachu
                    ),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.height(height = 8.dp))

                Text(text = message ?: stringResource(id = R.string.error_unknown), textAlign = TextAlign.Center)


                Spacer(modifier = Modifier.height(height = 16.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onClickTryAgain,
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator()
                    } else {
                        Text(text = stringResource(id = R.string.try_again))
                    }
                }
            }
        }
    }


}

@Preview
@Composable
private fun PreviewErrorFailedView(){
    PokedexTheme {
        ErrorFailedView(
            modifier = Modifier.fillMaxSize(),
            message = "Some error occurred",
            isLoading = false
        ) {

        }
    }
}