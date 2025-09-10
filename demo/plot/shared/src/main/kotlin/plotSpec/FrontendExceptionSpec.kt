package plotSpec

import org.jetbrains.letsPlot.Figure

class FrontendExceptionSpec : PlotDemoSpec {
    override fun createFigure(): RawSpecFigure {
        return RawSpecFigure.fromMap(
            mapOf(
                "kind" to "plot",
                "layers" to listOf(
                    mapOf(
                        "geom" to "line",
                        "data" to mapOf(
                            "x" to listOf("a", "b", "c", "d"),
                            "y" to listOf(3, 1, 4, 2)
                        ),
                        "mapping" to mapOf("x" to "x", "y" to "y"),
                        "stat" to "summary",
                        "fun" to "mean",
                        "data_meta" to mapOf(
                            "series_annotations" to listOf(
                                mapOf(
                                    "type" to "datetime",
                                    "column" to "x"
                                )
                            )
                        )
                    )
                )
            )
        )
    }

    override fun createFigureList(): List<Figure> {

        return listOf(
            createFigure(),
        )
    }

}

