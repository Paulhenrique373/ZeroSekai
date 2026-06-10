package com.example.zerosekai.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.zerosekai.data.model.Post
import com.example.zerosekai.ui.components.ZeroEmptyState
import com.example.zerosekai.ui.components.ZeroScreenBackground
import com.example.zerosekai.ui.components.ZeroSectionHeader
import com.example.zerosekai.ui.components.ZeroTopBar
import com.example.zerosekai.ui.theme.ZCard
import com.example.zerosekai.ui.theme.ZSecondary
import com.example.zerosekai.ui.theme.ZSurface
import com.example.zerosekai.ui.theme.ZText
import com.example.zerosekai.viewmodel.HomeViewModel
import com.example.zerosekai.viewmodel.SavedViewModel

@Composable
fun SavedPostsScreen(
    navController: NavHostController
) {
    val homeViewModel: HomeViewModel = viewModel()
    val savedViewModel: SavedViewModel = viewModel()

    val posts by homeViewModel.posts.collectAsState()
    val savedPostIds by savedViewModel.savedPostIds.collectAsState()

    val savedPosts =
        posts.filter { post ->
            savedPostIds.contains(post.id)
        }

    ZeroScreenBackground(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item(
                span = {
                    GridItemSpan(maxLineSpan)
                }
            ) {
                ZeroTopBar(
                    title = "Posts salvos",
                    subtitle = "Tudo que voce guardou",
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            if (savedPosts.isEmpty()) {
                item(
                    span = {
                        GridItemSpan(maxLineSpan)
                    }
                ) {
                    ZeroEmptyState(
                        icon = Icons.Default.BookmarkBorder,
                        title = "Nenhum post salvo",
                        message = "Toque no marcador de um post para guardar aqui.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 80.dp)
                    )
                }
            } else {
                item(
                    span = {
                        GridItemSpan(maxLineSpan)
                    }
                ) {
                    ZeroSectionHeader(
                        title = "Sua colecao",
                        subtitle = "${savedPosts.size} posts salvos"
                    )
                }

                items(
                    items = savedPosts,
                    key = { post -> post.id }
                ) { post ->
                    SavedPostTile(
                        post = post,
                        onOpen = {
                            navController.navigate("post_detail/${post.id}")
                        },
                        onUnsave = {
                            savedViewModel.toggleSaved(post.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SavedPostTile(
    post: Post,
    onOpen: () -> Unit,
    onUnsave: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(ZCard)
            .clickable(onClick = onOpen)
    ) {
        AsyncImage(
            model = post.imageUrl,
            contentDescription = post.caption,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(5.dp),
            color = ZSurface.copy(alpha = 0.78f),
            shape = RoundedCornerShape(999.dp)
        ) {
            IconButton(
                onClick = onUnsave
            ) {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = "Remover dos salvos",
                    tint = ZSecondary
                )
            }
        }
    }
}
