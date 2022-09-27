package com.application.animalAlertApp.View

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.application.animalAlertApp.Adapters.ProfilePostAdapter
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.View.Post.MyPostState
import com.application.animalAlertApp.View.Profile.MyAllPostViewModel
import com.application.animalAlertApp.data.Response.Post.GetPost
import com.application.animalAlertApp.databinding.ActivityMyProfilePostBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MyProfilePostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyProfilePostBinding
    private val viewModel: MyAllPostViewModel by viewModels()
    private lateinit var post_list: ArrayList<GetPost>
    private var see_size: Int? = null
    private lateinit var adapter1: ProfilePostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_my_profile_post)
        binding = ActivityMyProfilePostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        post_list = ArrayList()
        getmyposts()
    }


    private fun getmyposts() {
        viewModel.getmyposts()
        viewModel.mypostData.observe(this, Observer {
            binding.apply {
                when (it) {
                    is MyPostState.Success -> {
                        if (it.data.status == 200) {
                            binding.shimmer.stopShimmer()
                            binding.shimmer.visibility= View.GONE
                            post_list.addAll(it.data.getPost)

                            adapter1 = ProfilePostAdapter(
                                this@MyProfilePostActivity,
                                post_list,
                                post_list.size)
                            binding.recyclerview.adapter = adapter1
                        }else {
                            toast(""+it.data.message)
                            binding.shimmer.stopShimmer()
                            binding.shimmer.visibility= View.GONE
                        }
                    }
                    is MyPostState.Failure -> {
                        toast("" + it.msg)
                        binding.shimmer.stopShimmer()
                        binding.shimmer.visibility= View.GONE
                        Log.e("mypet", "" + it.msg)
                    }
                    is MyPostState.Loading -> {
                        binding.shimmer.stopShimmer()
                        binding.shimmer.visibility= View.VISIBLE
                        //    progress.show()
                    }
                    is MyPostState.Empty -> {
                        binding.shimmer.stopShimmer()
                        binding.shimmer.visibility= View.GONE

                    }
                }
            }
        })
    }


    fun ClosePost(view: android.view.View) {
        finish()
    }
}