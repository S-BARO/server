package com.dh.baro.identity.infra

import com.dh.baro.core.ErrorMessage
import com.dh.baro.identity.domain.AuthProvider
import com.dh.baro.identity.application.OauthApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class KakaoOauthApi(
    @Value("\${oauth2.client.registration.kakao.url}")
    private val url: String,
) : OauthApi {

    private val restClient = RestClient.create(url)

    override fun provider() = AuthProvider.KAKAO

    override fun fetchUser(accessToken: String): OauthApi.SocialUserInfo {
        val response = restClient.get()
            .header("Authorization", "Bearer $accessToken")
            .exchange { _, response ->
                response.bodyTo(KakaoUserInfoResponse::class.java)
                    ?: run {
                        val detail = response.bodyTo(String::class.java) ?: "no details"
                        throw IllegalArgumentException(ErrorMessage.KAKAO_USER_FETCH_FAILED.format(detail))
                    }
            }

        return OauthApi.SocialUserInfo(
            providerId = response.id.toString(),
            email = response.kakaoAccount.email,
            nickname = response.kakaoAccount.profile.nickname,
        )
    }
}
