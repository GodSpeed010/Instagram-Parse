package com.example.instagram.fragments

import android.util.Log
import com.example.instagram.Post
import com.parse.ParseQuery
import com.parse.ParseUser

class ProfileFragment : HomeFragment() {

    override fun queryPosts() {

        // Specify which class to query
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        query.include(Post.KEY_USER)

        //Only return posts form current signed in user
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser())

        // Return posts in descending order; newer posts appear 1st
        query.addDescendingOrder("createdAt")

        //only return 20 most recent posts
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
                    swipeContainer.isRefreshing = false
                }
            }
        }
    }

    companion object {
        private const val TAG = "ProfileFragment"
    }
}