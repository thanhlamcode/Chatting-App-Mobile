package lamdoan.chatting.models

data class ChatRoom(
    val id: String = "",
    val userIds: List<String> = listOf(), // IDs of users in the chatroom
    val lastMessage: String = "",        // The last message in the room
    val lastUpdated: Long = System.currentTimeMillis() // Timestamp for the last activity
)
