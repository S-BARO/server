package com.dh.baro.identity.infra

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoUserInfoResponse(
    val id: Long,
    @JsonProperty("kakao_account")
    val kakao_account: KakaoAccount,
    val properties: Properties?
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class KakaoAccount(val email: String?)
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Properties(val nickname: String?)
}
