package com.example.shopping.activities.view.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopping.R
import com.example.shopping.activities.adapter.ChatAdapter
import com.example.shopping.activities.entities.Message
import com.example.shopping.activities.entities.User
import com.example.shopping.activities.utils.Constant.Companion.SHOP_ID
import com.example.shopping.activities.viewmodel.AuthViewModel
import com.example.shopping.activities.viewmodel.DataViewModel
import com.example.shopping.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private val dataViewModel by viewModels<DataViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var currentUser: FirebaseUser
    private lateinit var user: User


    // Registers a photo picker activity launcher in single-select mode.
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(10)) { uris ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uris != null) {
                for (uri in uris) {
                    dataViewModel.sentMessage(
                        Message(
                            UUID.randomUUID().toString(),
                            user.userId,
                            user.email!!,
                            user.name,
                            user.photoUrl,
                            SHOP_ID,
                            null,
                            uri.toString(),
                            System.currentTimeMillis()
                        ),
                        authViewModel.currentUser?.uid!!
                    )
                }
            } else {
                Log.d("TAG", "No media selected")
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get current user
        currentUser = authViewModel.currentUser!!

        //get current user info and set to user object
        user = User(
            currentUser.uid,
            currentUser.email,
            currentUser.displayName,
            null,
            currentUser.photoUrl.toString(),
            null
        )

        //setup recyclerView
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        chatAdapter = ChatAdapter(user.userId)
        binding.recyclerView.adapter = chatAdapter
        binding.recyclerView.layoutManager = linearLayoutManager

        //get messages from firebase
        dataViewModel.getMessageByUser(user.userId)

        //observe message change
        dataViewModel.messagesLiveData.observe(this) { messages ->
            chatAdapter.setMessages(messages)
        }

        //close button
        binding.closeButton.setOnClickListener {
            this.finish()
        }

        //hnalde sent message
        binding.sentButton.setOnClickListener {
            //get message text
            val messageText = binding.edtMessage.text.toString()

            if (messageText.isNotEmpty()) {
                //create a message
                val message = Message(
                    UUID.randomUUID().toString(),
                    user.userId,
                    user.email!!,
                    user.name,
                    user.photoUrl,
                    SHOP_ID,
                    messageText,
                    null,
                    System.currentTimeMillis()
                )

                //sent message to firebase
                dataViewModel.sentMessage(
                    message,
                    authViewModel.currentUser?.uid!!
                )
            }

            //clear editext
            binding.edtMessage.text.clear()
        }

        //handle add message photo
        binding.addPhoto.setOnClickListener {
            // Launch the photo picker and let the user choose images and videos.
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
        }
    }
}