package com.sanryoo.shopping.feature.presentation.using.my_account

import android.net.Uri
import com.sanryoo.shopping.feature.domain.model.User

data class UserState(
    var user: User = User(),

    //Change information
    var changeName: Boolean = false,
    var changeBio: Boolean = false,
    var changePhoneNumber: Boolean = false,
    var changeAddress: Boolean = false,

    //Choose image for Profile Picture or Cover Photo
    var chooseImage: ChooseImage = ChooseImage.PROFILE_PICTURE,
    var profilePicture: Uri? = null,
    var coverPhoto: Uri? = null,

    var isLoading: Boolean = false
)

enum class ChooseImage {
    PROFILE_PICTURE, COVER_PHOTO
}
