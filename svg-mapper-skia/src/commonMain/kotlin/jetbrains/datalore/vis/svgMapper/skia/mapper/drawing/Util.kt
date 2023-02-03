package jetbrains.datalore.vis.svgMapper.skia.mapper.drawing

import org.jetbrains.skia.Matrix33
import org.jetbrains.skia.Point

internal val Matrix33.translate: Point get() = Point(mat[2], mat[5])

internal val Element.absoluteOffsetX: Float get() {
    val totalParentsOffsetX = when (parent) {
        null -> 0f
        is Parent -> (parent as Parent).let { p -> p.offsetX + p.parents.sumOf { it.offsetX.toDouble() } }.toFloat()
        else -> parent!!.absoluteOffsetX
    }
    return totalParentsOffsetX + (transform?.translate?.x ?: 0f)
}

internal val Element.absoluteOffsetY: Float get() {
    val totalParentsOffsetY = when (parent) {
        null -> 0f
        is Parent -> (parent as Parent).let { p -> p.offsetY + p.parents.sumOf { it.offsetY.toDouble() } }.toFloat()
        else -> parent!!.absoluteOffsetY
    }
    return totalParentsOffsetY + (transform?.translate?.y ?: 0f)
}


internal val Element.parents: List<Parent> get() {
    val res = mutableListOf<Parent>()

    var root = parent
    while (root != null) {
        res.add(root)
        root = root.parent
    }
    return res
}

internal fun traceTree(el: Element): String {
    val buffer = StringBuilder()

    val root = when (el.parent) {
        null -> el
        else -> el.parents.lastOrNull()
    } ?: return ""

    traceNode(root, 0, buffer)

    return buffer.toString()
}

internal fun traceNode(el: Element, indent: Int = 0, buffer: StringBuilder = StringBuilder()): StringBuilder {
    buffer.appendLine(" ".repeat(indent) + el.toString())
    if (el is Parent) {
        el.children.forEach { traceNode(it, indent + 1, buffer) }
    }
    return buffer
}

@Suppress("unused")
internal fun traceElement(el: Element): String {
    val elements = mutableListOf(el)

    var root = when (el.parent) {
        null -> el
        else -> el.parents.lastOrNull()
    }

    while (root != null) {
        elements.add(root)
        root = root.parent
    }

    return elements
        .reversed()
        .mapIndexed { index, it -> " ".repeat(index) + it.toString() }
        .joinToString(separator = "\n")
}
