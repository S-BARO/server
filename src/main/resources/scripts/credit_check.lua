local key = KEYS[1]
local current_time = tonumber(ARGV[1])

local max_capacity = 10
local refill_rate_per_hour = 3
local refill_interval = 3600 / refill_rate_per_hour

local bucket = redis.call('GET', key)

-- 기존 크레딧 데이터 파싱 또는 초기 크레딧 생성
local tokens, last_refill_time
if bucket then
    local data = {}
    for part in string.gmatch(bucket, '[^:]+') do
        table.insert(data, part)
    end
    tokens = tonumber(data[1])
    last_refill_time = tonumber(data[2])
else
    tokens = max_capacity
    last_refill_time = current_time
end

-- 크레딧 충전 로직 (시간 경과에 따른)
local elapsed_time = current_time - last_refill_time
local tokens_to_add = math.floor(elapsed_time / refill_interval)

if tokens_to_add > 0 then
    tokens = math.min(tokens + tokens_to_add, max_capacity)
    last_refill_time = current_time
end

-- 크레딧 가용성 검증 및 상태 저장
local new_bucket_value = tokens .. ':' .. last_refill_time
redis.call('SET', key, new_bucket_value, 'EX', 86400)

if tokens > 0 then
    return 1
else
    return 0
end
