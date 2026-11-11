import java.io.File

object Namespace {
    const val app = "com.bunbeauty.papakarlo"
    const val shared = "com.bunbeauty.shared"
}

object CommonApplication {
    private val versionProperties by lazy {
        var dir = File(System.getProperty("user.dir") ?: ".").canonicalFile
        repeat(8) {
            val candidate = File(dir, "version.properties")
            if (candidate.isFile) {
                return@lazy AppVersion.loadProperties(candidate)
            }
            dir = dir.parentFile ?: return@repeat
        }
        error("version.properties not found walking up from ${System.getProperty("user.dir")}")
    }

    val versionCode: Int get() = AppVersion.versionCode(versionProperties)
    val versionName: String get() = AppVersion.versionName(versionProperties)

    const val deploymentTarget = "15.5"
}

object AndroidSdk {
    const val min = 26
    const val compile = 37
    const val compileMinor = 0
    const val target = compile
}
