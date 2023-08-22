/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.letsPlot.Figure


@Suppress("FunctionName")
@Composable
expect fun PlotPanel(
    figure: Figure,
    preserveAspectRatio: Boolean = false,
    modifier: Modifier,
    computationMessagesHandler: (List<String>) -> Unit
)