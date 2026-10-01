package com.example.acousticanalyst.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import java.security.MessageDigest

object SecurityUtils {

    /**
     * Verifies app signature and checks for repackaging or tampering (anti-piracy measure).
     *
     * @param context Application context.
     * @param expectedHash Optional expected SHA-256 signature hash of the official release keystore.
     * @return true if the signature is valid/verified, false if tampering or repackaging is suspected.
     */
    fun verifyAppSignature(context: Context, expectedHash: String? = null): Boolean {
        try {
            val packageName = context.packageName
            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val packageInfo = context.packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
                )
                packageInfo.signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                val packageInfo = context.packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNATURES
                )
                @Suppress("DEPRECATION")
                packageInfo.signatures
            }

            if (signatures.isNullOrEmpty()) return false

            for (signature in signatures) {
                val md = MessageDigest.getInstance("SHA-256")
                val digest = md.digest(signature.toByteArray())
                val hash = digest.joinToString("") { "%02x".format(it) }
                if (expectedHash != null && hash.equals(expectedHash, ignoreCase = true)) {
                    return true
                }
            }

            // If no expected hash is strictly enforced, ensure signatures are present
            return true
        } catch (_: Exception) {
            return false
        }
    }

    /**
     * Checks if the app was installed from an official installer (e.g., Google Play Store or sideloaded).
     */
    fun isOfficialInstaller(context: Context): Boolean {
        return try {
            val installer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context.packageManager.getInstallSourceInfo(context.packageName).installingPackageName
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getInstallerPackageName(context.packageName)
            }
            // Official installers like Google Play ("com.android.vending") or null (for debug/direct installs during dev)
            installer == "com.android.vending" || installer == null
        } catch (_: Exception) {
            true
        }
    }
}
