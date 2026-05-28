import com.example.flashcard_compose_app.domain.model.Role
import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("userId")
    val id: Int,

    @SerializedName("username")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("role")
    val role: Role,

    @SerializedName("message")
    val message: String? = null
)