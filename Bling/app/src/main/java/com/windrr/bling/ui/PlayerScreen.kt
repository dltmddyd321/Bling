package com.windrr.bling.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.view.WindowManager
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun PlayerScreen(
    text: String,
    color: Color,
    size: Float,
    speed: Float,
    isBlinkMode: Boolean,
    onBackClick: () -> Unit
) {
    // 1. 화면 켜짐 유지 & 전체 화면 모드 설정
    KeepScreenOn()
    HideSystemBars()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // 전광판은 무조건 리얼 블랙
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null // 터치 시 물결 효과 제거 (깔끔하게)
            ) { onBackClick() }, // 화면 아무 데나 누르면 뒤로가기
        contentAlignment = Alignment.Center
    ) {
        if (isBlinkMode) {
            BlinkText(text, color, size, speed)
        } else {
            MarqueeText(text, color, size, speed)
        }
    }
}

@Composable
fun BlinkText(
    text: String,
    color: Color,
    fontSize: Float,
    speed: Float
) {
    // 속도가 빠를수록(2.0) 더 미친듯이 깜빡이게 설정
    // 기본 주기: 1000ms -> 속도 2배면 500ms
    val duration = (1000 / speed).toInt().coerceAtLeast(200)

    val infiniteTransition = rememberInfiniteTransition(label = "blink")

    // 1. 불투명도(Alpha) 애니메이션: 고장 난 형광등처럼 치직거림
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = duration
                // 불규칙한 타이밍 (치직- 팟! 치직-)
                1.0f at 0
                0.1f at (duration * 0.1f).toInt() // 꺼짐
                1.0f at (duration * 0.2f).toInt() // 켜짐
                0.3f at (duration * 0.5f).toInt() // 흐릿
                1.0f at (duration * 0.8f).toInt() // 켜짐
                0.0f at (duration * 0.9f).toInt() // 꺼짐
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    // 2. 크기(Scale) 애니메이션: 비트에 맞춰 움찔거리는 느낌
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f, // 10% 정도 커짐
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = duration
                1.0f at 0
                1.0f at (duration * 0.4f).toInt()
                1.1f at (duration * 0.5f).toInt() // 쿵! 하고 커짐
                1.0f at (duration * 0.6f).toInt()
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "scale"
    )

    // 3. 네온 글로우(빛 번짐) 효과
    // Alpha값이 높을 때(밝을 때) 빛 번짐도 같이 강해지게 연동
    val blurRadius = (30f * alpha)

    Text(
        text = text,
        color = color.copy(alpha = alpha), // 깜빡임 적용
        fontSize = fontSize.sp,
        fontWeight = FontWeight.Black,
        textAlign = TextAlign.Center,
        style = TextStyle(
            shadow = Shadow(
                color = color,
                offset = Offset.Zero,
                blurRadius = blurRadius // 빛 번짐 적용
            )
        ),
        modifier = Modifier
            .scale(scale) // 쿵! 움찔거림 적용
            // 화면 밖으로 나가도 잘리지 않게 (원한다면 줄바꿈 하려면 wrapContentWidth 대신 fillMaxWidth 쓰면 됨)
            .wrapContentWidth(unbounded = true)
    )
}

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun MarqueeText(
    text: String,
    color: Color,
    fontSize: Float,
    speed: Float
) {
    val density = LocalDensity.current
    val screenWidth = LocalResources.current.displayMetrics.widthPixels

    // 1. [핵심] 글자 길이를 '추측'하지 않고 '정확히 측정'하는 도구
    val textMeasurer = rememberTextMeasurer()

    // 2. 텍스트 스타일 정의 (측정용 & 그리기용 통일)
    val textStyle = TextStyle(
        fontSize = fontSize.sp,
        fontWeight = FontWeight.Black,
        textAlign = TextAlign.Center
    )

    // 3. 실제 텍스트가 차지하는 너비(px)를 계산
    val textLayoutResult = remember(text, fontSize) {
        textMeasurer.measure(text, textStyle)
    }
    val actualTextWidth = textLayoutResult.size.width

    // 4. 애니메이션 시작/끝 지점 설정
    val startX = screenWidth.toFloat()
    // 끝 지점: 글자 길이만큼 왼쪽으로 가고, 혹시 모르니 화면 너비만큼 더 밀어버림 (절대 안 잘리게)
    val endX = -(actualTextWidth.toFloat() + screenWidth.toFloat())

    // 5. 속도에 따른 시간 계산 (길이가 길수록 시간 더 줌)
    // 텍스트 길이 + 화면 너비를 이동해야 하므로 전체 이동 거리를 기준으로 계산
    val totalDistance = startX - endX
    val duration = (totalDistance / (speed * 0.5f)).toInt().coerceAtLeast(1000)

    val infiniteTransition = rememberInfiniteTransition(label = "marquee")

    val offsetX by infiniteTransition.animateFloat(
        initialValue = startX,
        targetValue = endX,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offsetX"
    )

    // 6. 텍스트 그리기
    Text(
        text = text,
        color = color,
        style = textStyle,
        maxLines = 1,
        softWrap = false,
        modifier = Modifier
            .wrapContentWidth(unbounded = true)
            .offset(x = (offsetX / density.density).dp)
    )
}

@Composable
fun KeepScreenOn() {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}

@Composable
fun HideSystemBars() {
    val view = LocalView.current
    DisposableEffect(Unit) {
        val window = (view.context as? Activity)?.window ?: return@DisposableEffect onDispose {}
        val insetsController = WindowCompat.getInsetsController(window, view)

        // 상태바, 네비게이션바 숨기기
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        onDispose {
            // 화면 나갈 때 다시 보이게 하기
            insetsController.show(WindowInsetsCompat.Type.systemBars())
        }
    }
}