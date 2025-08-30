package com.example.bhatsapp.feature.home

import androidx.lifecycle.ViewModel
import com.example.bhatsapp.model.Channel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val firebaseDatabase = Firebase.database
    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels = _channels.asStateFlow()

    init {
        getChannels()
    }
    // Fetch channels from Firebase Realtime Database
    // modify such that it fetches channel from user's channels only
    private fun getChannels() {
//        firebaseDatabase.getReference("users").child(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
//            val userChannels = it.child("channels").value as? List<String> ?: emptyList()
//            val list = mutableListOf<Channel>()
//            if (userChannels.isEmpty()) {
//                _channels.value = emptyList()
//                return@addOnSuccessListener
//            }
//            it.children.forEach { data ->
//                if (data.key in userChannels) {
//                    firebaseDatabase.getReference("channel").child(data.key!!).get().addOnSuccessListener { channelData ->
//                        val channel = Channel(channelData.key!!, channelData.value.toString())
//                        list.add(channel)
//                        _channels.value = list
//                    }
//                }
//            }
//
//        }
        firebaseDatabase.getReference("users").child((Firebase.auth.currentUser!!.uid)).child("channels").get().addOnSuccessListener {
            val list = mutableListOf<Channel>()
            it.children.forEach { data ->
                val channel = Channel(data.key!!, data.value.toString())
                list.add(channel)
            }
            _channels.value = list
        }
//        firebaseDatabase.getReference("channel").get().addOnSuccessListener {
//            val list = mutableListOf<Channel>()
//            it.children.forEach { data ->
//                val channel = Channel(data.key!!, data.value.toString())
//                list.add(channel)
//            }
//            _channels.value = list
//        }
    }
    // Add channel to Firebase Realtime Database
    // modify such that it adds channel to user's channels only
    // and adds user to channel's users list
    fun addChannel(name: String) {
        val key = firebaseDatabase.getReference("channel").push().key!!
//        firebaseDatabase.getReference("users").child(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
//            val userChannels = it.child("channels").value as? Map<String, String> ?: emptyMap()
//            val newChannels = userChannels.toMutableMap().apply { put(key, name) }
//            firebaseDatabase.getReference("users").child(Firebase.auth.currentUser!!.uid)
//                .child("channels").setValue(newChannels)
//        }
        firebaseDatabase.getReference("users").child(Firebase.auth.currentUser!!.uid).child("channels").child(key).setValue(name)
//        firebaseDatabase.getReference("channel").child(key).setValue(name)
        // added channel to user's channels list

        // added user to channel's users list
        firebaseDatabase.getReference("channel_users").child(key).child("users").child(Firebase.auth.currentUser!!.uid).setValue(Firebase.auth.currentUser!!.email)
//        firebaseDatabase.getReference("channel_users").child(key).get().addOnSuccessListener {
//            val channelUsers = it.value as? List<String> ?: emptyList()
//            val newUsers = channelUsers + Firebase.auth.currentUser!!.uid
//            firebaseDatabase.getReference("channel_users").child(key).setValue(newUsers)
//        }


//        val key = firebaseDatabase.getReference("channel").push().key
        firebaseDatabase.getReference("channel").child(key).setValue(name).addOnSuccessListener {
            getChannels()
        }
    }

    fun joinChannel(channelID: String) {
        firebaseDatabase.getReference("channel").child(channelID).get().addOnSuccessListener { snapshot ->
            val channelName = snapshot.value.toString()
            // Add channel to user's channels list
            firebaseDatabase.getReference("users").child(Firebase.auth.currentUser!!.uid).child("channels").child(channelID).setValue(channelName)
            // Add user to channel's users list
            firebaseDatabase.getReference("channel_users").child(channelID).child("users").child(Firebase.auth.currentUser!!.uid).setValue(Firebase.auth.currentUser!!.email)
            getChannels()
        }

    }
}