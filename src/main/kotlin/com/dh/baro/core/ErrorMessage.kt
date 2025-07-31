package com.dh.baro.core

enum class ErrorMessage(val message: String) {

    // Common
    UNAUTHORIZED("로그인이 필요합니다."),
    FORBIDDEN("권한이 없습니다."),
    NO_RESOURCE_FOUND("요청한 리소스를 찾을 수 없습니다."),
    UNHANDLED_EXCEPTION("서버 오류가 발생했습니다. 관리자에게 문의해주세요."),

    // Identity
    UNSUPPORTED_SOCIAL_LOGIN("지원하지 않는 소셜 로그인 제공자입니다: %s"),
    KAKAO_USER_FETCH_FAILED("카카오 사용자 정보 조회 실패: %s"),
    ;

    fun format(vararg args: Any): String =
        message.format(*args)
}
