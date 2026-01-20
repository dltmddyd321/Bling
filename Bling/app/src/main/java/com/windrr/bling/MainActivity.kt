package com.windrr.bling

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.windrr.bling.ui.Screen
import com.windrr.bling.ui.theme.BlingLedTheme
import kotlinx.coroutines.delay

// 색상 정의 (나중에 Theme으로 뺄 예정이지만 일단 여기에)
val NeonBlack = Color(0xFF000000)
val NeonGreen = Color(0xFF00FF85)
val NeonPink = Color(0xFFFF00FF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BlingLedTheme {
                BlingApp()
            }
        }
    }
}

@Composable
fun BlingApp() {
    // 1. 스플래시 상태 관리
    var showSplash by remember { mutableStateOf(true) }

    if (showSplash) {
        // 스플래시 화면 (기존 코드 그대로 사용)
        SplashScreen {
            showSplash = false // 애니메이션 끝나면 false로 바뀌면서 아래 NavHost가 보임
        }
    } else {
        // 2. 메인 네비게이션 호스트
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = Screen.Home.route) {

            // [화면 1] 홈 (설정창)
            composable(route = Screen.Home.route) {
                HomeScreen(
                    onPlayClick = {
                        // Play 버튼 누르면 플레이어 화면으로 이동
                        navController.navigate(Screen.Player.route)
                    }
                )
            }

            // [화면 2] 플레이어 (전광판)
            composable(route = Screen.Player.route) {
                PlayerScreen(
                    onBackClick = {
                        // 뒤로가기 누르면 홈으로 복귀
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

// --- 아래는 일단 껍데기(Skeleton) UI 입니다. 나중에 파일 분리할 예정 ---

@Composable
fun HomeScreen(onPlayClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NeonBlack),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "SETTINGS (Home)\nTouch to Play ▶️",
            color = Color.White,
            modifier = Modifier.clickable { onPlayClick() } // 임시 클릭 이벤트
        )
    }
}

@Composable
fun PlayerScreen(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "LED SCROLLER RUNNING...\nTouch to Back",
            color = NeonGreen,
            modifier = Modifier.clickable { onBackClick() }
        )
    }
}

@Composable
fun SplashScreen(onAnimationFinished: () -> Unit) {
    val alpha = remember { Animatable(0f) }

    // "치직- 칙- 탁!" 하는 네온사인 점등 효과 정의
    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = keyframes {
                durationMillis = 2000 // 총 2초 (짧고 굵게)
                0.0f at 0
                0.2f at 400  // 살짝 켜짐
                0.0f at 600  // 꺼짐
                0.5f at 800  // 반쯤 켜짐
                0.0f at 1000 // 다시 꺼짐
                1.0f at 1200 with FastOutSlowInEasing // 확 켜짐 (완성)
            }
        )
        delay(500) // 켜진 상태로 잠시 유지
        onAnimationFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NeonBlack), // 배경: 리얼 블랙
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "BLING",
            fontSize = 64.sp,
            fontWeight = FontWeight.Black, // 가장 두꺼운 폰트
            color = NeonGreen.copy(alpha = alpha.value), // 애니메이션 적용
            style = androidx.compose.ui.text.TextStyle(
                shadow = Shadow(
                    color = NeonGreen, // 네온 글로우 효과
                    offset = Offset(0f, 0f),
                    blurRadius = 30f * alpha.value // 밝아질수록 빛번짐도 강해짐
                )
            )
        )
    }
}