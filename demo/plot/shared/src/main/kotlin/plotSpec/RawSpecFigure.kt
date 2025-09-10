package plotSpec

import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport

class RawSpecFigure private constructor(
    val rawSpec: Map<String, Any>
) : Figure {
    override fun show(): Unit = TODO("Not yet implemented")

    companion object {
        fun fromMap(spec: Map<String, Any>): RawSpecFigure {
            return RawSpecFigure(spec)
        }

        fun fromJson(json: String): RawSpecFigure {
            @Suppress("UNCHECKED_CAST")
            return RawSpecFigure(JsonSupport.parseJson(json) as Map<String, Any>)
        }
    }
}