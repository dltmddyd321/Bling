package com.windrr.bling.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.windrr.bling.NeonPink
import com.windrr.bling.ui.theme.DarkGray
import com.windrr.bling.ui.theme.NeonBlack
import com.windrr.bling.ui.theme.NeonBlue
import com.windrr.bling.ui.theme.NeonGreen
import com.windrr.bling.ui.theme.NeonYellow
import com.windrr.bling.ui.theme.TextGray

@Composable
fun HomeScreen(
    onPlayClick: (text: String, color: Color, size: Float, speed: Float, isBlinkMode: Boolean) -> Unit
) {
    var inputText by remember { mutableStateOf("I LOVE YOU") }
    var selectedColor by remember { mutableStateOf(NeonGreen) }
    var textSize by remember { mutableFloatStateOf(100f) } // 50f ~ 200f
    var scrollSpeed by remember { mutableFloatStateOf(0.5f) } // 0f ~ 1f
    var showColorPicker by remember { mutableStateOf(false) }
    val colorPickerController = rememberColorPickerController()

    val colorPalette =
        listOf(NeonGreen, NeonPink, NeonBlue, NeonYellow, Color.White, Color.Red, Color(0xFFFF9800))

    var isBlinkMode by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeonBlack)
            .navigationBarsPadding()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .padding(top = 32.dp)
    ) {
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
                )
                .background(DarkGray, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = inputText.ifEmpty { "Text Here" },
                color = selectedColor,
                fontSize = (textSize / 3).sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                modifier = Modifier.wrapContentWidth(unbounded = true)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        NeonTextField(
            value = inputText,
            onValueChange = { if (it.length <= 30) inputText = it } // 글자수 제한
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(DarkGray, CircleShape)
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ModeTabButton(
                text = "SCROLL 📜",
                isSelected = !isBlinkMode,
                modifier = Modifier.weight(1f),
                onClick = { isBlinkMode = false }
            )

            ModeTabButton(
                text = "BLINK ⚡️",
                isSelected = isBlinkMode,
                modifier = Modifier.weight(1f),
                onClick = { isBlinkMode = true }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(text = "COLOR", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)

            // 깜빡이는 힌트 텍스트로 시선 끌기
            val infiniteTransition = rememberInfiniteTransition(label = "scroll_hint")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f, targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
                label = "alpha"
            )

            Text(
                text = "SWIPE >>>",
                color = NeonGreen.copy(alpha = alpha),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. 리스트에 '페이딩 엣지(안개)' 효과 주기
        Box(contentAlignment = Alignment.CenterEnd) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                // 오른쪽 끝에 그라데이션이 덮일 공간(50dp)만큼 패딩을 더 줍니다.
                contentPadding = PaddingValues(end = 50.dp)
            ) {
                // 기존 프리셋 색상들
                items(colorPalette) { color ->
                    ColorChip(
                        color = color,
                        isSelected = color == selectedColor,
                        onClick = { selectedColor = color }
                    )
                }

                // 무지개 버튼 (커스텀)
                item {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.sweepGradient(
                                    listOf(Color.Red, Color.Green, Color.Blue, Color.Red)
                                )
                            )
                            .clickable { showColorPicker = true }
                            .then(
                                if (!colorPalette.contains(selectedColor))
                                    Modifier.border(3.dp, Color.White, CircleShape)
                                else Modifier
                            )
                    )
                }
            }

            // ★ [핵심] 오른쪽 끝을 자연스럽게 가리는 '어둠의 안개'
            Box(
                modifier = Modifier
                    .width(50.dp) // 너비는 적당히
                    .height(40.dp) // 칩 높이랑 똑같이
                    .background(
                        Brush.horizontalGradient(
                            // 투명 -> 리얼 블랙 (자연스럽게 사라짐)
                            colors = listOf(Color.Transparent, NeonBlack)
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        ControlSlider(
            label = "SIZE",
            value = textSize,
            onValueChange = { textSize = it },
            range = 50f..300f,
            accentColor = NeonBlue
        )

        Spacer(modifier = Modifier.height(16.dp))

        ControlSlider(
            label = "SPEED",
            value = scrollSpeed,
            onValueChange = { scrollSpeed = it },
            range = 0.1f..2.0f,
            accentColor = NeonPink
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = { onPlayClick(inputText, selectedColor, textSize, scrollSpeed, isBlinkMode) },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonGreen
            ),
            shape = CircleShape,
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp)
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

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showColorPicker) {
        Dialog(onDismissRequest = { showColorPicker = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkGray)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "CUSTOM NEON",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    HsvColorPicker(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(10.dp),
                        controller = colorPickerController,
                        onColorChanged = { colorEnvelope: ColorEnvelope ->
                             selectedColor = colorEnvelope.color
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            selectedColor = colorPickerController.selectedColor.value
                            showColorPicker = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("SELECT", color = NeonBlack, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun NeonTextField(value: String, onValueChange: (String) -> Unit) {
    val focusRequester = remember { FocusRequester() }

    Column {
        Text(text = "TEXT", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkGray, RoundedCornerShape(12.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    focusRequester.requestFocus()
                }
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
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

@Composable
fun ModeTabButton(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(CircleShape)
            .background(if (isSelected) NeonGreen else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) NeonBlack else TextGray,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}