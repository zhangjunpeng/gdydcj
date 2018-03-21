package com.mapuni.gdydcaiji.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.mapuni.gdydcaiji.R
import kotlinx.android.synthetic.main.activity_traffic.*
import kotlinx.android.synthetic.main.titlebar.*

class CountryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_traffic)
        title_text.text="交通设施"

    }
}
