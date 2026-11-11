import java.io.File
import java.util.Properties

object AppVersion {
    fun loadProperties(file: File): Properties =
        Properties().apply {
            file.inputStream().use { load(it) }
        }

    fun versionName(properties: Properties): String {
        val major = required(properties, "versionMajor")
        val minor = required(properties, "versionMinor")
        val patch = required(properties, "versionPatch")
        return "$major.$minor.$patch"
    }

    fun versionCode(properties: Properties): Int =
        required(properties, "versionCode").toIntOrNull()
            ?: error("Invalid versionCode in version.properties")

    fun iosVersionXcconfigContent(propertiesFile: File): String {
        val properties = loadProperties(propertiesFile)
        val versionName = versionName(properties)
        val versionCode = versionCode(properties)
        return """
            |//
            |//  Version.xcconfig
            |//  iosApp
            |//
            |//  Generated from version.properties — do not edit by hand.
            |//  Run: ./gradlew syncIosVersion
            |//
            |
            |APP_VERSION = $versionName
            |APP_BUILD = $versionCode
            |
        """.trimMargin()
    }

    private fun required(properties: Properties, key: String): String =
        properties.getProperty(key)
            ?: error("Missing '$key' in version.properties")
}
