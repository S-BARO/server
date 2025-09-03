for i = 1, #KEYS do
    local key = KEYS[i]
    local deductAmount = tonumber(ARGV[i])
    
    -- 검증과 차감을 원자적으로 수행
    local currentStock = redis.call('GET', key)
    if currentStock == false then
        return -1 -- MISSING_KEYS
    end
    
    currentStock = tonumber(currentStock)
    if currentStock < deductAmount then
        return -2 -- INSUFFICIENT_STOCK  
    end
    
    -- 즉시 차감 수행
    redis.call('DECRBY', key, deductAmount)
end
return #KEYS
