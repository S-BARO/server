local key = KEYS[1]
local current_time = tonumber(ARGV[1])

local max_capacity = 10

local bucket = redis.call('GET', key)

-- 크레딧 데이터 존재 확인 및 파싱
if not bucket then
    return 0
end

local data = {}
for part in string.gmatch(bucket, '[^:]+') do
    table.insert(data, part)
end

local tokens = tonumber(data[1])
local last_refill_time = tonumber(data[2])

-- 크레딧 환불 (max_capacity를 초과하지 않도록)
tokens = math.min(tokens + 1, max_capacity)
local new_bucket_value = tokens .. ':' .. last_refill_time
redis.call('SET', key, new_bucket_value, 'EX', 86400)
return 1
