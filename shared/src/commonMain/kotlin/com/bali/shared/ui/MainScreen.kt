package com.bali.shared.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class MainTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    Feeds("Feeds", Icons.Filled.List, Icons.Outlined.List),
    Maps("Maps", Icons.Filled.Place, Icons.Outlined.Place),
    Updates("Updates", Icons.Filled.Campaign, Icons.Outlined.Campaign),
    AddPost("Add Post", Icons.Filled.AddCircleOutline, Icons.Outlined.AddCircleOutline),
    Profile("Profile", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle)
}

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableStateOf(MainTab.Feeds) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            contentWindowInsets = WindowInsets(0)
        ) { padding ->
            Crossfade(
                targetState = selectedTab,
                modifier = Modifier.padding(padding),
                label = "TabTransition"
            ) { tab ->
                when (tab) {
                    MainTab.Feeds -> FeedScreen()
                    MainTab.Maps -> ExploreScreen()
                    MainTab.Updates -> UpdatesScreen()
                    MainTab.AddPost -> AddPostScreen()
                    MainTab.Profile -> ProfileScreen()
                }
            }
        }

        TelegramBottomBar(
            tabs = MainTab.entries,
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun TelegramBottomBar(
    tabs: List<MainTab>,
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .height(60.dp)
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEach { tab ->
                    val selected = tab == selectedTab

                    val highlight by animateColorAsState(
                        if (selected)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        else Color.Transparent,
                        label = "highlight"
                    )

                    val iconColor =
                        if (selected) MaterialTheme.colorScheme.primary
                        else Color.Gray

                    Box(
                        modifier = Modifier
                            .height(44.dp)
                            .widthIn(min = 68.dp, max = 96.dp)
                            .clip(RoundedCornerShape(50))
                            .background(highlight)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { onTabSelected(tab) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                modifier = Modifier.size(22.dp),
                                contentDescription = tab.title,
                                tint = iconColor
                            )

                            Spacer(Modifier.height(2.dp))

                            Text(
                                text = tab.title,
                                fontSize = 10.sp,
                                lineHeight = 12.sp,
                                maxLines = 1,
                                color = iconColor
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun MainScreenPreview() {
    MaterialTheme {
        Surface(color = Color(0xFFF2F3F5)) {
            MainScreen()
        }
    }
}
