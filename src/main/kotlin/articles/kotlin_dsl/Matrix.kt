package articles.kotlin_dsl

open class Matrix<T>(val width: Int, val height: Int, init: (Int, Int) -> T) {
    private val content: List<MutableList<T>>

    init {
        content = (0 until width).mapTo(ArrayList()) { row ->
            (0 until height).mapTo(ArrayList()) { column -> init(row, column) }
        }
    }

    operator fun get(i: Int, j: Int) = content[i][j]

    operator fun set(i: Int, j: Int, value: T) {
        content[i][j] = value
    }

    override fun toString() = buildString {
        for(i in 0 until height) {
            for(j in 0 until width) {
                append(content[j][i]).append("\t\t")
            }
            append("\n")
        }
    }
}
