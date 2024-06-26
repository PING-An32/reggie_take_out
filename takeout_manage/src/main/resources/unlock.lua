-- 存的时候key = KEYS[1] |  value = ARGV[1]
-- 如果get KEYS[1]得到的ARGV[1]，那么表明当前锁是由自己加的，可以删除
if(redis.call('get'),KEYS[1]) == ARGV[1] then
    return redis.call('del',KEYS[1])
end
return 0