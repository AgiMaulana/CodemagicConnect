package io.github.agimaulana.codemagicconnect.core.navigation

class GDDestinationRoute private constructor(val route: String) {

    class Builder(private val baseRoute: String) {
        private val paths = mutableListOf<String>()

        fun addPath(path: String) = apply { paths.add("{$path}") }

        fun build(): String {
            return if (paths.isEmpty()) {
                baseRoute
            } else {
                buildString {
                    append(baseRoute)
                    paths.forEach { append("/$it") }
                }
            }
        }
    }
}
