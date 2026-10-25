package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.ActionPrimary
import com.example.ui.theme.AccentHighlight

@Composable
fun BeforeAfterSlider(
    modifier: Modifier = Modifier,
    beforeImageRes: Int = R.drawable.img_treatment_before,
    afterImageRes: Int = R.drawable.img_treatment_after,
    height: Dp = 320.dp
) {
    var splitRatio by remember { mutableFloatStateOf(0.5f) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(16.dp))
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .background(Color.Black)
    ) {
        val totalWidthPx = constraints.maxWidth.toFloat()
        val density = LocalDensity.current

        // Right / Base layer: AFTER image
        Image(
            painter = painterResource(id = afterImageRes),
            contentDescription = "Foto Depois do Procedimento",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Left layer: BEFORE image clipped to current split position
        val leftClipShape = remember(splitRatio) {
            object : Shape {
                override fun createOutline(
                    size: Size,
                    layoutDirection: LayoutDirection,
                    density: Density
                ): Outline {
                    val clipWidth = size.width * splitRatio
                    return Outline.Rectangle(Rect(0f, 0f, clipWidth, size.height))
                }
            }
        }

        Image(
            painter = painterResource(id = beforeImageRes),
            contentDescription = "Foto Antes do Procedimento",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(leftClipShape)
        )

        // Labels
        Surface(
            color = Color.Black.copy(alpha = 0.65f),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
        ) {
            Text(
                text = "ANTES",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        Surface(
            color = ActionPrimary.copy(alpha = 0.85f),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
        ) {
            Text(
                text = "DEPOIS (15 DIAS)",
                color = AccentHighlight,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        // Draggable vertical divider line and handle
        val splitDp = with(density) { (totalWidthPx * splitRatio).toDp() }

        // Vertical line
        Box(
            modifier = Modifier
                .offset(x = splitDp - 1.5.dp)
                .width(3.dp)
                .fillMaxHeight()
                .background(Color.White.copy(alpha = 0.9f))
        )

        // Center Handle Button
        Surface(
            shape = CircleShape,
            color = ActionPrimary,
            shadowElevation = 8.dp,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = splitDp - 22.dp)
                .size(44.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val newRatio = (splitRatio + (dragAmount.x / totalWidthPx)).coerceIn(0.05f, 0.95f)
                        splitRatio = newRatio
                    }
                }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.CompareArrows,
                    contentDescription = "Arrastar divisor Antes e Depois",
                    tint = AccentHighlight,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Instructions banner at bottom
        Surface(
            color = Color.Black.copy(alpha = 0.5f),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Arraste o botão para comparar Antes e Depois",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 11.sp
                )
            }
        }
    }
}
