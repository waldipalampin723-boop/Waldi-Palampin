package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Vibrant modern color accents for Glassmorphism
val GlassBgColor = Color(0x1FFFFFFF)
val GlassDarkBgColor = Color(0x1F0B0F19)
val GlassBorderColor = Color(0x33FFFFFF)
val GlassAccentBlue = Color(0xFF6366F1) // Indigo 500
val GlassAccentIndigo = Color(0xFF4F46E5) // Indigo 600
val GlassAccentPurple = Color(0xFF8E24AA)
val GlassAccentCyan = Color(0xFF14B8A6) // Teal 500

/**
 * Custom Modifier that draws a deep Slate background with beautiful,
 * atmospheric glowing blobs of Indigo and Teal.
 */
fun Modifier.atmosphericBackground(): Modifier = this.drawBehind {
    // Draw solid dark slate 900 background
    drawRect(color = Color(0xFF0F172A))

    // Indigo Atmospheric Blob (Top-Left)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF6366F1).copy(alpha = 0.22f),
                Color(0xFF6366F1).copy(alpha = 0.05f),
                Color.Transparent
            ),
            center = Offset(x = size.width * -0.05f, y = size.height * -0.05f),
            radius = size.width * 0.75f
        ),
        center = Offset(x = size.width * -0.05f, y = size.height * -0.05f),
        radius = size.width * 0.75f
    )

    // Teal Atmospheric Blob (Bottom-Right)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF14B8A6).copy(alpha = 0.16f),
                Color(0xFF14B8A6).copy(alpha = 0.04f),
                Color.Transparent
            ),
            center = Offset(x = size.width * 1.05f, y = size.height * 0.85f),
            radius = size.width * 0.75f
        ),
        center = Offset(x = size.width * 1.05f, y = size.height * 0.85f),
        radius = size.width * 0.75f
    )
}

@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    borderWidth: Dp = 1.dp,
    borderColor: Color = Color.White.copy(alpha = 0.15f),
    containerColor: Color = Color.White.copy(alpha = 0.08f),
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(16.dp, shape = shape, clip = false)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        containerColor.copy(alpha = 0.12f),
                        containerColor.copy(alpha = 0.04f)
                    )
                ),
                shape = shape
            )
            .border(
                width = borderWidth,
                brush = Brush.linearGradient(
                    colors = listOf(
                        borderColor.copy(alpha = 0.2f),
                        borderColor.copy(alpha = 0.05f)
                    )
                ),
                shape = shape
            )
            .clip(shape)
            .padding(16.dp)
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun GlassmorphicButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color(0xFF1A237E),
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(4.dp, shape = RoundedCornerShape(16.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = if (enabled) listOf(
                        containerColor.copy(alpha = 0.9f),
                        containerColor.copy(alpha = 0.6f)
                    ) else listOf(
                        Color.Gray.copy(alpha = 0.4f),
                        Color.Gray.copy(alpha = 0.2f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
}

@Composable
fun GlassmorphicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    textStyle: TextStyle = TextStyle(color = Color.White, fontSize = 15.sp),
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label, color = Color.White.copy(alpha = 0.8f)) },
        singleLine = singleLine,
        textStyle = textStyle,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
            focusedContainerColor = Color(0x33000000),
            unfocusedContainerColor = Color(0x14000000),
            cursorColor = Color.White
        ),
        modifier = modifier
    )
}
