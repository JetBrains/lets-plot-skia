/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.skia

import android.content.Context
import android.view.View
import jetbrains.datalore.base.geometry.DoubleVector

internal interface PlotComponentProvider {
    fun getPreferredSize(containerSize: DoubleVector): DoubleVector
    fun createComponent(ctx: Context, containerSize: DoubleVector?): View
}