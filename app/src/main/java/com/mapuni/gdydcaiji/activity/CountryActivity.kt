package com.mapuni.gdydcaiji.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.mapuni.gdydcaiji.R
import kotlinx.android.synthetic.main.titlebar.*

class CountryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_country)
        title_text.text="村采集"

    }
}
