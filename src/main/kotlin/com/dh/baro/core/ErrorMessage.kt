package com.dh.baro.core

enum class ErrorMessage(val message: String) {

    // Common
    UNAUTHORIZED("로그인이 필요합니다."),
    FORBIDDEN("권한이 없습니다."),
    INVALID_JSON("잘못된 JSON 형식입니다. 요청 데이터를 확인하세요."),
    FIELD_ERROR("요청 본문 필드 검증에 실패했습니다."),
    URL_PARAMETER_ERROR("요청 URL 파라미터 검증에 실패했습니다."),
    MISSING_REQUEST_HEADER("필수 요청 헤더가 누락되었습니다"),
    METHOD_ARGUMENT_TYPE_MISMATCH("요청 파라미터 타입이 올바르지 않습니다."),
    ALREADY_DISCONNECTED("클라이언트 연결이 중단되었습니다."),
    NO_RESOURCE_FOUND("요청한 리소스를 찾을 수 없습니다."),
    METHOD_NOT_SUPPORTED("허용되지 않은 메서드입니다."),
    MEDIA_TYPE_NOT_SUPPORTED("허용되지 않은 미디어 타입입니다."),
    UNHANDLED_EXCEPTION("서버 오류가 발생했습니다. 관리자에게 문의해주세요."),

    // Identity
    USER_NOT_FOUND("사용자를 찾을 수 없습니다: %d"),
    STORE_NOT_FOUND("스토어를 찾을 수 없습니다: %d"),
    UNSUPPORTED_SOCIAL_LOGIN("지원하지 않는 소셜 로그인 제공자입니다: %s"),
    KAKAO_USER_FETCH_FAILED("카카오 사용자 정보 조회 실패: %s"),

    // Product
    PRODUCT_NOT_FOUND("상품을 찾을 수 없습니다: %s"),
    INVALID_POPULAR_PRODUCT_CURSOR("cursorLikes와 cursorId는 함께 지정하거나 함께 생략해야 합니다."),
    OUT_OF_STOCK("재고가 모두 소진되었습니다.[id = %d]"),
    INSUFFICIENT_STOCK("일부 상품의 재고가 부족합니다."),
    INVALID_STOCK_OPERATION_INPUT("잘못된 입력: keys와 quantities 길이가 일치하지 않습니다."),
    STOCK_RESTORE_FAILED("재고 복원 실패: keys와 quantities 길이 불일치"),
    PRODUCT_NOT_FOUND_FOR_DEDUCTION("재고 차감할 상품을 찾을 수 없습니다: %d"),
    INVENTORY_RESTORE_ERROR("Redis 재고 복원 중 오류 발생: orderId=%d"),

    // Category
    CATEGORY_ALREADY_EXISTS("카테고리(id = %d)는 이미 존재합니다."),
    CATEGORY_NOT_FOUND("카테고리를 찾을 수 없습니다: %s"),

    // Cart
    CART_ITEM_NOT_FOUND("장바구니 항목을 찾을 수 없습니다: %d"),
    CART_ITEM_LIMIT_EXCEEDED("장바구니에는 최대 20개의 상품만 담을 수 있습니다."),

    // Order
    ORDER_NOT_FOUND("주문을 찾을 수 없습니다: %d"),

    // Look
    LOOK_NOT_FOUND("룩을 찾을 수 없습니다: %d"),

    // Outbox
    OUTBOX_MESSAGE_DEAD("아웃박스 메시지가 재시도에 최종 실패했습니다: [eventType=%s, id=%d]"),
    UNKNOWN_EVENT_TYPE("알 수 없는 이벤트 타입입니다: %s"),
    ;

    fun format(vararg args: Any): String =
        message.format(*args)
}
