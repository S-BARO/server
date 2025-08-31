-- Phase 1: validation
for i = 1, #KEYS do
    local key = KEYS[i]
    local deductAmount = tonumber(ARGV[i])

    local currentStock = redis.call('GET', key)
    if currentStock == false then
        return -1  -- Stock key not found
    end

    local stock = tonumber(currentStock)
    if deductAmount <= 0 or stock < deductAmount then
        return -2  -- Insufficient stock
    end
end

-- Phase 2: deduction
for i = 1, #KEYS do
    local key = KEYS[i]
    local deductAmount = tonumber(ARGV[i])
    redis.call('DECRBY', key, deductAmount)
end

return #KEYS
