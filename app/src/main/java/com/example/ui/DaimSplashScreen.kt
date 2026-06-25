package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun DaimSplashScreen(
    modifier: Modifier = Modifier
) {
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFFFFF),
            Color(0xFFFAFDFA),
            Color(0xFFEDF7EE),
            Color(0xFFD6EFD8)
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        // 1. Static Background Patterns (Leaves and Glassy Bubbles - matching the uploaded design)
        BackgroundDecoration()

        // 2. Central Branding Column
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Iconic Green Medical Cross with Leaf
            DaimPharmacyLogo(
                modifier = Modifier
                    .size(140.dp)
                    .padding(bottom = 24.dp)
            )

            // Brand Title: DAIM
            Text(
                text = "DAIM",
                fontSize = 44.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20),
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Brand Sub-heading: PHARMACY (with horizontal rules)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(Color(0xFF81C784))
                )
                Text(
                    text = " PHARMACY ",
                    fontSize = 15.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2E7D32),
                    letterSpacing = 6.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(Color(0xFF81C784))
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Slogan: CAREGIVING, ALWAYS (flanked by leaves)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // Left Leaf
                LeafIcon(modifier = Modifier.size(16.dp))
                
                Text(
                    text = " CAREGIVING, ALWAYS ",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF388E3C),
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                
                // Right Leaf
                LeafIcon(
                    modifier = Modifier
                        .size(16.dp)
                        .graphicsLayer(scaleX = -1f) // Flip leaf for symmetry
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Heart Decoration
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(0.3f)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(Color(0xFFA5D6A7).copy(alpha = 0.6f))
                )
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier
                        .size(12.dp)
                        .padding(horizontal = 4.dp)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(Color(0xFFA5D6A7).copy(alpha = 0.6f))
                )
            }
        }
    }
}

@Composable
fun BackgroundDecoration() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // 1. Soft wave ribbons representing the curved light-green accents at bottom
        val ribbonPath1 = Path().apply {
            moveTo(0f, height * 0.8f)
            cubicTo(
                width * 0.25f, height * 0.7f,
                width * 0.75f, height * 0.95f,
                width, height * 0.85f
            )
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(
            path = ribbonPath1,
            color = Color(0xFFC8E6C9).copy(alpha = 0.15f)
        )

        val ribbonPath2 = Path().apply {
            moveTo(0f, height * 0.85f)
            cubicTo(
                width * 0.3f, height * 0.78f,
                width * 0.7f, height * 0.92f,
                width, height * 0.75f
            )
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(
            path = ribbonPath2,
            color = Color(0xFFA5D6A7).copy(alpha = 0.1f)
        )

        // Wave ribbon at top left
        val topRibbonPath = Path().apply {
            moveTo(0f, 0f)
            lineTo(width * 0.4f, 0f)
            cubicTo(
                width * 0.25f, height * 0.08f,
                width * 0.08f, height * 0.15f,
                0f, height * 0.25f
            )
            close()
        }
        drawPath(
            path = topRibbonPath,
            color = Color(0xFFC8E6C9).copy(alpha = 0.2f)
        )

        // 2. Translucent Static "Glassy" Bubbles matching original layout
        // Bubble 1 (Top Left)
        val yOffset1 = height * 0.18f
        drawCircle(
            color = Color.White.copy(alpha = 0.4f),
            radius = 35f,
            center = Offset(width * 0.15f, yOffset1)
        )
        drawCircle(
            color = Color(0xFF81C784).copy(alpha = 0.2f),
            radius = 35f,
            center = Offset(width * 0.15f, yOffset1),
            style = Stroke(width = 2f)
        )

        // Bubble 2 (Right Middle)
        val yOffset2 = height * 0.35f
        drawCircle(
            color = Color.White.copy(alpha = 0.5f),
            radius = 25f,
            center = Offset(width * 0.88f, yOffset2)
        )
        drawCircle(
            color = Color(0xFF81C784).copy(alpha = 0.2f),
            radius = 25f,
            center = Offset(width * 0.88f, yOffset2),
            style = Stroke(width = 2f)
        )

        // Bubble 3 (Bottom Left)
        val yOffset3 = height * 0.78f
        drawCircle(
            color = Color.White.copy(alpha = 0.4f),
            radius = 45f,
            center = Offset(width * 0.4f, yOffset3)
        )
        drawCircle(
            color = Color(0xFF81C784).copy(alpha = 0.15f),
            radius = 45f,
            center = Offset(width * 0.4f, yOffset3),
            style = Stroke(width = 2f)
        )

        // Bubble 4 (Bottom Right)
        val yOffset4 = height * 0.94f
        drawCircle(
            color = Color.White.copy(alpha = 0.6f),
            radius = 50f,
            center = Offset(width * 0.7f, yOffset4)
        )
        drawCircle(
            color = Color(0xFF81C784).copy(alpha = 0.25f),
            radius = 50f,
            center = Offset(width * 0.7f, yOffset4),
            style = Stroke(width = 2f)
        )

        // 3. Static background leaves
        // Draw decorative leaf shape at top-left
        drawLeaf(this, Offset(width * 0.08f, height * 0.05f), scale = 1.8f, rotationDegrees = 45f)
        drawLeaf(this, Offset(width * 0.18f, height * 0.08f), scale = 1.3f, rotationDegrees = 15f)
        drawLeaf(this, Offset(width * 0.05f, height * 0.28f), scale = 0.9f, rotationDegrees = -30f)
        
        // Draw leaves at bottom
        drawLeaf(this, Offset(width * 0.08f, height * 0.72f), scale = 1.6f, rotationDegrees = -20f)
        drawLeaf(this, Offset(width * 0.12f, height * 0.82f), scale = 2.2f, rotationDegrees = 30f)
        drawLeaf(this, Offset(width * 0.22f, height * 0.74f), scale = 1.0f, rotationDegrees = -60f)
        drawLeaf(this, Offset(width * 0.88f, height * 0.93f), scale = 1.8f, rotationDegrees = -45f)
    }
}

