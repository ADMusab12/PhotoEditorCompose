package com.codetech.photoeditorcompose.abstraction.model

import android.graphics.Bitmap
import com.codetech.photoeditorcompose.abstraction.enums.FilterType

data class EditState(
    val bitmap: Bitmap,
    val filter: FilterType,
    val rotation: Float
)