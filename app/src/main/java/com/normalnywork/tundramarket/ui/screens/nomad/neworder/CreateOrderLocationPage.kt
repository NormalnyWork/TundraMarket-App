package com.normalnywork.tundramarket.ui.screens.nomad.neworder

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.normalnywork.tundramarket.R
import com.normalnywork.tundramarket.ui.kit.components.TMAlertDialog
import com.normalnywork.tundramarket.ui.kit.components.TMAlertDialogTextButton
import com.normalnywork.tundramarket.ui.kit.components.TMButtonSecondary
import com.normalnywork.tundramarket.ui.kit.components.TMTextField
import com.normalnywork.tundramarket.ui.kit.icons.Latitude
import com.normalnywork.tundramarket.ui.kit.icons.Longitude
import com.normalnywork.tundramarket.ui.kit.icons.TMIcons
import com.normalnywork.tundramarket.ui.kit.icons.UserLocation
import com.normalnywork.tundramarket.ui.kit.style.LocalTMColors
import com.normalnywork.tundramarket.ui.kit.style.LocalTMTypography
import com.normalnywork.tundramarket.ui.kit.style.TMPreviewWrapperProvider
import com.normalnywork.tundramarket.ui.tools.CoordinateInputTransformation
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun LocationPageContent(component: NomadCreateOrderComponent) {
    val activity = LocalActivity.current
    val context = LocalContext.current
    val isAutomaticLocationDetectionForbidden by component.isAutomaticLocationDetectionForbidden.collectAsState()
    val locationDetectionState by component.locationDetectionState.collectAsState()
    var showLocationPermissionDialog by rememberSaveable { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        val isPreciseLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        if (isPreciseLocationGranted) {
            showLocationPermissionDialog = false
            component.onAutomaticLocationPermissionGranted()
            component.onStartAutomaticLocationDetection()
        } else {
            showLocationPermissionDialog = !component.onAutomaticLocationPermissionRejected()
        }
    }

    fun requestLocationPermissions() {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ),
        )
    }

    fun requestAutomaticLocationDetection() {
        if (isAutomaticLocationDetectionForbidden) return

        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            component.onAutomaticLocationPermissionGranted()
            component.onStartAutomaticLocationDetection()
        } else if (
            activity != null &&
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        ) {
            showLocationPermissionDialog = true
        } else {
            requestLocationPermissions()
        }
    }

    LocationPageFields(
        latitude = component.latitude,
        longitude = component.longitude,
        showAutomaticLocationButton = !isAutomaticLocationDetectionForbidden,
        onAutomaticLocationClick = ::requestAutomaticLocationDetection,
    )

    if (showLocationPermissionDialog) {
        TMAlertDialog(
            title = stringResource(R.string.nomad_create_order_location_permission_dialog_title),
            body = stringResource(R.string.nomad_create_order_location_permission_dialog_body),
            onDismiss = { showLocationPermissionDialog = false },
            confirmAction = {
                TMAlertDialogTextButton(
                    text = stringResource(R.string.nomad_create_order_location_permission_dialog_confirm_action),
                    onClick = ::requestLocationPermissions,
                )
            },
        )
    }

    (locationDetectionState as? NomadCreateOrderComponent.LocationDetectionState.Detecting)
        ?.let { state ->
            LocationDetectionDialog(
                state = state,
                onDismiss = component::onCancelAutomaticLocationDetection,
                onApply = component::onApplyDetectedLocationClicked,
            )
        }
}

