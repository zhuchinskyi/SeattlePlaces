package com.dzhucinski.seattleplaces.search

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.dzhucinski.seattleplaces.R
import com.dzhucinski.seattleplaces.detail.DetailActivity
import com.dzhucinski.seattleplaces.detail.KEY_VENUE_ID
import com.dzhucinski.seattleplaces.map.KEY_MAP_DETAILS
import com.dzhucinski.seattleplaces.map.MapViewActivity
import com.dzhucinski.seattleplaces.util.distinct

import kotlinx.android.synthetic.main.activity_search.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private val searchItems = mutableListOf<SearchViewModel.VenueItem>()

    private lateinit var adapter: RecyclerAdapter

    private lateinit var linearLayoutManager: LinearLayoutManager

    private val searchViewModel: SearchViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchView.setOnQueryTextListener(this)

        searchViewModel.progressLiveData.observe(this, Observer {
            progressView.visibility = if (it == true) View.VISIBLE else View.GONE
            if (it == true) ivSearchHolder.visibility = View.GONE
        })

        searchViewModel.errorLiveData.observe(this, Observer {
            showError(it)
        })

        searchViewModel
            .liveData
            .distinct()
            .observe(this, Observer<List<SearchViewModel.VenueItem>> {
                searchItems.clear()
                searchItems.addAll(it)
                adapter.notifyDataSetChanged()
                if (it.isEmpty()) ivSearchHolder.visibility = View.VISIBLE
            })

        linearLayoutManager = LinearLayoutManager(this)
        rvSearchContent.layoutManager = linearLayoutManager
        adapter = RecyclerAdapter(
            searchItems, object : ClickHandler {
                override fun onSearchItemClick(item: SearchViewModel.VenueItem) {
                    val intent = Intent(this@SearchActivity, DetailActivity::class.java)
                    intent.putExtra(KEY_VENUE_ID, item.id)
                    this@SearchActivity.startActivity(intent)
                }

                override fun onStarClick(item: SearchViewModel.VenueItem, selected: Boolean) {
                    if (selected) {
                        searchViewModel.removeFromFavorites(item.id)
                    } else {
                        searchViewModel.addToFavorites(item.id)
                    }
                }
            })
        rvSearchContent.adapter = adapter

        fabMap.setOnClickListener { view ->

            val arrayList = ArrayList<SearchViewModel.VenueItem>()
            arrayList.addAll(adapter.getData())

            val intent = Intent(view.context, MapViewActivity::class.java)
            intent.putParcelableArrayListExtra(KEY_MAP_DETAILS, arrayList)
            view.context.startActivity(intent)
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        performSearch(newText)
        return false
    }

    private fun performSearch(query: String) {
        searchViewModel.performSearch(query)
    }

    private fun showError(errorMsg: String) {
        Snackbar.make(root, errorMsg, Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.retry)) {
                searchViewModel.performSearch(searchView.query.toString())
            }.show()
    }
}
