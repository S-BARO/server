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

for i = 1, #KEYS do
    local key = KEYS[i]
    local deductAmount = tonumber(ARGV[i])

    redis.call('DECRBY', key, deductAmount)
end
