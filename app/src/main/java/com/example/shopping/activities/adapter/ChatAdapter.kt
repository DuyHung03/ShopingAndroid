package com.example.shopping.activities.adapter

import android.app.ActivityOptions
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopping.R
import com.example.shopping.activities.entities.Message
import com.example.shopping.activities.view.activities.PhotoActivity
import com.makeramen.roundedimageview.RoundedImageView
import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapter(private val currentUserId: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val messages = mutableListOf<Message>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SENDER -> {
                SenderMessageViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.sender, parent, false)
                )
            }

            VIEW_TYPE_RECIPIENT -> {
                RecipientMessageViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.recipient, parent, false)
                )
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        holder.setIsRecyclable(false)

        message.imageUrl?.let {
            holder.itemView.setOnClickListener {
                val intent = Intent(holder.itemView.context, PhotoActivity::class.java)
                intent.putExtra("photoUrl", message.imageUrl)
                val options = ActivityOptions.makeCustomAnimation(
                    holder.itemView.context,
                    R.anim.fade_in,
                    R.anim.fade_out,
                )
                holder.itemView.context.startActivity(intent, options.toBundle())
            }
        }

        when (holder) {
            is SenderMessageViewHolder -> {
                holder.bind(message)
            }

            is RecipientMessageViewHolder -> {
                holder.bind(message)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.senderId == currentUserId) {
            VIEW_TYPE_SENDER
        } else {
            VIEW_TYPE_RECIPIENT
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    fun setMessages(newMessages: List<Message>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    inner class SenderMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(message: Message) {
            val messageView = itemView.findViewById<TextView>(R.id.senderMessageTextView)
            val image = itemView.findViewById<RoundedImageView>(R.id.senderImageMessage)
            val avatar = itemView.findViewById<CircleImageView>(R.id.avatar)

            //load sender avatar
            Glide.with(itemView.context)
                .load(message.senderPhoto)
                .override(100, 100)
                .into(avatar)

            //check message is image or text
            if (!message.imageUrl.isNullOrEmpty()) {
                //if message is image
                image.visibility = View.VISIBLE
                messageView.visibility = View.GONE
                Glide.with(itemView.context)
                    .load(message.imageUrl)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(image)
            } else {
                image.visibility = View.GONE
                messageView.visibility = View.VISIBLE
                messageView.text = message.message
            }
        }
    }

    inner class RecipientMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(message: Message) {
            val messageView = itemView.findViewById<TextView>(R.id.recipientMessageTextView)
            val image = itemView.findViewById<RoundedImageView>(R.id.recipientImageMessage)

            if (!message.imageUrl.isNullOrEmpty()) {
                image.visibility = View.VISIBLE
                messageView.visibility = View.GONE
                Glide.with(itemView.context)
                    .load(message.imageUrl)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(image)
            } else {
                image.visibility = View.GONE
                messageView.visibility = View.VISIBLE
                messageView.text = message.message
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_SENDER = 1
        private const val VIEW_TYPE_RECIPIENT = 2
    }
}
