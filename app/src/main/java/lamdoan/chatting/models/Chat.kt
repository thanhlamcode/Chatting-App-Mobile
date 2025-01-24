package lamdoan.chatting.models

data class Chat(
    val id: String = "",
    val user1Id: String = "",
    val user2Id: String = "",
    val content: String = "",
    val isRead: Boolean = false
)
