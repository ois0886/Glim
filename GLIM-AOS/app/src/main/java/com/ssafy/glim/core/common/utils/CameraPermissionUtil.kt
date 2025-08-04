package com.ssafy.glim.core.common.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

@Composable
fun rememberCameraPermissionState(
    onPermissionGranted: () -> Unit = {},
    onPermissionDenied: () -> Unit = {}
): CameraPermissionState {
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }

    LaunchedEffect(Unit) {
        hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    return remember {
        CameraPermissionState(
            hasPermission = hasPermission,
            requestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) }
        )
    }
}

data class CameraPermissionState(
    val hasPermission: Boolean,
    val requestPermission: () -> Unit
)

@Composable
fun rememberCameraLauncher(
    onImageCaptured: (Uri) -> Unit,
    onCaptureFailed: () -> Unit = {}
): CameraLauncher {
    val context = LocalContext.current
    var currentImageUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            currentImageUri?.let { uri ->
                onImageCaptured(uri)
            }
        } else {
            onCaptureFailed()
        }
        currentImageUri = null
    }

    return remember {
        CameraLauncher(
            launch = {
                val uri = createImageUri(context)
                currentImageUri = uri
                cameraLauncher.launch(uri)
            }
        )
    }
}

data class CameraLauncher(
    val launch: () -> Unit
)

// 임시 이미지 파일 생성 함수
private fun createImageUri(context: Context): Uri {
    val imageFile = File(
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
        "camera_photo_${System.currentTimeMillis()}.jpg"
    )

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
}

// 통합 카메라 헬퍼 - 권한 + 카메라 실행을 한번에
@Composable
fun rememberCameraWithPermission(
    onImageCaptured: (Uri) -> Unit,
    onPermissionDenied: () -> Unit = {},
    onCaptureFailed: () -> Unit = {}
): CameraWithPermissionState {

    val cameraLauncher = rememberCameraLauncher(
        onImageCaptured = onImageCaptured,
        onCaptureFailed = onCaptureFailed
    )

    val permissionState = rememberCameraPermissionState(
        onPermissionGranted = {
            cameraLauncher.launch()
        },
        onPermissionDenied = onPermissionDenied
    )

    return remember {
        CameraWithPermissionState(
            hasPermission = permissionState.hasPermission,
            launchCamera = {
                if (permissionState.hasPermission) {
                    cameraLauncher.launch()
                } else {
                    permissionState.requestPermission()
                }
            }
        )
    }
}

data class CameraWithPermissionState(
    val hasPermission: Boolean,
    val launchCamera: () -> Unit
)
