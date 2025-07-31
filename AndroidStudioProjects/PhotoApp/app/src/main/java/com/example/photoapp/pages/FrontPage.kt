package com.example.photoapp.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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

/**
 * Preview function used for Android Studio’s Preview pane.
 * Helps developers visualize the layout of FrontPage without launching the app.
 */
@Preview
@Composable
fun FrontPage_Preview() {
    FrontPage(onOpenClick = {}) // Pass an empty lambda for preview
}

/**
 * This composable represents the main front screen of the app.
 * It shows a title, logo, and an OPEN button that navigates to the gallery.
 *
 * @param onOpenClick Callback executed when the OPEN button is clicked (navigation logic is passed from MainActivity)
 */
@Composable
fun FrontPage(onOpenClick: () -> Unit) {

    // Root container with centered alignment and white background
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(White), // White background (custom color from theme)
        contentAlignment = Alignment.Center
    ) {

        // Vertical column layout to arrange title, image, and button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, // Center elements horizontally
            verticalArrangement = Arrangement.Center, // Center elements vertically in the screen
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp) // Add padding around the screen
        ) {

            // App title text
            Text(
                text = "photos", // Title
                color = Blue, // Title color (custom blue from theme)
                fontSize = 40.sp, // Large text size
                fontWeight = FontWeight.Medium // Medium weight
            )

            Spacer(modifier = Modifier.height(140.dp)) // Space between title and image

            // Center logo image
            Image(
                painter = painterResource(id = R.drawable.photologo), // Your logo resource
                contentDescription = "Photo Logo",
                modifier = Modifier.size(160.dp) // Square image size
            )

            Spacer(modifier = Modifier.height(140.dp)) // Space between image and button

            // OPEN button to go to the gallery
            Button(
                onClick = onOpenClick, // Navigate to gallery when clicked
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White, // White background for button
                    contentColor = Color.Black // Black text color
                ),
                border = BorderStroke(4.dp, Color.Black), // Bold black border
                shape = RoundedCornerShape(6.dp), // Slightly rounded corners
                modifier = Modifier
                    .height(48.dp) // Height of the button
                    .width(150.dp)  // Width of the button
            ) {
                Text(
                    text = "OPEN", // Button label
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}