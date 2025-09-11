/*
 * Copyright (c) 2024 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package plotSpec

import org.jetbrains.letsPlot.commons.intern.json.JsonSupport

class MarkdownSpec : PlotDemoSpec {
    override fun createRawSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            mpg(),
            mpgTitleOnly(),
        )
    }

    fun mpgTitleOnly(): MutableMap<String, Any> {
        return JsonSupport.parseJson(
            """
            |{
            |  "theme": {
            |    "title": { "markdown": true, "blank": false },
            |    "axis_title": { "blank": true },
            |    "text": { "blank": true },
            |    "plot_title": { "size": 30.0, "hjust": 0.5, "blank": false }
            |  },
            |  "ggtitle": {
            |    "text": "<span style=\"color:#66c2a5\">**Forward**</span>, <span style=\"color:#8da0cb\">**Rear**</span> and <span style=\"color:#fc8d62\">**4WD**</span> Drivetrain"
            |  },
            |  "kind": "plot",
            |  "layers": [
            |    {
            |      "geom": "blank",
            |      "inherit_aes": false,
            |      "tooltips": "none"
            |    }
            |  ]
            |}
        """.trimMargin()
        ) as MutableMap<String, Any>
    }

    fun mpg(): MutableMap<String, Any> {
        return JsonSupport.parseJson(
            """
            |{
            |  "theme": {
            |    "title": { "markdown": true, "blank": false },
            |    "plot_title": { "size": 30.0, "hjust": 0.5, "blank": false },
            |    "plot_subtitle": { "hjust": 0.5, "blank": false }
            |  },
            |  "ggtitle": {
            |    "text": "<span style=\"color:#66c2a5\">**Forward**</span>, <span style=\"color:#8da0cb\">**Rear**</span> and <span style=\"color:#fc8d62\">**4WD**</span> Drivetrain",
            |    "subtitle": "**City milage** *vs* **displacement**"
            |  },
            |  "caption": {
            |    "text": "<span style='color:grey'>Powered by <a href='https://lets-plot.org'>Lets-Plot</a>.  \nVisit the <a href='https://github.com/jetbrains/lets-plot/issues'>issue tracker</a> for feedback.</span>"
            |  },
            |  "guides": {
            |    "x": { "title": "Displacement (***inches***)" },
            |    "y": { "title": "Miles per gallon (***cty***)" }
            |  },
            |  "kind": "plot",
            |  "scales": [
            |    {
            |      "aesthetic": "color",
            |      "guide": "none",
            |      "values": [ "#66c2a5", "#fc8d62", "#8da0cb" ]
            |    }
            |  ],
            |  "layers": [
            |    {
            |      "geom": "blank",
            |      "inherit_aes": false,
            |      "tooltips": "none"
            |    }
            |  ]
            |}
        """.trimMargin()
        ) as MutableMap<String, Any>
    }
}
