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
    ALREADY_DISCONNECTED("클라이언트 연결이 중단되었습니다.")  ,
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
    INVALID_STOCK_AMOUNT("잘못된 형식의 재고 수량입니다."),
    PRODUCT_NOT_FOUND_FOR_DEDUCTION("재고 차감할 상품을 찾을 수 없습니다: %d"),
    INVENTORY_RESTORE_ERROR("Redis 재고 복원 중 오류 발생: orderId=%d"),
    INVENTORY_RETRY_EXCEEDED("재고 초기화 재시도 횟수 초과: maxRetry=%d"),

    // Category
    CATEGORY_ALREADY_EXISTS("카테고리(id = %d)는 이미 존재합니다."),
    CATEGORY_NOT_FOUND("카테고리를 찾을 수 없습니다: %s"),

    // Cart
    CART_ITEM_NOT_FOUND("장바구니 항목을 찾을 수 없습니다: %d"),
    CART_ITEM_LIMIT_EXCEEDED("장바구니에는 최대 20개의 상품만 담을 수 있습니다."),

    // Order
    ORDER_NOT_FOUND("주문을 찾을 수 없습니다: %d"),
    ORDER_CONFIRM_INVALID_STATUS("주문 확정은 PENDING 상태에서만 가능합니다. 현재 상태: %s"),
    ORDER_CONFIRM_NO_ITEMS("주문 항목이 없어 주문을 확정할 수 없습니다."),
    ORDER_ALREADY_CANCELLED("주문(orderId = %d)는 이미 취소되었습니다."),
    ORDER_CANCEL_INVALID_STATUS("%s 상태일 때는 주문(orderId = %d)을 취소할 수 없습니다."),
    ORDER_COMPENSATION_USER_NOT_AVAILABLE("보상 트랜잭션 실행 시 사용자 정보를 찾을 수 없습니다."),
    ORDER_COMPENSATION_INVENTORY_CONFLICT("재고 부족으로 인한 주문 취소: 상품 %d"),

    // Look
    LOOK_NOT_FOUND("룩을 찾을 수 없습니다: %d"),
    FITTING_SOURCE_IMAGE_NOT_FOUND("피팅 소스 이미지를 찾을 수 없습니다: %d"),

    // AI Fitting
    GEMINI_API_REQUEST_FAILED("Gemini API 요청에 실패했습니다: %s"),
    GEMINI_API_NO_CANDIDATES("Gemini API 응답에 후보가 없습니다"),
    GEMINI_API_NO_IMAGE_DATA("Gemini API 응답에 이미지 데이터가 없습니다"),
    IMAGE_DOWNLOAD_FAILED("이미지 다운로드에 실패했습니다: %s"),
    IMAGE_DOWNLOAD_NO_DATA("이미지 다운로드 응답에 데이터가 없습니다"),

    // Outbox
    OUTBOX_MESSAGE_DEAD("아웃박스 메시지가 재시도에 최종 실패했습니다: [eventType=%s, id=%d]"),
    UNKNOWN_EVENT_TYPE("알 수 없는 이벤트 타입입니다: %s"),
    ;

    fun format(vararg args: Any): String =
        message.format(*args)
}
