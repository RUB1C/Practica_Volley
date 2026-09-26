package co.edu.apigetvolley.model

data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
) {
    override fun toString(): String {
        return "ID: $id | User ID: $userId\nTítulo: $title\nContenido: $body"
    }
}
