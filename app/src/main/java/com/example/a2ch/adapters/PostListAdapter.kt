package com.example.a2ch.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.a2ch.databinding.PostRowBinding
import com.example.a2ch.models.post.Post
import com.example.a2ch.ui.posts.PostsAdapterListener
import com.example.a2ch.ui.posts.PostsViewModel
import kotlinx.android.synthetic.main.post_row.view.*


class PostListAdapter(private val viewModel: PostsViewModel): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items = ArrayList<Post>()
    var listener: PostsAdapterListener? = null

    fun updateList(newList: List<Post>){
        if(items.size != newList.size){
            items.clear()
            items.addAll(newList)

            notifyDataSetChanged()
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       val inflater = LayoutInflater.from(parent.context)
        val binding = PostRowBinding.inflate(inflater, parent, false)
        return PostViewHolder(binding.root)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(position){
            0 -> listener?.upReached()
            items.size - 1 -> listener?.bottomReached()
        }
        when(holder){
            is PostViewHolder -> holder.bind(items[position])
        }
    }

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(post: Post){
            DataBindingUtil.bind<PostRowBinding>(itemView).apply {
                this?.post = post
                this?.viewmodel = viewModel
            }
            try {
                val myOptions = RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()

                Glide.with(itemView.context)
                    .load("https://2ch.hk${post.files[0].path}")
                    .apply(myOptions)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(itemView.photo1)

                Glide.with(itemView.context)

                    .load("https://2ch.hk${post.files[1].path}")
                    .apply(myOptions)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(itemView.photo2)

                Glide.with(itemView.context)

                    .load("https://2ch.hk${post.files[2].path}")
                    .apply(myOptions)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(itemView.photo3)

                Glide.with(itemView.context)

                    .load(post.files[3])  .load("https://2ch.hk${post.files[3].path}")
                    .apply(myOptions)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(itemView.photo4)

            } catch (ex: Exception){}

        }
    }
}