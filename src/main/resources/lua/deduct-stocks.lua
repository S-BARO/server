-- Validation
for i = 1, #KEYS do
    local key = KEYS[i]

    local deductAmount = tonumber(ARGV[i])
    if deductAmount == nil or deductAmount <= 0 then
        return -3  -- INVALID_AMOUNT
    end

    local currentStock = redis.call('GET', key)
    if currentStock == false then
        return -1  -- MISSING_KEYS
    else
        currentStock = tonumber(currentStock)
        if currentStock == nil then
            return -3  -- INVALID_AMOUNT
        end
    end

    if currentStock < deductAmount then
        return -2  -- INSUFFICIENT_STOCK
    end
end

-- Deduction
for i = 1, #KEYS do
    local key = KEYS[i]
    local deductAmount = tonumber(ARGV[i])
    redis.call('DECRBY', key, deductAmount)
end

return #KEYS
