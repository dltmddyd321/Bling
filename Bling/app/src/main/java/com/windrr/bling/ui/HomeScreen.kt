package com.windrr.bling.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.windrr.bling.NeonPink
import com.windrr.bling.ui.theme.DarkGray
import com.windrr.bling.ui.theme.NeonBlack
import com.windrr.bling.ui.theme.NeonBlue
import com.windrr.bling.ui.theme.NeonGreen
import com.windrr.bling.ui.theme.NeonYellow
import com.windrr.bling.ui.theme.TextGray

@Composable
fun HomeScreen(
    onPlayClick: (text: String, color: Color, size: Float, speed: Float) -> Unit
) {
    // 1. 상태 관리 (사용자가 입력하는 값들)
    var inputText by remember { mutableStateOf("I LOVE YOU") }
    var selectedColor by remember { mutableStateOf(NeonGreen) }
    var textSize by remember { mutableFloatStateOf(100f) } // 50f ~ 200f
    var scrollSpeed by remember { mutableFloatStateOf(0.5f) } // 0f ~ 1f

    // 색상 팔레트 (미리 정의)
    val colorPalette =
        listOf(NeonGreen, NeonPink, NeonBlue, NeonYellow, Color.White, Color.Red, Color(0xFFFF9800))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeonBlack) // 배경은 리얼 블랙
            .padding(16.dp)
            .padding(top = 32.dp) // 상태바 여백
    ) {
        // --- [A. 미리보기 영역] ---
        Text(text = "PREVIEW", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .border(
                    2.dp,
                    Brush.linearGradient(listOf(NeonBlue, NeonPink)),
                    RoundedCornerShape(12.dp)
                ) // 그라데이션 테두리
                .background(DarkGray, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            // 실제 마퀴(Marquee) 효과는 PlayerScreen에서 구현하고, 여기선 느낌만 냄
            Text(
                text = inputText.ifEmpty { "Text Here" },
                color = selectedColor,
                fontSize = (textSize / 3).sp, // 미리보기니까 1/3 크기로 축소
                fontWeight = FontWeight.Black,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- [B. 컨트롤 패널] ---

        // 1. 텍스트 입력
        NeonTextField(
            value = inputText,
            onValueChange = { if (it.length <= 30) inputText = it } // 글자수 제한
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 2. 색상 선택
        Text(text = "COLOR", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(colorPalette) { color ->
                ColorChip(
                    color = color,
                    isSelected = color == selectedColor,
                    onClick = { selectedColor = color }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. 사이즈 조절
        ControlSlider(
            label = "SIZE",
            value = textSize,
            onValueChange = { textSize = it },
            range = 50f..300f,
            accentColor = NeonBlue
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4. 속도 조절
        ControlSlider(
            label = "SPEED",
            value = scrollSpeed,
            onValueChange = { scrollSpeed = it },
            range = 0.1f..2.0f,
            accentColor = NeonPink
        )

        Spacer(modifier = Modifier.weight(1f)) // 남은 공간 밀어내기

        // --- [C. 플레이 버튼 & 광고] ---
        // (광고는 나중에 넣을 자리) Box { Text("Ad Banner Here", color = Color.Gray) }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onPlayClick(inputText, selectedColor, textSize, scrollSpeed) },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape), // 은은한 테두리
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonGreen
            ),
            shape = CircleShape,
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp) // 그림자 빡!
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = "Play",
                    tint = NeonBlack,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "START BLING",
                    color = NeonBlack,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp)) // 하단 여백
    }
}

// --- [Components] 파일 분리 안 하고 일단 여기에 둡니다 (복붙 편의성) ---

@Composable
fun NeonTextField(value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(text = "TEXT", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkGray, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                ),
                cursorBrush = SolidColor(NeonGreen),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text("Enter text here... 😎", color = Color.Gray)
                    }
                    innerTextField()
                }
            )
        }
    }
}

@Composable
fun ColorChip(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .clickable { onClick() }
            .then(
                if (isSelected) Modifier.border(3.dp, Color.White, CircleShape)
                else Modifier
            )
    )
}

@Composable
fun ControlSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float>,
    accentColor: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            // 현재 값 표시 (선택사항)
            // Text(text = "${value.toInt()}", color = TextGray, fontSize = 12.sp)
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = accentColor,
                activeTrackColor = accentColor,
                inactiveTrackColor = DarkGray
            )
        )
    }
}