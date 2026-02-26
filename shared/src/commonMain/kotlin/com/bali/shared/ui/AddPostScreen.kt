package com.bali.shared.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bali.shared.presentation.CreatePostViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddPostScreen(
    viewModel: CreatePostViewModel = koinViewModel(),
    onPostSuccess: () -> Unit = {}
) {
    var content by remember { mutableStateOf("") }
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val postSuccess by viewModel.postSuccess.collectAsState()

    LaunchedEffect(postSuccess) {
        if (postSuccess) {
            viewModel.resetPostSuccess()
            content = ""
            onPostSuccess()
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Create Post",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    TextButton(
                        onClick = { viewModel.createPost(content, null) },
                        enabled = content.isNotBlank()
                    ) {
                        Text("Post", fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Content Input
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                placeholder = { Text("What's happening in your village?") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    errorBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Media Options
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* TODO: Pick Image */ }) {
                    Icon(
                        Icons.Default.Image,
                        contentDescription = "Add Image",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text("Add Photo", style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.width(24.dp))

                IconButton(onClick = { /* TODO: Pick Video */ }) {
                    Icon(
                        Icons.Default.VideoCall,
                        contentDescription = "Add Video",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text("Add Video", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
