package com.codetech.photoeditorcompose.util

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import com.codetech.photoeditorcompose.abstraction.enums.FilterType

fun getColorFilterForType(filter: FilterType): ColorFilter? {
    return when (filter) {
        FilterType.NONE -> null
        FilterType.GRAYSCALE -> ColorFilter.colorMatrix(
            ColorMatrix(
                floatArrayOf(
                    0.299f, 0.587f, 0.114f, 0f, 0f,
                    0.299f, 0.587f, 0.114f, 0f, 0f,
                    0.299f, 0.587f, 0.114f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
        )

        FilterType.SEPIA -> ColorFilter.colorMatrix(
            ColorMatrix(
                floatArrayOf(
                    0.393f, 0.769f, 0.189f, 0f, 0f,
                    0.349f, 0.686f, 0.168f, 0f, 0f,
                    0.272f, 0.534f, 0.131f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
        )

        FilterType.INVERT -> ColorFilter.colorMatrix(
            ColorMatrix(
                floatArrayOf(
                    -1f, 0f, 0f, 0f, 255f,
                    0f, -1f, 0f, 0f, 255f,
                    0f, 0f, -1f, 0f, 255f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
        )

        FilterType.VINTAGE -> ColorFilter.colorMatrix(
            ColorMatrix(
                floatArrayOf(
                    0.56f, 0.23f, 0.19f, 0f, 0f,
                    0.37f, 0.56f, 0.07f, 0f, 0f,
                    0.07f, 0.23f, 0.70f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
        )

        FilterType.COOL -> ColorFilter.colorMatrix(
            ColorMatrix(
                floatArrayOf(
                    0.8f, 0f, 0f, 0f, 0f,
                    0f, 1f, 0f, 0f, 0f,
                    0f, 0f, 1.4f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
        )

        FilterType.WARM -> ColorFilter.colorMatrix(
            ColorMatrix(
                floatArrayOf(
                    1.4f, 0f, 0f, 0f, 0f,
                    0f, 1f, 0f, 0f, 0f,
                    0f, 0f, 0.8f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
        )

        FilterType.BRIGHT -> ColorFilter.colorMatrix(
            ColorMatrix(
                floatArrayOf(
                    1f, 0f, 0f, 0f, 76.5f,
                    0f, 1f, 0f, 0f, 76.5f,
                    0f, 0f, 1f, 0f, 76.5f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
        )

        FilterType.DARK -> ColorFilter.colorMatrix(
            ColorMatrix(
                floatArrayOf(
                    1f, 0f, 0f, 0f, -76.5f,
                    0f, 1f, 0f, 0f, -76.5f,
                    0f, 0f, 1f, 0f, -76.5f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
        )

        FilterType.CONTRAST -> {
            val contrast = 1.5f
            val scalar = (255f * (1f - contrast) / 2f)
            ColorFilter.colorMatrix(
                ColorMatrix(
                    floatArrayOf(
                        contrast, 0f, 0f, 0f, scalar,
                        0f, contrast, 0f, 0f, scalar,
                        0f, 0f, contrast, 0f, scalar,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
            )
        }

        FilterType.SATURATE -> {
            val saturation = 1.5f
            val lumR = 0.3086f * (1f - saturation)
            val lumG = 0.6094f * (1f - saturation)
            val lumB = 0.082f * (1f - saturation)
            ColorFilter.colorMatrix(
                ColorMatrix(
                    floatArrayOf(
                        lumR + saturation, lumG, lumB, 0f, 0f,
                        lumR, lumG + saturation, lumB, 0f, 0f,
                        lumR, lumG, lumB + saturation, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
            )
        }

        FilterType.VIVID -> {
            val saturation = 2.0f
            val lumR = 0.3086f * (1f - saturation)
            val lumG = 0.6094f * (1f - saturation)
            val lumB = 0.082f * (1f - saturation)
            ColorFilter.colorMatrix(
                ColorMatrix(
                    floatArrayOf(
                        lumR + saturation, lumG, lumB, 0f, 0f,
                        lumR, lumG + saturation, lumB, 0f, 0f,
                        lumR, lumG, lumB + saturation, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
            )
        }

        FilterType.FADE -> {
            val saturation = 0.5f
            val lumR = 0.3086f * (1f - saturation)
            val lumG = 0.6094f * (1f - saturation)
            val lumB = 0.082f * (1f - saturation)
            ColorFilter.colorMatrix(
                ColorMatrix(
                    floatArrayOf(
                        lumR + saturation, lumG, lumB, 0f, 0f,
                        lumR, lumG + saturation, lumB, 0f, 0f,
                        lumR, lumG, lumB + saturation, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
            )
        }

        FilterType.BLUE_TINT -> ColorFilter.colorMatrix(
            ColorMatrix(
                floatArrayOf(
                    0.8f, 0f, 0f, 0f, 0f,
                    0f, 0.8f, 0f, 0f, 0f,
                    0f, 0f, 1f, 0f, 51f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
        )

        FilterType.RED_TINT -> ColorFilter.colorMatrix(
            ColorMatrix(
                floatArrayOf(
                    1f, 0f, 0f, 0f, 51f,
                    0f, 0.8f, 0f, 0f, 0f,
                    0f, 0f, 0.8f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
        )
    }
}