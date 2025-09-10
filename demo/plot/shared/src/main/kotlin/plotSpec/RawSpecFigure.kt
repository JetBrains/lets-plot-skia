package plotSpec

import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport

class RawSpecFigure private constructor(
    val rawSpec: MutableMap<String, Any>
) : Figure {
    override fun show(): Unit = TODO("Not yet implemented")

    companion object {
        fun fromMap(spec: Map<String, Any>): RawSpecFigure {
            return RawSpecFigure(spec.toMutableMap())
        }

        fun fromJson(json: String): RawSpecFigure {
            @Suppress("UNCHECKED_CAST")
            return RawSpecFigure(JsonSupport.parseJson(json) as MutableMap<String, Any>)
        }
    }
}