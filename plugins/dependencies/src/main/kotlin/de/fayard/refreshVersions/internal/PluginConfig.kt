package de.fayard.refreshVersions.internal


@Suppress("unused")
internal object PluginConfig {

    const val REFRESH_VERSIONS = "refreshVersions"
    const val BUILD_SRC_VERSIONS = "buildSrcVersions"

    /** Documentation **/
    internal fun issue(number: Int): String = "$refreshVersionsUrl/issues/$number"

    const val refreshVersionsUrl = "https://github.com/jmfayard/refreshVersions"

    /**
     * We don't want to use meaningless generic libs like Libs.core
     *
     * Found many inspiration for bad libs here https://developer.android.com/jetpack/androidx/migrate
     * **/
    val MEANING_LESS_NAMES: MutableList<String> = mutableListOf(
        "common", "core", "testing", "runtime", "extensions",
        "compiler", "migration", "db", "rules", "runner", "monitor", "loader",
        "media", "print", "io", "collection", "gradle", "android"
    )

    val INITIAL_GITIGNORE = """
        |.gradle/
        |build/
        """.trimMargin()

    val KDOC_LIBS = """
        |Generated by
        |   $ ./gradlew $BUILD_SRC_VERSIONS
        |Re-run when you add a new dependency to the build
        |
        |Find which updates are available by running
        |    $ ./gradlew $REFRESH_VERSIONS
        |And edit the file `versions.properties`
        |
        |See $refreshVersionsUrl
    """.trimMargin()

    //
    val KDOC_VERSIONS = """

        |And edit the file `versions.properties`

    """.trimMargin()


    val INITIAL_BUILD_GRADLE_KTS = """
        |plugins {
        |    `kotlin-dsl`
        |}
        |repositories {
        |    mavenCentral()
        |}
        """.trimMargin()

    internal fun computeUseFqdnFor(
        dependencies: List<Dependency>,
        configured: List<String>,
        byDefault: List<String> = MEANING_LESS_NAMES
    ): List<String> {
        val groups = (configured + byDefault).filter { it.contains(".") }.distinct()
        val depsFromGroups = dependencies.filter { it.group in groups }.map { it.module }
        val ambiguities = dependencies.groupBy { it.module }.filter { it.value.size > 1 }.map { it.key }
        return (configured + byDefault + ambiguities + depsFromGroups - groups).distinct().sorted()
    }

    internal fun escapeLibsKt(name: String): String {
        val escapedChars = listOf('-', '.', ':')
        return buildString {
            for (c in name) {
                append(if (c in escapedChars) '_' else c.toLowerCase())
            }
        }
    }
}
