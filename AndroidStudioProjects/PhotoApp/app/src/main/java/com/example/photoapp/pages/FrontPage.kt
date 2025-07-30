package com.example.photoapp.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.photoapp.R
import com.example.photoapp.ui.theme.Blue
import com.example.photoapp.ui.theme.White

@Preview
@Composable
fun FrontPage_Preview() {
    // Preview function to visualize the UI in Android Studio's preview window
    FrontPage(onOpenClick = {})
}

@Composable
fun FrontPage(onOpenClick: () -> Unit) {
    // Top-level container using Box to center the content
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(White), // Background color of the screen
        contentAlignment = Alignment.Center
    ) {
        // Main vertical layout
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, // Center items horizontally
            verticalArrangement = Arrangement.Center, // Center items vertically
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp) // Outer padding
        ) {

            // Title text "photos"
            Text(
                text = "photos",
                color = Blue, // Make sure Blue is defined, e.g., Color(0xFF1A73E8)
                fontSize = 40.sp,
                fontWeight = FontWeight.Medium
            )

            // Spacer to separate title from logo
            Spacer(modifier = Modifier.height(140.dp))

            // Image (Logo) in the center
            Image(
                painter = painterResource(id = R.drawable.photologo), // Replace with your logo resource
                contentDescription = "Photo Logo",
                modifier = Modifier.size(160.dp)
            )

            // Spacer to separate logo from the button
            Spacer(modifier = Modifier.height(140.dp))

            // OPEN Button
            Button(
                onClick = onOpenClick, // Executes callback when clicked (e.g., navigate to Gallery)
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White, // White background
                    contentColor = Color.Black // Black text
                ),
                border = BorderStroke(4.dp, Color.Black), // Bold border
                shape = RoundedCornerShape(6.dp), // Rounded edges
                modifier = Modifier
                    .height(48.dp)
                    .width(150.dp)
            ) {
                // Text inside the button
                Text(
                    text = "OPEN",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}