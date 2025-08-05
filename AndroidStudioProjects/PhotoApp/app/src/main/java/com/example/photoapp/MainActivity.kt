package com.example.photoapp

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.photoapp.pages.FrontPage
import com.example.photoapp.pages.GalleryPage
import com.example.photoapp.ui.theme.PhotoAppTheme

class MainActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter") // Ignore Scaffold padding warning
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enables content to draw behind system bars for edge-to-edge layout
        enableEdgeToEdge()

        setContent {
            PhotoAppTheme { // Apply your custom app theme
                val navController = rememberNavController() // Controls screen navigation
                val context = this // Store reference to current Activity context

                // Launcher to request multiple storage permission at runtime
                val storagePermissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    val deniedPermissions = permissions.filterValues { !it }
                    if (deniedPermissions.isNotEmpty()) {
                        // Show a message if user denies the permission
                        Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
                    }
                }

                // Request permission when the app is first launched
                LaunchedEffect(Unit) {
                    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        // Android 13+ requires separate permissions for images and videos
                        arrayOf(
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VIDEO
                        )
                    } else {
                        // Older Android versions use single permission
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                    // Launch permission request for all required permissions
                    storagePermissionLauncher.launch(permissions)
                }

                // Main screen layout
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    // Navigation setup: defines screen routes
                    NavHost(
                        navController = navController,
                        startDestination = "front" // First screen to show
                    ) {
                        // Route for the front (home) screen
                        composable("front") {
                            FrontPage(
                                onOpenClick = {
                                    navController.navigate("gallery") // Navigate to gallery
                                }
                            )
                        }

                        // Route for the media gallery screen
                        composable("gallery") {
                            GalleryPage(navController) // Displays photos/videos
                        }
                    }
                }
            }
        }
    }
}