package com.scienjus.add1link.ui.activity

import FeedQuery
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import com.scienjus.add1link.R
import com.scienjus.add1link.client.AccountStorage
import com.scienjus.add1link.ui.adapter.LinksAdapter
import com.scienjus.add1link.ui.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var viewAdapter: LinksAdapter

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViewModel()

        viewAdapter = LinksAdapter(this, emptyList()) { _: View?, link: FeedQuery.Link ->
            startActivity(WebviewActivity.initIntent(this, link))
        }

        links_recycler_view.run {
            setHasFixedSize(true)

            layoutManager = LinearLayoutManager(this.context)

            adapter = viewAdapter

            addItemDecoration((DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)))
        }

        refresh_layout.setOnRefreshListener {
            mainViewModel.refresh(false)
        }

        refresh_layout.setOnLoadMoreListener {
            mainViewModel.loadMore()
        }

        if (isLogin()) {
            mainViewModel.refresh(true)
            if (intent?.action == Intent.ACTION_SEND) {
                intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                    showAddDialog(it)
                }
            }
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivityForResult(intent, 0)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                showAddDialog()
                true
            }
            R.id.action_search -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            finish()
        } else if (resultCode == Activity.RESULT_OK) {
            mainViewModel.refresh(true)
        }
    }

    private fun bindViewModel() {
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        mainViewModel.initializing.observe(this, Observer {
            loadingProgressBar.visibility = if (it!!) View.VISIBLE else View.INVISIBLE
        })
        mainViewModel.refreshing.observe(this, Observer {
            if (!it!!) {
                refresh_layout.finishRefresh()
            }
        })
        mainViewModel.loadingMore.observe(this, Observer {
            if (!it!!) {
                refresh_layout.finishLoadMore()
            }
        })
        mainViewModel.links.observe(this, Observer {
            viewAdapter.links = it!!
            viewAdapter.notifyDataSetChanged()
        })
    }

    private fun showAddDialog(url: String? = null) {
        val view = layoutInflater.inflate(R.layout.dialog_add_url, null)
        val urlInput = view.findViewById<EditText>(R.id.add_url)
        url?.let { urlInput.setText(it) }
        AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton(R.string.ok) { _, _ ->
                    val urlToAdd = urlInput.text.toString()
                    mainViewModel.addUrl(urlToAdd)
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
                .create()
                .show()
    }

    private fun isLogin(): Boolean = AccountStorage.get() != null
}
