//Versions.kt
object Versions {
    private const val MAJOR = 1
    private const val MINOR = 0
    private const val PATCH = 0
    const val snapshot = false
    val classifier: String? = null

    const val VERSION_CODE = MAJOR * 10000 + MINOR * 100 + PATCH
    const val VERSION_NAME = "${MAJOR}.${MINOR}.${PATCH}"
}