package butterknife.plugin

import java.io.File
import java.util.Properties


internal fun androidHome(): String {
    val env = System.getenv("ANDROID_HOME")
    if (env != null) {
        return env
    }
    val localProp = File(File(System.getProperty("user.dir")).parentFile, "local.properties")
    if (localProp.exists()) {
        val prop = Properties()
        localProp.inputStream().use {
            prop.load(it)
        }
        val sdkHome = prop.getProperty("sdk.dir")
        if (sdkHome != null) {
            return sdkHome
        }
    }
    throw IllegalStateException(
            "Missing 'ANDROID_HOME' environment variable or local.properties with 'sdk.dir'")
}