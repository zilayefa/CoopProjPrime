package com.example.photoapp.pages

import android.content.ActivityNotFoundException
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter



@Composable
fun GalleryPage(navController: NavController) {
    val context = LocalContext.current

    // This list holds pairs of (URI, type) for each media file (image/video) found on the device
    val mediaList = remember { mutableStateListOf<Pair<Uri, String>>() }

    // Tracks the media items the user has selected — used for sharing via WhatsApp
    val selectedItems = remember { mutableStateListOf<Uri>() }

    // Boolean flag to toggle selection mode (true = selection active, false = browsing mode)
    val selectionMode = remember { mutableStateOf(false) }

    // Holds the URI of the currently previewed image (null means no image is being previewed)
    val previewUri = remember { mutableStateOf<Uri?>(null) }

    // Loads all image and video files from the gallery once when the screen appears
    LaunchedEffect(Unit) {
        mediaList.addAll(loadMediaFromGallery(context))
    }

    // If an image is selected for preview, show it full-screen and hide the gallery UI
    if (previewUri.value != null) {
        ImagePreviewScreen(
            uri = previewUri.value!!,
            onBack = { previewUri.value = null } // Tapping will exit the preview and return to gallery
        )
    } else {
        // Main Gallery UI wrapped in a Scaffold, allowing us to add a bottom bar
        Scaffold(
            bottomBar = {
                BottomBar(
                    onHomeClick = { navController.navigate("front") }, // Navigate back to home
                    onShareClick = {
                        // Trigger WhatsApp sharing if any items are selected
                        if (selectedItems.isNotEmpty()) {
                            shareImagesOnWhatsApp(context, selectedItems)
                        }
                    }
                )
            }
        ) { paddingValues ->

            // Main column layout that fills the entire screen and accounts for scaffold padding
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF2F0F0)) // Light gray background for aesthetics
            ) {
                // Title bar at the top with gallery title and "Select"/"Cancel" toggle button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween, // Place title and button far apart
                    verticalAlignment = Alignment.CenterVertically // Center them vertically
                ) {
                    Text(
                        text = "Your Gallery", // Main heading
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    // Button toggles selection mode (for multi-select/sharing)
                    Button(
                        onClick = {
                            selectionMode.value = !selectionMode.value
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) {
                        // Text changes depending on whether selection mode is active
                        Text(if (selectionMode.value) "Cancel" else "Select")
                    }
                }

                // Section label just above the grid
                Text(
                    text = "BROWSE ALL", // Subheading above media grid
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )

                // Grid that displays all media items (images/videos)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2), // 2 items per row
                    modifier = Modifier
                        .weight(1f) // Fill available space
                        .padding(horizontal = 8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp) // Space at the bottom
                ) {
                    // Loop over each media item and build its grid tile
                    items(mediaList.size) { index ->
                        val (uri, type) = mediaList[index]

                        // Box representing each image or video tile
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .aspectRatio(1f) // Keep square shape
                                .clip(RoundedCornerShape(8.dp)) // Rounded corners
                                .clickable {
                                    if (selectionMode.value) {
                                        // If selection mode is on, toggle the selected state
                                        if (selectedItems.contains(uri)) {
                                            selectedItems.remove(uri)
                                        } else {
                                            selectedItems.add(uri)
                                        }
                                    } else {
                                        if (type == "image") {
                                            // If it's an image, open it in full-screen preview
                                            previewUri.value = uri
                                        } else {
                                            // If it's a video, launch external video player
                                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                                setDataAndType(uri, "video/*")
                                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                            }
                                            context.startActivity(intent)
                                        }
                                    }
                                }
                        ) {
                            // Renders the media thumbnail based on its type
                            if (type == "image") {
                                Image(
                                    painter = rememberAsyncImagePainter(model = uri), // Load image from URI
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop, // Fill tile while preserving crop
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                // Placeholder for video: black background with play icon
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Video",
                                        tint = Color.White,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                            }

                            // If item is selected, draw a white check icon in top-right
                            if (selectedItems.contains(uri)) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun loadMediaFromGallery(context: Context): List<Pair<Uri, String>> {
    // This list will store pairs of (URI, "image"/"video") for each media file found
    val mediaList = mutableListOf<Pair<Uri, String>>()

    // URI representing the general external media collection (includes both images and videos)
    val collection = MediaStore.Files.getContentUri("external")

    // Columns we want to retrieve from the database: file ID and media type
    val projection = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.MEDIA_TYPE
    )

    // SQL WHERE clause to filter for images OR videos
    val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"

    // Values to plug into the selection query — looking for images and videos
    val selectionArgs = arrayOf(
        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
        MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
    )

    // Sort the results by most recently added first
    val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"

    // Perform the actual query
    context.contentResolver.query(
        collection,
        projection,
        selection,
        selectionArgs,
        sortOrder
    )?.use { cursor ->
        // Get column indexes for ID and type so we can extract data efficiently
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
        val typeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)

        // Loop through all rows returned by the query
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn) // Unique ID of the media file
            val type = cursor.getInt(typeColumn) // Type: image or video

            // Build the content URI for the specific media type
            val uri = when (type) {
                MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE ->
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO ->
                    ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)

                else -> continue // Skip unsupported media types
            }

            // Store URI along with its type as a pair
            val mediaType = if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) "image" else "video"
            mediaList.add(uri to mediaType)
        }
    }

    return mediaList // Return the complete list
}

fun shareImagesOnWhatsApp(context: Context, uris: List<Uri>) {
    // Create an intent for sharing multiple images
    val intent = Intent().apply {
        action = Intent.ACTION_SEND_MULTIPLE // Allows sharing multiple files
        type = "image/*" // MIME type for images (WhatsApp will still accept videos if added)
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris)) // URIs to share
        `package` = "com.whatsapp" // Target WhatsApp specifically
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Grant read access to WhatsApp
    }

    try {
        // Show a chooser dialog to share the content
        context.startActivity(Intent.createChooser(intent, "Share via"))
    } catch (e: ActivityNotFoundException) {
        // Show a toast if WhatsApp is not installed
        Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun BottomBar(onHomeClick: () -> Unit, onShareClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth() // Stretch across the screen
            .background(Color.White) // White background for bar
            .padding(vertical = 10.dp, horizontal = 24.dp), // Inner padding
        horizontalArrangement = Arrangement.SpaceBetween, // Items at left and right
        verticalAlignment = Alignment.CenterVertically // Center icons/text vertically
    ) {
        // Share Text Button
        Text(
            text = "share",
            modifier = Modifier
                .clickable { onShareClick() } // Triggers share callback
                .background(Color.LightGray)
                .padding(8.dp),
            color = Color.DarkGray
        )

        // Home Icon Button
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Home",
            modifier = Modifier
                .size(28.dp)
                .clickable { onHomeClick() }, // Triggers home navigation callback
            tint = Color.Black
        )
    }
}

@Composable
fun ImagePreviewScreen(uri: Uri, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize() // Take full screen
            .background(Color.Black) // Black background for better focus
            .clickable { onBack() }, // Tap anywhere to exit preview
        contentAlignment = Alignment.Center // Center image in the screen
    ) {
        // Display the selected image using Coil
        Image(
            painter = rememberAsyncImagePainter(uri), // Load image from URI
            contentDescription = "Full image preview",
            contentScale = ContentScale.Fit, // Scale the image to fit inside the screen
            modifier = Modifier.fillMaxSize()
        )
    }
}
