package com.bali.shared.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bali.shared.presentation.ProfileIntent
import com.bali.shared.presentation.ProfileViewModel
import com.bali.shared.ui.theme.DeepTeal
import com.bali.shared.ui.theme.VibrantMint
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: ProfileViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val user = state.user
    val loading = state.isLoading
    val error = state.error

    var isEditing by remember { mutableStateOf(false) }
    var editedUsername by remember { mutableStateOf("") }
    var editedEmail by remember { mutableStateOf("") }

    LaunchedEffect(user) {
        user?.let {
            editedUsername = it.username
            editedEmail = it.email
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", fontWeight = FontWeight.Bold, color = DeepTeal) },
                actions = {
                    if (isEditing) {
                        IconButton(onClick = {
                            viewModel.onIntent(ProfileIntent.UpdateProfile(editedUsername, editedEmail))
                            isEditing = false
                        }) {
                            Icon(Icons.Default.Check, contentDescription = "Save", tint = VibrantMint)
                        }
                    } else {
                        IconButton(onClick = { isEditing = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = DeepTeal)
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (loading && user == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = VibrantMint)
            }
        } else if (error != null && user == null) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.ErrorOutline,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color.Red
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = error, color = Color.Red, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                    BaliButton(text = "Retry", onClick = { viewModel.onIntent(ProfileIntent.FetchProfile) })
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
                    .padding(bottom = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Image Section
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(VibrantMint, DeepTeal)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (!user?.username.isNullOrEmpty()) user?.username?.take(1)?.uppercase() ?: "?" else "?",
                            color = Color.White,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 4.dp,
                        shadowElevation = 4.dp
                    ) {
                        IconButton(onClick = { /* TODO: Pick Image */ }) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Change Photo",
                                modifier = Modifier.size(20.dp),
                                tint = DeepTeal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // User Name & Role
                Text(
                    text = if (!user?.username.isNullOrEmpty()) user?.username!! else "Loading...",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = user?.role?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "User",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Information Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        ProfileInfoItem(
                            label = "Username",
                            value = if (isEditing) editedUsername else user?.username ?: "",
                            isEditing = isEditing,
                            onValueChange = { editedUsername = it },
                            icon = Icons.Default.Person
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))
                        ProfileInfoItem(
                            label = "Email",
                            value = if (isEditing) editedEmail else user?.email ?: "",
                            isEditing = isEditing,
                            onValueChange = { editedEmail = it },
                            icon = Icons.Default.Email
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))
                        ProfileInfoItem(
                            label = "Phone",
                            value = user?.phoneNumber ?: "Not provided",
                            isEditing = false,
                            icon = Icons.Default.Phone
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))
                        ProfileInfoItem(
                            label = "Village",
                            value = user?.villageGroup?.name ?: "No village assigned",
                            isEditing = false,
                            icon = Icons.Default.LocationOn
                        )
                    }
                }

                if (error != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = error, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                }

                if (isEditing) {
                    Spacer(modifier = Modifier.height(32.dp))
                    BaliButton(
                        text = "Save Changes",
                        onClick = {
                            viewModel.onIntent(ProfileIntent.UpdateProfile(editedUsername, editedEmail))
                            isEditing = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        isLoading = loading
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileInfoItem(
    label: String,
    value: String,
    isEditing: Boolean,
    onValueChange: (String) -> Unit = {},
    icon: ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(DeepTeal.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = DeepTeal,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (isEditing) {
                EditTextField(
                    value = value,
                    onValueChange = onValueChange
                )
            } else {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = DeepTeal,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = DeepTeal
        ),
        singleLine = true
    )
}
