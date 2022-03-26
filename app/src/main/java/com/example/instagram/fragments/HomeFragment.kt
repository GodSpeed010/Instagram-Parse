package com.example.instagram.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.Post
import com.example.instagram.PostAdapter
import com.example.instagram.R
import com.parse.ParseQuery

open class HomeFragment : Fragment() {

    lateinit var rvPosts: RecyclerView
    lateinit var adapter: PostAdapter
    var allPosts: MutableList<Post> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvPosts = view.findViewById(R.id.rvPosts)
        adapter = PostAdapter(requireContext(), allPosts)

        rvPosts.adapter = adapter
        rvPosts.layoutManager = LinearLayoutManager(requireContext())

        queryPosts()
    }

    //Query for posts in our server
    open fun queryPosts() {

        // Specify which class to query
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        query.include(Post.KEY_USER)
        // Return posts in descending order; newer posts appear 1st
        query.addDescendingOrder("createdAt")

        //todo only return 20 most recentposts
        query.limit = 20

        query.findInBackground { posts, e ->
            if (e != null) {
                //Something went wrong
                Log.e(TAG, "Error fetching posts")
            } else {
                if (posts != null) {
                    for (post in posts) {
                        Log.i(TAG,"Post: ${post.getDescription()}, username: ${post.getUser()?.username}")
                    }

                    allPosts.addAll(posts)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}