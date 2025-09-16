package org.jetbrains.letsPlot.android.canvas

import org.jetbrains.letsPlot.commons.values.Color

object Utils {
    internal fun Color.toAndroidColor() = android.graphics.Color.argb(alpha, red, green, blue)
}
