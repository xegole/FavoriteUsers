package com.bigthinkapps.sw.test.ui.main

import android.app.SearchManager
import android.content.res.Configuration
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.view.*
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.SearchView
import androidx.cursoradapter.widget.CursorAdapter
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bigthinkapps.sw.test.R
import com.bigthinkapps.sw.test.databinding.MainFragmentBinding
import com.bigthinkapps.sw.test.models.User
import com.bigthinkapps.sw.test.ui.users.adapters.UsersAdapter
import com.bigthinkapps.sw.test.utils.Metrics
import com.bigthinkapps.sw.test.utils.calculateScreenSizeAndItemsOnIt
import com.bigthinkapps.sw.test.utils.hideKeyboard
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment() {
    private var viewModel: MainViewModel? = null
    private lateinit var binding: MainFragmentBinding
    private var searchView: SearchView? = null

    private val from = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1)
    private val to = intArrayOf(R.id.item_label)
    lateinit var cursorAdapter: SimpleCursorAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        val orientation = resources.configuration.orientation

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        if (viewModel?.usersAdapter != null || orientation != viewModel?.lastOrientation) resetMainList()
        if (viewModel?.favoritesAdapter != null || orientation != viewModel?.lastOrientation) resetFavoriteList()
        viewModel?.lastOrientation = orientation

        setListeners()

        viewModel?.getUsers()
        viewModel?.getFavorites()
    }

    private fun setListeners() {
        viewModel?.notifyMainList?.observe(viewLifecycleOwner, {
            if (it == true) {
                val users = viewModel?.lastUsers
                if (allList.adapter == null && users != null && users.isNotEmpty()) {
                    setAdapter(allList, users)
                } else if (users != null && users.isNotEmpty()) {
                    viewModel?.usersAdapter?.addUsers(users)
                }
                setSuggestionsAdapter()
            }
        })

        viewModel?.notifyFavoriteList?.observe(viewLifecycleOwner, {
            if (it == true) {
                val users = viewModel?.lastFavorites
                if (favoritesList.adapter == null && users != null && users.isNotEmpty()) setAdapter(
                    favoritesList,
                    users,
                    false
                )
                else if (users != null && users.isNotEmpty()) viewModel?.favoritesAdapter?.addUsers(
                    users
                )
            }
        })

        viewModel?.loading?.observe(viewLifecycleOwner, {
            progressLayout.visibility = if (it == null) View.GONE else View.VISIBLE
            dataLayout.visibility = if (it == null) View.VISIBLE else View.GONE
        })
    }

    private fun setAdapter(
        recyclerView: RecyclerView,
        users: List<User>?,
        mainAdapter: Boolean = true
    ) {
        val manager = initManager(recyclerView, mainAdapter)
        val adapter = UsersAdapter(this, users)
        if (mainAdapter) {
            viewModel?.usersAdapter = adapter
            viewModel?.scrollListener(manager)?.let {
                recyclerView.addOnScrollListener(it)
            }
            recyclerView.adapter = viewModel?.usersAdapter
        } else {
            viewModel?.favoritesAdapter = adapter
            if (users != null && users.isNotEmpty()) {
                favoritesLayout.visibility = View.VISIBLE
                allTitle.visibility = View.VISIBLE
            } else {
                favoritesLayout.visibility = View.GONE
                allTitle.visibility = View.GONE
            }
            recyclerView.adapter = viewModel?.favoritesAdapter
        }
    }

    private fun initManager(
        recyclerView: RecyclerView,
        mainAdapter: Boolean = true,
        itemSizeDpHeight: Int = 100,
        itemSizeDpWidth: Int = 100
    ): GridLayoutManager {
        val metrics =
            if (viewModel?.twoPane == true && viewModel?.lastOrientation == Configuration.ORIENTATION_LANDSCAPE) Metrics(
                null,
                300
            ) else null
        val (_, itemsOnScreen) = activity.calculateScreenSizeAndItemsOnIt(
            itemSizeDpHeight,
            itemSizeDpWidth,
            metrics
        )
        val manager = GridLayoutManager(
            context, if (mainAdapter) itemsOnScreen.itemsOnWidth else 1,
            if (mainAdapter) RecyclerView.VERTICAL else RecyclerView.HORIZONTAL, false
        )
        recyclerView.layoutManager = manager
        return manager
    }

    // Not Working
    private fun setSuggestionsAdapter() {
        val lastSuggestions = viewModel?.usersAdapter?.users?.map { it.firstName }?.toTypedArray()
        cursorAdapter = SimpleCursorAdapter(
            context,
            R.layout.search_item,
            null,
            from,
            to,
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )

        viewModel?.usersSuggestionsAdapter = cursorAdapter

        searchView?.suggestionsAdapter = viewModel?.usersSuggestionsAdapter

        searchView?.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return false
            }

            override fun onSuggestionClick(position: Int): Boolean {
                hideKeyboard()
                val cursor = searchView?.suggestionsAdapter?.getItem(position) as Cursor
                val selection =
                    cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
                searchView?.setQuery(selection, false)

                // Do something with selection
                return true
            }

        })
    }

    private fun resetMainList() {
        viewModel?.lastUsers = listOf()
        viewModel?.usersAdapter = null
    }

    private fun resetFavoriteList() {
        viewModel?.lastFavorites = listOf()
        viewModel?.favoritesAdapter = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val menuItem = menu.findItem(R.id.search)
        searchView = menuItem.actionView as SearchView
        searchView?.queryHint = getString(R.string.search_users_title)
        searchView?.isQueryRefinementEnabled = true
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel?.usersAdapter?.filter?.filter(query)
                viewModel?.favoritesAdapter?.filter?.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchView?.suggestionsAdapter?.filter?.filter(newText)
                viewModel?.usersAdapter?.filter?.filter(newText)
                viewModel?.favoritesAdapter?.filter?.filter(newText)
                val cursor = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))
                newText?.let {
                    viewModel?.usersAdapter?.users?.forEachIndexed { index, suggestion ->
                        if (suggestion.firstName.contains(newText, true))
                            cursor.addRow(arrayOf(index, suggestion.firstName))
                    }
                }
                cursorAdapter.changeCursor(cursor)
                return true
            }
        })
        menuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                viewModel?.usersAdapter?.filtered = false
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.search) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        if (viewModel?.lastOrientation == resources.configuration.orientation) {
            viewModel?.notifyMainList?.removeObservers(this)
            viewModel?.notifyFavoriteList?.removeObservers(this)
        }
    }
}
