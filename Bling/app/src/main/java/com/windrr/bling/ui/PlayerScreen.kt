package com.windrr.bling.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.view.WindowManager
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer

@Composable
fun PlayerScreen(
    text: String,
    color: Color,
    size: Float,
    speed: Float,
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
        // 2. 텍스트 스크롤 애니메이션 컴포저블
        MarqueeText(
            text = text,
            color = color,
            fontSize = size,
            speed = speed
        )
    }
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