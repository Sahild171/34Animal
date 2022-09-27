package com.application.animalAlertApp.View.Chats

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.application.animalAlertApp.Adapters.TitleChatAdapter
import com.application.animalAlertApp.Model.UsersChatList
import com.application.animalAlertApp.SharedPrefrences.MySharedPreferences
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.databinding.FragmentChatBinding
import com.google.firebase.database.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*


class ChatFragment : Fragment() {
    private lateinit var adapter: TitleChatAdapter
    private lateinit var binding: FragmentChatBinding
    private var chatroom: String? = null
    private var query: Query? = null
    private var query1: Query? = null
    private var myRef: DatabaseReference? = null
    private lateinit var mySharedPreferences: MySharedPreferences
    private lateinit var list: ArrayList<UsersChatList>
    private lateinit var newlist: ArrayList<UsersChatList>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false)
        val view = binding.root
        mySharedPreferences = MySharedPreferences(requireContext())
        chatroom = mySharedPreferences.getUserid()

        list = ArrayList()
        newlist = ArrayList()


        val database = FirebaseDatabase.getInstance()
        myRef = database.getReference("User")
        if (chatroom != null) {
            query = myRef?.child("ChatRoomList")?.child(chatroom!!)?.child("userid")
                ?.orderByChild("timestamp")?.endBefore("" + System.currentTimeMillis())
            query?.addChildEventListener(childEventListener)
            query1 = myRef?.child("ChatRoomList")?.child(chatroom!!)?.child("userid")
            query1?.addChildEventListener(childEventListener1)
        }
        return view
    }


    private var childEventListener: ChildEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            if (dataSnapshot.exists()) {
                binding.shimmer.visibility=View.VISIBLE
                binding.shimmer.startShimmer()
                binding.tvNochat.visibility=View.GONE
                val userChat = dataSnapshot.getValue(UsersChatList::class.java)
                list.add(userChat!!)
                Collections.reverse(list)
                binding.shimmer.stopShimmer()
                binding.shimmer.visibility = View.GONE

                if (list.size > 0) {
                    adapter = TitleChatAdapter(context, list, mySharedPreferences.getUserid()!!)
                    binding.recyclerview.adapter = adapter
                } else {
                    binding.tvNochat.visibility=View.GONE
                    Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show()
                }
            } else {
                binding.tvNochat.visibility=View.GONE
                binding.shimmer.stopShimmer()
                binding.shimmer.visibility = View.GONE
            }
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
        override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
        override fun onCancelled(databaseError: DatabaseError) {
            binding.shimmer.stopShimmer()
            binding.shimmer.visibility = View.GONE
            binding.tvNochat.visibility=View.GONE
            Log.e("ChatListException", databaseError.message)
            context?.toast("" + databaseError.message)
        }
    }


    var childEventListener1: ChildEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {}
        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
            val userChat = dataSnapshot.getValue(UsersChatList::class.java)
            if (dataSnapshot.exists()) {
                binding.shimmer.visibility=View.VISIBLE
                binding.shimmer.startShimmer()
                binding.tvNochat.visibility=View.GONE
                newlist.clear()
                newlist.add(userChat!!)
                binding.shimmer.stopShimmer()
                binding.shimmer.visibility = View.GONE
                for (i in 0..list.size - 1) {
                    if (list[0].id.equals(newlist[0].id)) {
                        list.removeAt(i)
                        list.add(0, newlist[0])
                        adapter.setData(list)
                    }
                }
            } else {
                binding.shimmer.stopShimmer()
                binding.tvNochat.visibility=View.GONE
                binding.shimmer.visibility = View.GONE
            }
        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
        override fun onCancelled(databaseError: DatabaseError) {
            binding.shimmer.stopShimmer()
            binding.shimmer.visibility = View.GONE
            binding.tvNochat.visibility=View.GONE
            context?.toast("" + databaseError.message)
            Log.e("exception", databaseError.message)
        }
    }
}