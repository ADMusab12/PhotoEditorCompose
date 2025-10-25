package com.codetech.photoeditorcompose.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.Filter1
import androidx.compose.material.icons.filled.Filter2
import androidx.compose.material.icons.filled.Filter3
import androidx.compose.material.icons.filled.Filter4
import androidx.compose.material.icons.filled.Filter5
import androidx.compose.material.icons.filled.Filter6
import androidx.compose.material.icons.filled.Filter7
import androidx.compose.material.icons.filled.Filter8
import androidx.compose.material.icons.filled.Filter9
import androidx.compose.material.icons.filled.FilterBAndW
import androidx.compose.material.icons.filled.FilterCenterFocus
import androidx.compose.material.icons.filled.FilterDrama
import androidx.compose.material.icons.filled.FilterHdr
import androidx.compose.material.icons.filled.Photo
import androidx.compose.ui.graphics.vector.ImageVector
import com.codetech.photoeditorcompose.abstraction.enums.FilterType

fun getIconForFilter(filter: FilterType): ImageVector = when (filter) {
    FilterType.NONE -> Icons.Default.Photo
    FilterType.GRAYSCALE -> Icons.Default.Filter1
    FilterType.SEPIA -> Icons.Default.Filter2
    FilterType.INVERT -> Icons.Default.Filter3
    FilterType.VINTAGE -> Icons.Default.Filter4
    FilterType.COOL -> Icons.Default.Filter5
    FilterType.WARM -> Icons.Default.Filter6
    FilterType.BRIGHT -> Icons.Default.Filter7
    FilterType.DARK -> Icons.Default.Filter8
    FilterType.CONTRAST -> Icons.Default.Filter9
    FilterType.SATURATE -> Icons.Default.Filter
    FilterType.VIVID -> Icons.Default.FilterBAndW
    FilterType.FADE -> Icons.Default.FilterCenterFocus
    FilterType.BLUE_TINT -> Icons.Default.FilterDrama
    FilterType.RED_TINT -> Icons.Default.FilterHdr
}