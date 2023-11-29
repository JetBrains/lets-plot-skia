/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.compose

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.skia.compose.android.PlotComponentProvider
import org.jetbrains.letsPlot.skia.compose.android.PlotViewContainer
import org.jetbrains.letsPlot.skia.compose.util.NaiveLogger

private val LOG = NaiveLogger("PlotPanel")

@Suppress("FunctionName")
@Composable
actual fun PlotPanel(
    figure: Figure,
    preserveAspectRatio: Boolean,
    modifier: Modifier,
    computationMessagesHandler: (List<String>) -> Unit
) {
    LOG.print { "Recompose PlotPanel() preserveAspectRatio: $preserveAspectRatio " }

    val provider by remember {
        mutableStateOf(
            PlotComponentProvider(
                computationMessagesHandler
            )
        )
    }

    DisposableEffect(provider) {
        onDispose {
            LOG.print { "DisposableEffect preserveAspectRatio: ${provider.plotViewContainer?.preserveAspectRatio} " }
            provider.dispose()
        }
    }

    AndroidView(
        factory = provider.factory,
        modifier = modifier,
        update = { plotViewContainer ->
            plotViewContainer as PlotViewContainer
            LOG.print { "UPDATE PlotViewContainer preserveAspectRatio ${plotViewContainer.preserveAspectRatio} ->  $preserveAspectRatio" }

            plotViewContainer.figure = figure
            plotViewContainer.preserveAspectRatio = preserveAspectRatio
        }
    )
}

