package com.example.zerosekai.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.example.zerosekai.ui.theme.ZBorderSoft
import com.example.zerosekai.ui.theme.ZSurfaceElevated

@Composable
private fun rememberZeroShimmerBrush(): Brush {
    val transition =
        rememberInfiniteTransition(
            label = "zeroShimmerTransition"
        )

    val translate by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "zeroShimmerTranslate"
    )

    return Brush.linearGradient(
        colors = listOf(
            ZSurfaceElevated.copy(alpha = 0.54f),
            ZBorderSoft.copy(alpha = 0.88f),
            ZSurfaceElevated.copy(alpha = 0.54f)
        ),
        start = Offset(
            x = translate - 700f,
            y = translate - 700f
        ),
        end = Offset(
            x = translate,
            y = translate
        )
    )
}

@Composable
fun ZeroShimmerBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp)
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(rememberZeroShimmerBrush())
    )
}

@Composable
fun FeedLoadingSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 2
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        repeat(itemCount) {
            FeedPostSkeleton()
        }
    }
}

@Composable
fun FeedPostSkeleton(
    modifier: Modifier = Modifier
) {
    ZeroElevatedPanel(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ZeroShimmerBox(
                    modifier = Modifier.size(46.dp),
                    shape = CircleShape
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    ZeroShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth(0.44f)
                            .height(16.dp)
                    )

                    ZeroShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth(0.24f)
                            .height(10.dp)
                    )
                }

                ZeroShimmerBox(
                    modifier = Modifier.size(24.dp),
                    shape = CircleShape
                )
            }

            ZeroShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .aspectRatio(4f / 5f),
                shape = RoundedCornerShape(18.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(3) {
                        ZeroShimmerBox(
                            modifier = Modifier.size(26.dp),
                            shape = CircleShape
                        )
                    }
                }

                ZeroShimmerBox(
                    modifier = Modifier.size(26.dp),
                    shape = CircleShape
                )
            }

            Column(
                modifier = Modifier.padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ZeroShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.30f)
                        .height(14.dp)
                )

                ZeroShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                )

                ZeroShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .height(14.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun SearchLoadingSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 5
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        repeat(itemCount) {
            UserRowSkeleton()
        }
    }
}

@Composable
fun ChatListLoadingSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 5
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        repeat(itemCount) {
            UserRowSkeleton(
                showTime = true
            )
        }
    }
}

@Composable
fun UserRowSkeleton(
    modifier: Modifier = Modifier,
    showTime: Boolean = false
) {
    ZeroElevatedPanel(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ZeroShimmerBox(
                modifier = Modifier.size(58.dp),
                shape = CircleShape
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ZeroShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.58f)
                        .height(15.dp)
                )

                ZeroShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.82f)
                        .height(12.dp)
                )
            }

            if (showTime) {
                Spacer(modifier = Modifier.width(12.dp))

                ZeroShimmerBox(
                    modifier = Modifier
                        .width(44.dp)
                        .height(22.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}
