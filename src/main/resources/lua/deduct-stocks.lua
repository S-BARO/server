-- 1단계: 재고 확인
for i = 1, #KEYS do
    local key = KEYS[i]
    local deductAmount = tonumber(ARGV[i])
    
    redis.log(redis.LOG_NOTICE, "Checking key: " .. key .. ", deduct amount: " .. deductAmount)

    local currentStock = redis.call('GET', key)
    if currentStock == false then
        redis.log(redis.LOG_WARNING, "Missing key: " .. key)
        return -1 -- MISSING_KEYS
    end

    currentStock = tonumber(currentStock)
    redis.log(redis.LOG_NOTICE, "Current stock for " .. key .. ": " .. currentStock)

    if currentStock < deductAmount then
        redis.log(redis.LOG_WARNING, "Insufficient stock for " .. key .. ": " .. currentStock .. " < " .. deductAmount)
        return -2 -- INSUFFICIENT_STOCK
    end
end

-- 2단계: 감소 작업 (rollback tracking)
local processed = 0
for i = 1, #KEYS do
    local key = KEYS[i]
    local deductAmount = tonumber(ARGV[i])

    redis.log(redis.LOG_NOTICE, "Processing key: " .. key .. ", iteration: " .. processed)

    local success, result = pcall(redis.call, 'DECRBY', key, deductAmount)
    if not success then
        redis.log(redis.LOG_WARNING, "Error processing key " .. key .. ": " .. tostring(result))

        -- 롤백: 이미 처리된 키들을 복구
        redis.log(redis.LOG_NOTICE, "Rolling back " .. processed .. " operations")
        for j = 1, processed do
            redis.log(redis.LOG_NOTICE, "Rollback key: " .. KEYS[j] .. ", amount: " .. ARGV[j])
            redis.call('INCRBY', KEYS[j], tonumber(ARGV[j]))
        end
        return -3 -- ERROR_WITH_ROLLBACK
    end

    redis.log(redis.LOG_NOTICE, "Successfully processed key: " .. key .. ", new value: " .. result)
    processed = processed + 1
end

redis.log(redis.LOG_NOTICE, "All operations completed successfully, processed: " .. processed)
return #KEYS
