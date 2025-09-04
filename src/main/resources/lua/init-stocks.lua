local setCount = 0

for i = 1, #KEYS do
    local key = KEYS[i]
    local value = ARGV[i]
    
    if redis.call('EXISTS', key) == 0 then
        redis.call('SET', key, value)
        redis.call('EXPIRE', key, 43200) -- 12 hours
        setCount = setCount + 1
    end
end

return setCount