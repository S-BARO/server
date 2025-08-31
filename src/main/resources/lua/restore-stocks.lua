-- Phase 1: validation
for i = 1, #KEYS do
    local key = KEYS[i]
    local increaseAmount = tonumber(ARGV[i])

    local currentStock = redis.call('GET', key)
    if currentStock == false then
        return -1  -- Stock key not found
    end
end

-- Phase 2: increase
for i = 1, #KEYS do
    local key = KEYS[i]
    local increaseAmount = tonumber(ARGV[i])
    redis.call('INCRBY', key, increaseAmount)
end

return #KEYS
