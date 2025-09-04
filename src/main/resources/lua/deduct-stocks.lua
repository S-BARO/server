-- 1단계: 재고 확인
for i = 1, #KEYS do
    local key = KEYS[i]
    local deductAmount = tonumber(ARGV[i])
    
    local currentStock = redis.call('GET', key)
    if currentStock == false then
        return -1 -- MISSING_KEYS
    end
    
    currentStock = tonumber(currentStock)
    if currentStock < deductAmount then
        return -2 -- INSUFFICIENT_STOCK
    end
end

-- 2단계: 감소 작업 (rollback tracking)
local processed = 0
for i = 1, #KEYS do
    local key = KEYS[i]
    local deductAmount = tonumber(ARGV[i])
    
    local success, result = pcall(redis.call, 'DECRBY', key, deductAmount)
    if not success then
        -- 롤백: 이미 처리된 키들을 복구
        for j = 1, processed do
            redis.call('INCRBY', KEYS[j], tonumber(ARGV[j]))
        end
        return -3 -- ERROR_WITH_ROLLBACK
    end
    processed = processed + 1
end

return #KEYS
