package com.normalnywork.tundramarket.ui.screens.nomad.neworder

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.normalnywork.tundramarket.R
import com.normalnywork.tundramarket.ui.kit.components.TMTextField
import com.normalnywork.tundramarket.ui.tools.SmsOrderCommentInputTransformation

@Composable
fun CommentPageContent(comment: TextFieldState) {
    TMTextField(
        state = comment,
        placeholder = stringResource(R.string.nomad_create_order_comment_placeholder),
        multiline = true,
        height = 196.dp,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        inputTransformation = SmsOrderCommentInputTransformation,
    )
}
