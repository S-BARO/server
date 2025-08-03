package com.dh.baro.core

enum class ErrorMessage(val message: String) {

    // Common
    UNAUTHORIZED("로그인이 필요합니다."),
    FORBIDDEN("권한이 없습니다."),
    NO_RESOURCE_FOUND("요청한 리소스를 찾을 수 없습니다."),
    UNHANDLED_EXCEPTION("서버 오류가 발생했습니다. 관리자에게 문의해주세요."),

    // Identity
    USER_NOT_FOUND("사용자를 찾을 수 없습니다: %d"),
    UNSUPPORTED_SOCIAL_LOGIN("지원하지 않는 소셜 로그인 제공자입니다: %s"),
    KAKAO_USER_FETCH_FAILED("카카오 사용자 정보 조회 실패: %s"),

    // Product
    PRODUCT_NOT_FOUND("상품을 찾을 수 없습니다: %d"),

    // Category
    CATEGORY_ALREADY_EXISTS("카테고리(id = %d)는 이미 존재합니다."),
    CATEGORY_NOT_FOUND("카테고리를 찾을 수 없습니다: %d"),

    //Cart
    CART_ITEM_NOT_FOUND("장바구니 항목을 찾을 수 없습니다: %d"),
    CART_ITEM_LIMIT_EXCEEDED("장바구니에는 최대 20개의 상품만 담을 수 있습니다."),
    ;

    fun format(vararg args: Any): String =
        message.format(*args)
}
