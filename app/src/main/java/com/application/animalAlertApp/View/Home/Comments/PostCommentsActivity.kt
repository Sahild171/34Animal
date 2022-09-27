package com.application.animalAlertApp.View.Home.Comments

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.application.animalAlertApp.Adapters.PostCommentAdapter
import com.application.animalAlertApp.Interfaces.CommentInterface
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.data.Response.Comments.FindComment
import com.application.animalAlertApp.databinding.ActivityPostCommentsBinding
import dagger.hilt.android.AndroidEntryPoint
import android.view.inputmethod.InputMethodManager
import com.application.animalAlertApp.ProgressBar.MyProgressBar
import com.application.animalAlertApp.SharedPrefrences.MySharedPreferences
import com.application.animalAlertApp.Utils.checkForInternet
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class PostCommentsActivity : AppCompatActivity(), CommentInterface {
    private lateinit var binding: ActivityPostCommentsBinding
    private lateinit var adapter: PostCommentAdapter
    private var postId: String = ""
    private val viewmodel: CommentsViewModel by viewModels()
    private lateinit var list: ArrayList<FindComment>
    private var commentstatus: String = "Normal_Comment"
    private var comment_id: String = ""
    private var postion: Int? = null
    private lateinit var pd: Dialog
    private lateinit var mySharedPreferences: MySharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  setContentView(R.layout.activity_post_comments)
        binding = ActivityPostCommentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pd = MyProgressBar.progress(this)
        mySharedPreferences= MySharedPreferences(this)
        postId = intent.getStringExtra("postid").toString()
   //     Log.e("postid", "" + postId+" " +mySharedPreferences.getToken() )
        list = ArrayList()


        adapter = PostCommentAdapter(this, list, this)
        binding.recyclerview.adapter = adapter
        getAllComments()


        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.btSend.setOnClickListener {
            if (validation())
                if (commentstatus.equals("Normal_Comment")) {
                    addcomment()
                } else if (commentstatus.equals("Reply_Comment")) {
                    addreplycomment()
                }
        }

        addcommentdata()
        replydata()
        addlikeoncomment()

        binding.imgCrossAddreply.setOnClickListener {
            commentstatus = "Normal_Comment"
            binding.constraintAddReply.visibility = View.GONE
            binding.etMsg.setText("")
        }
    }


    private fun addlikeoncomment() {
        viewmodel.likedata.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
//                        pd.dismiss()
                        // toast("" + it.value.message)
                    } else {
                        // pd.dismiss()
                        toast("" + it.value.message)
                    }
                }
                is Resource.Failure -> {
                    //   pd.dismiss()
                    toast("" + it.exception!!.message)
                }
                is Resource.Loading -> {
                    //  pd.show()
                }
            }
        })
    }

    private fun replydata() {
        /////////reply comment
        viewmodel.replydata.observe(this@PostCommentsActivity, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        binding.etMsg.setText("")
                        binding.constraintAddReply.visibility = View.GONE
                        commentstatus = "Normal_Comment"
                        list[postion!!].visibilitystatus = true
                        list[postion!!].replyArray.add(it.value.replyArray)
                        Collections.reverse(list[postion!!].replyArray)
                        adapter.setData(list)
                    } else {
                        toast("" + it.value.message)
                    }
                }
                is Resource.Failure -> {
                    toast("" + it.exception!!.message)
                }
                is Resource.Loading -> {
                    Log.e("test", "loading")
                    // toast("Loading")
                }
            }
        })
    }


    private fun addcommentdata() {
        viewmodel.addcommentdata.observe(this@PostCommentsActivity, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        binding.etMsg.setText("")
                        toast("" + it.value.message)
                        // Log.e("value",""+it.value.result)
                        list.add(it.value.result)
                        if (list.size == 0) {
                            binding.imgNocomment.visibility = View.VISIBLE
                            binding.tvNocomment.visibility = View.VISIBLE
                        } else {
                            binding.imgNocomment.visibility = View.GONE
                            binding.tvNocomment.visibility = View.GONE
                        }
                        adapter.setData(list)
                    } else {
                        toast("" + it.value.message)
                    }
                }
                is Resource.Failure -> {
                    toast("" + it.exception!!.message)
                }
                is Resource.Loading -> {
                    toast("Loading")
                }
            }
        })
    }

    private fun addcomment() {
        if (checkForInternet(this)) {
            viewmodel.addcommnet(postId, binding.etMsg.text.toString().trim())
        } else {
            toast("No Internet Connection")
        }
    }

    private fun addreplycomment() {
        if (checkForInternet(this)) {
            viewmodel.replycomment(comment_id, binding.etMsg.text.toString().trim())
        } else {
            toast("No Internet Connection")
        }
    }

    private fun validation(): Boolean {
        if (binding.etMsg.text.toString().trim().isEmpty()) {
            toast("Please Enter Comment")
            return false
        }
        return true
    }

    private fun getAllComments() {
        viewmodel.getAllcomments(postId)
        viewmodel.mycommentdata.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        list.clear()
                        list.addAll(it.value.result)
                        if (list.size == 0) {
                            binding.imgNocomment.visibility = View.VISIBLE
                            binding.tvNocomment.visibility = View.VISIBLE
                        }
                        adapter.setData(list)
                        binding.shimmer.stopShimmer()
                        binding.shimmer.visibility = View.GONE
                        binding.recyclerview.visibility = View.VISIBLE
                    } else {
                        binding.shimmer.stopShimmer()
                        binding.shimmer.visibility = View.GONE
                        binding.recyclerview.visibility = View.VISIBLE
                        toast("" + it.value.message)
                    }
                }
                is Resource.Failure -> {
                    toast("" + it.exception!!.message)
                    binding.shimmer.stopShimmer()
                    binding.shimmer.visibility = View.GONE
                    binding.recyclerview.visibility = View.VISIBLE
                }
                is Resource.Loading -> {
                    binding.recyclerview.visibility = View.INVISIBLE
                    binding.shimmer.startShimmer()
                    binding.shimmer.visibility = View.VISIBLE
                }
            }
        })
    }


    override fun onLikeComment(commentId: String) {
        viewmodel.likecomment(commentId)
    }

    override fun onReplyComment(commentId: String, name: String, pos: Int) {
        commentstatus = "Reply_Comment"
        comment_id = commentId
        postion = pos
        binding.constraintAddReply.visibility = View.VISIBLE
        binding.tvReplyCommentuser.setText(name)
        binding.etMsg.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etMsg, InputMethodManager.SHOW_IMPLICIT)
        Handler().postDelayed({
            val ff = adapter.itemCount - pos
            binding.recyclerview.smoothScrollToPosition(adapter.itemCount - ff)
        }, 200)
    }
}