package com.example.bhatsapp.feature.home

import androidx.lifecycle.ViewModel
import com.example.bhatsapp.model.Channel
import com.google.firebase.Firebase
import com.google.firebase.database.database
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(): ViewModel() {
    private val firebaseDatabase = Firebase.database
    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels = _channels.asStateFlow()

    init {
        getChannels()
    }

    private fun getChannels() {
        firebaseDatabase.getReference("channel").get().addOnSuccessListener { snapshot ->
            val list = mutableListOf<Channel>()
            for (childSnapshot in snapshot.children) {
                val key = childSnapshot.key ?: continue
                val value = childSnapshot.value.toString()
                //println("$key $value")
                val channel = Channel(key, value)
                list.add(channel)
            }
            _channels.value = list
        }.addOnFailureListener { exception ->
            // Handle the error
            println("Error fetching channels: ${exception.message}")
        }
    }

    fun addChannel(name: String) {
        val key = firebaseDatabase.getReference("channel").push().key

        firebaseDatabase.getReference("channel").child(key!!).setValue(name).addOnSuccessListener {
            getChannels()
        }
    }
}