@Composable
private fun LocationPageFields(
    latitude: TextFieldState,
    longitude: TextFieldState,
    showAutomaticLocationButton: Boolean,
    onAutomaticLocationClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TMTextField(
            state = latitude,
            label = stringResource(R.string.nomad_create_order_latitude_label),
            icon = TMIcons.Latitude,
            placeholder = stringResource(R.string.nomad_create_order_latitude_placeholder),
            inputTransformation = CoordinateInputTransformation,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next,
            ),
        )
        TMTextField(
            state = longitude,
            label = stringResource(R.string.nomad_create_order_longitude_label),
            icon = TMIcons.Longitude,
            placeholder = stringResource(R.string.nomad_create_order_longitude_placeholder),
            inputTransformation = CoordinateInputTransformation,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done,
            ),
        )
        AnimatedVisibility(
            visible = showAutomaticLocationButton,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            TMButtonSecondary(
                text = stringResource(R.string.nomad_create_order_locate_action),
                icon = TMIcons.UserLocation,
                onClick = onAutomaticLocationClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun LocationDetectionDialog(
    state: NomadCreateOrderComponent.LocationDetectionState.Detecting,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
) {
    val colors = LocalTMColors.current
    val typography = LocalTMTypography.current
    val accuracyText = state.accuracyMeters?.let { accuracyMeters ->
        stringResource(
            R.string.nomad_create_order_location_detection_accuracy_value,
            accuracyMeters.roundToInt(),
        )
    } ?: stringResource(R.string.nomad_create_order_location_detection_accuracy_unknown)

    TMAlertDialog(
        title = stringResource(R.string.nomad_create_order_location_detection_dialog_title),
        body = stringResource(R.string.nomad_create_order_location_detection_dialog_body),
        onDismiss = onDismiss,
        confirmAction = {
            TMAlertDialogTextButton(
                text = stringResource(R.string.nomad_create_order_location_detection_apply_action),
                enabled = state.location != null,
                onClick = {
                    if (state.location != null) {
                        onApply()
                    }
                },
            )
        },
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            LocationAccuracyIndicator(
                accuracyMeters = state.accuracyMeters,
                targetAccuracyMeters = state.targetAccuracyMeters,
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp, alignment = Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = accuracyText,
                style = typography.bodySmall,
                color = colors.textSecondary,
            )
            CircularProgressIndicator(
                modifier = Modifier.size(14.dp),
                trackColor = colors.primaryVariant,
                color = colors.primary,
                strokeWidth = 2.dp,
            )
        }
    }
}

@Composable
private fun LocationAccuracyIndicator(
    accuracyMeters: Float?,
    targetAccuracyMeters: Float,
) {
    val colors = LocalTMColors.current
    val pulseTransition = rememberInfiniteTransition(label = "LocationAccuracyPulse")
    val unknownRadiusFraction by pulseTransition.animateFloat(
        initialValue = UnknownRadiusMinFraction,
        targetValue = UnknownRadiusMaxFraction,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = UnknownPulseDurationMillis),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "UnknownAccuracyRadius",
    )
    val measuredRadiusFraction by animateFloatAsState(
        targetValue = accuracyMeters.toRadiusFraction(targetAccuracyMeters),
        label = "MeasuredAccuracyRadius",
    )
    val radiusFraction = if (accuracyMeters == null) {
        unknownRadiusFraction
    } else {
        measuredRadiusFraction
    }

    Canvas(modifier = Modifier.size(132.dp)) {
        val center = this.center
        val maxRadius = min(size.width, size.height) / 2f
        val targetRadius = maxRadius * TargetRadiusFraction
        val targetRingRadius = maxRadius * TargetRingRadiusFraction
        val accuracyRadius = maxRadius * radiusFraction
        val strokeWidth = 3.dp.toPx()

        drawCircle(
            color = colors.primaryVariant,
            radius = maxRadius,
            center = center,
        )
        drawCircle(
            color = colors.primary.copy(alpha = 0.12f),
            radius = accuracyRadius,
            center = center,
        )
        drawCircle(
            color = colors.primary.copy(alpha = 0.7f),
            radius = accuracyRadius,
            center = center,
            style = Stroke(width = strokeWidth),
        )
        drawCircle(
            color = colors.stroke,
            radius = targetRingRadius,
            center = center,
            style = Stroke(width = 1.dp.toPx()),
        )
        drawCircle(
            color = colors.primary,
            radius = targetRadius,
            center = center,
        )
        drawCircle(
            color = colors.background,
            radius = targetRadius * 0.42f,
            center = center,
        )
    }
}

private fun Float?.toRadiusFraction(targetAccuracyMeters: Float): Float {
    if (this == null) return UnknownRadiusMaxFraction

    val clampedAccuracy = coerceIn(targetAccuracyMeters, MaxVisualAccuracyMeters)
    val uncertainty = (clampedAccuracy - targetAccuracyMeters) /
        (MaxVisualAccuracyMeters - targetAccuracyMeters)

    return TargetRingRadiusFraction +
        (UnknownRadiusMaxFraction - TargetRingRadiusFraction) * uncertainty
}

private const val MaxVisualAccuracyMeters = 50f
private const val TargetRadiusFraction = 0.055f
private const val TargetRingRadiusFraction = 0.14f
private const val UnknownRadiusMinFraction = 0.34f
private const val UnknownRadiusMaxFraction = 0.48f
private const val UnknownPulseDurationMillis = 950

@PreviewWrapper(TMPreviewWrapperProvider::class)
@Preview
@Composable
private fun Preview() {
    LocationPageFields(
        latitude = rememberTextFieldState(),
        longitude = rememberTextFieldState(),
        showAutomaticLocationButton = true,
        onAutomaticLocationClick = {},
    )
}

@PreviewWrapper(TMPreviewWrapperProvider::class)
@Preview
@Composable
private fun AccuracyIndicatorPreview() {
    LocationAccuracyIndicator(
        accuracyMeters = 8f,
        targetAccuracyMeters = 1f,
    )
}
