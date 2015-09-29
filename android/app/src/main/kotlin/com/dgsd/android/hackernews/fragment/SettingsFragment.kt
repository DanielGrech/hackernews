package com.dgsd.android.hackernews.fragment

import android.os.Bundle
import android.preference.*
import android.support.annotation.StringRes
import com.dgsd.android.hackernews.BuildConfig
import com.dgsd.android.hackernews.HNApp
import com.dgsd.android.hackernews.R
import com.dgsd.android.hackernews.data.AppSettings
import com.dgsd.android.hackernews.util.getPlayStoreIntent
import com.dgsd.android.hackernews.util.isAvailable
import de.psdev.licensesdialog.LicensesDialog
import org.jetbrains.anko.email

public class SettingsFragment : PreferenceFragment() {

    private var appSettings: AppSettings? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings)
        appSettings = (activity.application as HNApp).getAppServicesComponent().appSettings()
    }

    override fun onResume() {
        super.onResume()
        findPreference(R.string.settings_key_app_version)?.summary = BuildConfig.VERSION_NAME;

        val buildInfoPref = findPreference(R.string.settings_key_build_info)
        if (buildInfoPref != null) {
            if (BuildConfig.DEBUG) {
                buildInfoPref.summary = "build_time: %s git_sha: %s".format(
                        BuildConfig.BUILD_TIME, BuildConfig.GIT_SHA)
            } else {
                val aboutCategory = findPreference(R.string.settings_cat_key_about) as PreferenceCategory
                aboutCategory.removePreference(buildInfoPref)
            }
        }
    }

    override fun onPreferenceTreeClick(preferenceScreen: PreferenceScreen?, preference: Preference): Boolean {
        when (preference.key) {
            getString(R.string.settings_key_support) -> {
                email(BuildConfig.SUPPORT_EMAIL,
                        "%s %s support".format(getString(R.string.app_name), BuildConfig.VERSION_NAME))
            }

            getString(R.string.settings_key_rate) -> {
                val intent = getPlayStoreIntent()
                if (intent.isAvailable(activity)) {
                    startActivity(intent)
                }
            }

            getString(R.string.settings_key_licenses) -> {
                LicensesDialog.Builder(activity)
                        .setTitle(R.string.settings_title_licenses)
                        .setIncludeOwnLicense(true)
                        .setNotices(R.raw.licenses)
                        .build()
                        .show()
            }

            getString(R.string.settings_key_show_links_first) -> {
                appSettings!!.showLinksFirst().put((preference as CheckBoxPreference).isChecked)
            }
        }


        return super.onPreferenceTreeClick(preferenceScreen, preference)
    }

    private fun findPreference(@StringRes preferenceKey: Int): Preference? {
        return findPreference(getString(preferenceKey));
    }
}