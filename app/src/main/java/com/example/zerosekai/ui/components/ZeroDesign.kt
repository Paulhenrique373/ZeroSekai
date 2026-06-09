package com.example.zerosekai.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.zerosekai.ui.theme.ZAccent
import com.example.zerosekai.ui.theme.ZBackground
import com.example.zerosekai.ui.theme.ZBackgroundSoft
import com.example.zerosekai.ui.theme.ZBorder
import com.example.zerosekai.ui.theme.ZBorderSoft
import com.example.zerosekai.ui.theme.ZCard
import com.example.zerosekai.ui.theme.ZGlass
import com.example.zerosekai.ui.theme.ZGlassStrong
import com.example.zerosekai.ui.theme.ZPrimary
import com.example.zerosekai.ui.theme.ZSecondary
import com.example.zerosekai.ui.theme.ZSurface
import com.example.zerosekai.ui.theme.ZSurfaceElevated
import com.example.zerosekai.ui.theme.ZText
import com.example.zerosekai.ui.theme.ZTextMuted

@Composable
fun ZeroScreenBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        ZBackground,
                        ZPrimary.copy(alpha = 0.18f),
                        ZBackgroundSoft,
                        ZSecondary.copy(alpha = 0.10f),
                        ZBackground
                    )
                )
            )
    ) {
        content()
    }
}

val ZeroPrimaryGradient = Brush.linearGradient(
    colors = listOf(
        ZPrimary,
        ZAccent,
        ZSecondary
    )
)

val ZeroSubtleGradient = Brush.linearGradient(
    colors = listOf(
        ZSurface.copy(alpha = 0.96f),
        ZPrimary.copy(alpha = 0.16f),
        ZSecondary.copy(alpha = 0.10f)
    )
)

@Composable
fun ZeroGlassCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(24.dp),
    contentPadding: PaddingValues = PaddingValues(18.dp),
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = ZGlass,
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    ZGlassStrong,
                    ZBorder.copy(alpha = 0.7f)
                )
            )
        ),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier.padding(contentPadding)
        ) {
            content()
        }
    }
}

@Composable
fun ZeroElevatedPanel(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = ZCard.copy(alpha = 0.94f)
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier.padding(contentPadding)
        ) {
            content()
        }
    }
}

@Composable
fun ZeroAvatar(
    photoUrl: String?,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    label: String = "",
    showRing: Boolean = true
) {
    val innerSize = (size.value - if (showRing) 4f else 0f).coerceAtLeast(1f).dp

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                if (showRing) {
                    Brush.linearGradient(
                        colors = listOf(
                            ZAccent,
                            ZPrimary,
                            ZSecondary
                        )
                    )
                } else {
                    Brush.linearGradient(
                        colors = listOf(
                            ZSurfaceElevated,
                            ZSurfaceElevated
                        )
                    )
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(innerSize)
                .clip(CircleShape)
                .background(ZSurfaceElevated),
            contentAlignment = Alignment.Center
        ) {
            if (!photoUrl.isNullOrBlank()) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = label.ifBlank { "Avatar" },
                    modifier = Modifier
                        .size(innerSize)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = ZTextMuted,
                    modifier = Modifier.size((size.value * 0.44f).dp)
                )
            }
        }
    }
}

@Composable
fun ZeroGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    val shape = RoundedCornerShape(16.dp)

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(52.dp)
            .clip(shape)
            .background(
                brush = if (enabled) {
                    ZeroPrimaryGradient
                } else {
                    Brush.linearGradient(
                        colors = listOf(
                            ZBorderSoft,
                            ZSurfaceElevated
                        )
                    )
                }
            ),
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = ZText,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = ZTextMuted
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp,
            disabledElevation = 0.dp
        ),
        contentPadding = PaddingValues(horizontal = 18.dp)
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(19.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))
        }

        Text(
            text = text,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ZeroSoftButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ZSurfaceElevated.copy(alpha = 0.92f),
            contentColor = ZText
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        ),
        border = BorderStroke(1.dp, ZBorderSoft),
        contentPadding = PaddingValues(horizontal = 18.dp)
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(19.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))
        }

        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun zeroTextFieldColors(): TextFieldColors =
    OutlinedTextFieldDefaults.colors(
        focusedTextColor = ZText,
        unfocusedTextColor = ZText,
        focusedLabelColor = ZText,
        unfocusedLabelColor = ZTextMuted,
        focusedPlaceholderColor = ZTextMuted,
        unfocusedPlaceholderColor = ZTextMuted,
        focusedLeadingIconColor = ZPrimary,
        unfocusedLeadingIconColor = ZTextMuted,
        focusedTrailingIconColor = ZText,
        unfocusedTrailingIconColor = ZTextMuted,
        cursorColor = ZPrimary,
        focusedBorderColor = ZPrimary,
        unfocusedBorderColor = ZBorder,
        focusedContainerColor = ZSurface.copy(alpha = 0.76f),
        unfocusedContainerColor = ZSurface.copy(alpha = 0.58f)
    )

@Composable
fun ZeroSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailing: @Composable () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = ZText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    color = ZTextMuted,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        trailing()
    }
}

@Composable
fun ZeroStatBlock(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = ZText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            color = ZTextMuted,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ZeroEmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(ZSurfaceElevated)
                .border(1.dp, ZBorderSoft, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ZPrimary,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = title,
            color = ZText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = message,
            color = ZTextMuted,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}
