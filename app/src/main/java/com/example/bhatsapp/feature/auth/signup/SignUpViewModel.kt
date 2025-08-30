package com.example.bhatsapp.feature.auth.signup

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor() : ViewModel() {
    private val firebaseDatabase = Firebase.database
    private val _state = MutableStateFlow<SignUpState>(SignUpState.Nothing)
    val state = _state.asStateFlow()
    // Firebase Authentication for SignUp
    // but doesn't add user details to Realtime Database
    // mofify such that it adds user details to Realtime Database
    // each user should have a node in the database with their uid
    // and their details should be stored in that node
    // user details: name, email, uid and a list of channels they are part of
    fun signUp(name: String, email: String, password: String) {
        _state.value = SignUpState.Loading
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result.user
                    user?.updateProfile(
                        com.google.firebase.auth.UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()
                    )?.addOnCompleteListener {
                        // Add user details to Realtime Database
                        firebaseDatabase.getReference("users").child(user.uid).setValue(
                            mapOf(
                                "name" to name,
                                "email" to email,
                                "uid" to user.uid,
                                "channels" to listOf<String>()
                            )
                        )
                        _state.value = SignUpState.Success
                    }
                } else {
                    _state.value = SignUpState.Error
                }
            }
    }
}

sealed class SignUpState {
    object Nothing : SignUpState()
    object Loading : SignUpState()
    object Success : SignUpState()
    object Error : SignUpState()
}