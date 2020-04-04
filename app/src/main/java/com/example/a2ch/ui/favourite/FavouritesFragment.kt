package com.example.a2ch.ui.favourite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.a2ch.R
import com.example.a2ch.adapters.ThreadListAdapter
import com.example.a2ch.databinding.FavouritesFragmentBinding
import com.example.a2ch.models.category.Thread
import com.example.a2ch.ui.posts.PostsActivity
import com.example.a2ch.util.BOARD_NAME
import com.example.a2ch.util.THREAD_NUM
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class FavouritesFragment : Fragment(), KodeinAware {
    override val kodein by kodein()
    private val factory: FavouritesViewModelFactory by instance()
    private lateinit var binding: FavouritesFragmentBinding
    private lateinit var viewModel: FavouritesViewModel
    private lateinit var favouritesAdapter: ThreadListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(FavouritesViewModel::class.java)
        binding = FavouritesFragmentBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@FavouritesFragment.viewLifecycleOwner
            viewmodel = viewModel
        }

        initFavouritesList()
        initObservers()
        return binding.root
    }

    private fun initObservers() {
        viewModel.threads.observe(viewLifecycleOwner, Observer {
            favouritesAdapter.updateList(it)
        })
        viewModel.startPostsActivity.observe(viewLifecycleOwner, Observer {
            val data = it.peekContent()
            startPostsActivity(data.board, data.thread)
        })
    }

    private fun initFavouritesList() {
        favouritesAdapter = ThreadListAdapter(viewModel)
        binding.root.findViewById<RecyclerView>(R.id.favourites_list).adapter = favouritesAdapter

    }

    private fun startPostsActivity(board: String, thread: String){
        startActivity(
            Intent(requireContext(), PostsActivity::class.java)
                .putExtra(THREAD_NUM, thread)
                .putExtra(BOARD_NAME, board)
        )
    }
}