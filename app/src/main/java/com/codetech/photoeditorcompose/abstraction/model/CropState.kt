package com.codetech.photoeditorcompose.abstraction.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

data class CropState(
    var topLeft: Offset = Offset.Zero,
    var size: Size = Size(300f, 300f),
    var imageSize: Size = Size.Zero,
    var imageOffset: Offset = Offset.Zero
)