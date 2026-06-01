package com.normalnywork.tundramarket.ui.screens.nomad.neworder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.normalnywork.tundramarket.R
import com.normalnywork.tundramarket.ui.kit.components.TMTextField
import com.normalnywork.tundramarket.ui.kit.icons.Latitude
import com.normalnywork.tundramarket.ui.kit.icons.Longitude
import com.normalnywork.tundramarket.ui.kit.icons.TMIcons
import com.normalnywork.tundramarket.ui.tools.CoordinateInputTransformation

@Composable
fun LocationPageContent(
    latitude: TextFieldState,
    longitude: TextFieldState,
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
                imeAction = ImeAction.Next
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
    }
}