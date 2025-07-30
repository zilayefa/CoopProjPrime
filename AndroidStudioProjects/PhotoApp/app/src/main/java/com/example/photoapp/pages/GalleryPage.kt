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

    // Holds the list of image URIs from device storage
    val imageUris = remember { mutableStateListOf<Uri>() }

    // Tracks which images have been selected by the user
    val selectedItems = remember { mutableStateListOf<Uri>() }

    val selectionMode = remember { mutableStateOf(false) }

    val previewUri = remember { mutableStateOf<Uri?>(null) }

    // Load images only once when the composable first enters the composition
    LaunchedEffect(Unit) {
        imageUris.addAll(loadGalleryImages(context))
    }

    if (previewUri.value != null) {
        // Show full image preview
        ImagePreviewScreen(
            uri = previewUri.value!!,
            onBack = { previewUri.value = null } // Clear preview on tap
        )
    } else {

        // Scaffold gives us a slot to add a bottomBar
        Scaffold(
            bottomBar = {
                BottomBar(
                    onHomeClick = { navController.navigate("front") },
                    onShareClick = {
                        if (selectedItems.isNotEmpty()) {
                            shareImagesOnWhatsApp(context, selectedItems)
                        }
                    }
                )
            }
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF2F0F0)) // light gray background
            ) {
                // Top section: Title and "Select" button in a row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Gallery",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Button(
                        onClick = {
                            selectionMode.value = !selectionMode.value
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) {
                        Text(if (selectionMode.value) "Cancel" else "Select")
                    }
                }

                // Subheading
                Text(
                    text = "BROWSE ALL",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )

                // Image Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(imageUris.size) { index ->
                        val uri = imageUris[index]

                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    if (selectionMode.value) {
                                        if (selectedItems.contains(uri)) {
                                            selectedItems.remove(uri)
                                        } else {
                                            selectedItems.add(uri)
                                        }
                                    } else {
                                        previewUri.value = uri  // show full-screen preview
                                    }
                                }
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(model = uri),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

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

fun loadGalleryImages(context: Context): List<Uri> {
    val imageUris = mutableListOf<Uri>()

    // URI representing the external image storage location
    val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    // Which columns we want to retrieve from the database
    val projection = arrayOf(MediaStore.Images.Media._ID)

    // Query the system media store for images
    context.contentResolver.query(
        collection,
        projection,
        null,
        null,
        "${MediaStore.Images.Media.DATE_ADDED} DESC" // sort by most recent
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val contentUri = ContentUris.withAppendedId(collection, id)
            imageUris.add(contentUri)
        }
    }

    return imageUris
}

fun shareImagesOnWhatsApp(context: Context, uris: List<Uri>) {
    val intent = Intent().apply {
        action = Intent.ACTION_SEND_MULTIPLE
        type = "image/*"
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
        `package` = "com.whatsapp"
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    try {
        context.startActivity(Intent.createChooser(intent, "Share via"))
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun BottomBar(onHomeClick: () -> Unit, onShareClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 10.dp, horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "share",
            modifier = Modifier
                .clickable { onShareClick() }
                .background(Color.LightGray)
                .padding(8.dp),
            color = Color.DarkGray
        )

        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Home",
            modifier = Modifier
                .size(28.dp)
                .clickable { onHomeClick() },
            tint = Color.Black
        )
    }
}

@Composable
fun ImagePreviewScreen(uri: Uri, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { onBack() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = rememberAsyncImagePainter(uri),
            contentDescription = "Full image preview",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}