private fun drawLeaf(
    drawScope: androidx.compose.ui.graphics.drawscope.DrawScope,
    center: Offset,
    scale: Float,
    rotationDegrees: Float
) {
    drawScope.withTransform({
        translate(center.x, center.y)
        rotate(rotationDegrees, pivot = Offset.Zero)
    }) {
        val size = 20f * scale
        val leafPath = Path().apply {
            moveTo(0f, -size)
            cubicTo(size * 0.8f, -size * 0.8f, size * 1.2f, 0f, 0f, size)
            cubicTo(-size * 1.2f, 0f, -size * 0.8f, -size * 0.8f, 0f, -size)
            close()
        }
        
        // Leaf body gradient
        val leafBrush = Brush.linearGradient(
            colors = listOf(Color(0xFF4CAF50).copy(alpha = 0.7f), Color(0xFF2E7D32).copy(alpha = 0.8f)),
            start = Offset(0f, -size),
            end = Offset(0f, size)
        )
        
        drawPath(
            path = leafPath,
            brush = leafBrush
        )
        
        // Leaf vein
        drawLine(
            color = Color.White.copy(alpha = 0.4f),
            start = Offset(0f, -size),
            end = Offset(0f, size),
            strokeWidth = 2f
        )
    }
}

@Composable
fun DaimPharmacyLogo(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = w * 0.13f
        
        // Draw the medical cross outline using thick rounded stroke
        val r = w * 0.12f // rounded corner radius
        val sizeArm = w * 0.26f // arm length
        val midX = w / 2f
        val midY = h / 2f
        
        val crossPath = Path().apply {
            moveTo(midX - sizeArm, midY - w * 0.48f)
            lineTo(midX + sizeArm, midY - w * 0.48f)
            quadraticTo(midX + sizeArm + r, midY - w * 0.48f, midX + sizeArm + r, midY - sizeArm)
            
            lineTo(midX + w * 0.48f, midY - sizeArm)
            quadraticTo(midX + w * 0.48f, midY - sizeArm + r, midX + w * 0.48f, midY + sizeArm)
            quadraticTo(midX + w * 0.48f, midY + sizeArm + r, midX + sizeArm + r, midY + sizeArm)
            
            lineTo(midX + sizeArm + r, midY + w * 0.48f)
            quadraticTo(midX + sizeArm, midY + w * 0.48f, midX - sizeArm, midY + w * 0.48f)
            quadraticTo(midX - sizeArm - r, midY + w * 0.48f, midX - sizeArm - r, midY + sizeArm)
            
            lineTo(midX - w * 0.48f, midY + sizeArm)
            quadraticTo(midX - w * 0.48f, midY + sizeArm - r, midX - w * 0.48f, midY - sizeArm)
            quadraticTo(midX - w * 0.48f, midY - sizeArm - r, midX - sizeArm - r, midY - sizeArm)
            
            close()
        }

        // Draw cross outline
        drawPath(
            path = crossPath,
            color = Color(0xFF4CAF50),
            style = Stroke(
                width = strokeWidth,
                join = StrokeJoin.Round,
                cap = StrokeCap.Round
            )
        )

        // Draw the elegant stylized leaf in the center forming the inner part
        val leafWidth = w * 0.35f
        val leafHeight = h * 0.5f
        
        val centerLeafPath = Path().apply {
            moveTo(midX - leafWidth * 0.4f, midY + leafHeight * 0.4f)
            cubicTo(
                midX - leafWidth * 0.9f, midY - leafHeight * 0.1f,
                midX - leafWidth * 0.2f, midY - leafHeight * 0.6f,
                midX + leafWidth * 0.3f, midY - leafHeight * 0.5f
            )
            cubicTo(
                midX + leafWidth * 0.8f, midY - leafHeight * 0.1f,
                midX + leafWidth * 0.2f, midY + leafHeight * 0.5f,
                midX - leafWidth * 0.4f, midY + leafHeight * 0.4f
            )
            close()
        }
        
        val logoLeafBrush = Brush.linearGradient(
            colors = listOf(Color(0xFF81C784), Color(0xFF2E7D32)),
            start = Offset(midX - leafWidth, midY + leafHeight),
            end = Offset(midX + leafWidth, midY - leafHeight)
        )
        
        drawPath(
            path = centerLeafPath,
            brush = logoLeafBrush
        )

        // Draw leaf stem/vein curving beautifully down the center of the leaf
        val veinPath = Path().apply {
            moveTo(midX - leafWidth * 0.35f, midY + leafHeight * 0.35f)
            quadraticTo(
                midX - leafWidth * 0.05f, midY - leafHeight * 0.05f,
                midX + leafWidth * 0.25f, midY - leafHeight * 0.45f
            )
        }
        drawPath(
            path = veinPath,
            color = Color.White.copy(alpha = 0.5f),
            style = Stroke(width = 4f, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun LeafIcon(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        
        val path = Path().apply {
            moveTo(0f, h * 0.8f)
            cubicTo(w * 0.4f, h * 0.1f, w * 0.9f, h * 0.1f, w, 0f)
            cubicTo(w * 0.8f, h * 0.6f, w * 0.3f, h * 0.9f, 0f, h * 0.8f)
            close()
        }
        
        drawPath(
            path = path,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF81C784), Color(0xFF2E7D32)),
                start = Offset(0f, h),
                end = Offset(w, 0f)
            )
        )
    }
}
