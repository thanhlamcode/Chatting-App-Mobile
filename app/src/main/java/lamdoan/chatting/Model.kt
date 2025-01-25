package lamdoan.chatting

data class MessageItem(
    val text: String = "",
    val senderId: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", System.currentTimeMillis()) // Required for Firebase
}

data class Room(
    val id: String = "",
    val userIds: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
) {
    constructor() : this("", emptyList(), "", System.currentTimeMillis()) // Required for Firebase
}

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val avatar: String = "",
    val password: String = "",
)