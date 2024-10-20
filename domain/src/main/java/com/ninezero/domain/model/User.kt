package com.ninezero.domain.model

data class User(
    val id: String,
    val email: String?,
    val name: String?,
    val profileUrl: String?,
    val authType: AuthType
) {
    enum class AuthType {
        GOOGLE, NAVER, KAKAO
    }
}