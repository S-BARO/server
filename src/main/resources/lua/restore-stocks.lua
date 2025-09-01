-- Validation
for i = 1, #KEYS do
    local key = KEYS[i]

    local increaseAmount = tonumber(ARGV[i])
    if increaseAmount == nil or increaseAmount <= 0 then
        return -3  -- INVALID_AMOUNT
    end

    local currentStock = redis.call('GET', key)
    if currentStock == false then
        return -1  -- Missing key
    else
        currentStock = tonumber(currentStock)
        if currentStock == nil then
            return -3  -- INVALID_AMOUNT
        end
    end
end

-- Increase
for i = 1, #KEYS do
    local key = KEYS[i]
    local increaseAmount = tonumber(ARGV[i])
    redis.call('INCRBY', key, increaseAmount)
end

return #KEYS
