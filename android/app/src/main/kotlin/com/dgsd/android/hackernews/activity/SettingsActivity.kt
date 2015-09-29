package com.dgsd.android.hackernews.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dgsd.android.hackernews.R
import kotlinx.android.synthetic.act_settings.toolbar

public class SettingsActivity : BaseActivity() {

    companion object {
        public fun getStartIntent(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }

    override fun getLayoutResource(): Int {
        return R.layout.act_settings
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)

        with (supportActionBar) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

}
