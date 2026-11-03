object Namespace {
    const val app = "com.bunbeauty.papakarlo"
    const val shared = "com.bunbeauty.shared"
}

object CommonApplication {
    private const val versionMajor = 3
    private const val versionMinor = 1
    private const val versionPatch = 7

    const val versionCode = 317
    const val versionName = "$versionMajor.$versionMinor.$versionPatch"

    const val deploymentTarget = "15.5"
}

object AndroidSdk {
    const val min = 26
    const val compile = 37
    const val compileMinor = 0
    const val target = compile
}
