package com.dh.baro.identity.infra

import com.dh.baro.identity.domain.AuthProvider
import com.dh.baro.identity.domain.OauthClient
import com.dh.baro.identity.domain.dto.SocialUserInfo
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class KakaoOauthClient : OauthClient {
    private val rest = RestClient.create()
    private val URL = "https://kapi.kakao.com/v2/user/me"

    override fun provider() = AuthProvider.KAKAO

    @Retryable(retryFor = [Exception::class], maxAttempts = 2, backoff = Backoff(delay = 500))
    override fun fetchUser(accessToken: String): SocialUserInfo {
        val res = rest.get()
            .uri(URL)
            .header("Authorization", "Bearer $accessToken")
            .retrieve()
            .body(KakaoUserInfoResponse::class.java)
            ?: throw IllegalStateException("카카오 사용자 정보 조회 실패")

        return SocialUserInfo(
            providerId = res.id.toString(),
            email = res.kakao_account.email,
            nickname = res.properties?.nickname,
        )
    }
}
