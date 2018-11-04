package com.scienjus.add1link.ui.activity

import FeedQuery
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.scienjus.add1link.R
import com.scienjus.add1link.ui.viewmodel.WebviewViewModel
import kotlinx.android.synthetic.main.activity_webview.*

class WebviewActivity : AppCompatActivity() {

    companion object {

        private const val KEY_EXTRA_URL = "url"

        fun initIntent(context: Context, link: FeedQuery.Link): Intent {
            val intent = Intent(context, WebviewActivity::class.java)
            intent.putExtra(KEY_EXTRA_URL, link.url())
            return intent
        }
    }

    private lateinit var webviewViewModel: WebviewViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        webviewViewModel = ViewModelProviders.of(this).get(WebviewViewModel::class.java)
        webviewViewModel.url.observe(this, Observer {
            webview.loadUrl(it!!)
        })
        intent.getStringExtra(KEY_EXTRA_URL).let { webviewViewModel.url.postValue(it) }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.webview_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_share -> {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, webviewViewModel.url.value)
                    type = "text/plain"
                }
                startActivity(sendIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
