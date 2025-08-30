-- Validate input
if #KEYS ~= #ARGV then
    return -3
end

if #KEYS == 0 then
    return 0
end

-- Phase 1: validation
local stockValues = {}
for i = 1, #KEYS do
    local key = KEYS[i]
    local deductAmount = tonumber(ARGV[i])

    if deductAmount == nil or deductAmount <= 0 then
        return -4  -- Invalid deduction amount
    end

    local currentStock = redis.call('GET', key)
    if currentStock == false then
        return -1  -- Stock key not found
    end

    local stock = tonumber(currentStock)
    if stock < deductAmount then
        return -2  -- Insufficient stock
    end

    stockValues[i] = stock
end

-- Phase 2: deduction
for i = 1, #KEYS do
    local key = KEYS[i]
    local deductAmount = tonumber(ARGV[i])
    redis.call('DECRBY', key, deductAmount)
end

return #KEYS
