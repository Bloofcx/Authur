package com.cx;

import com.cx.authur.core.ServerCoreMain8110;
import com.cx.authur.core.mapper.DictMapper;
import com.cx.authur.core.pojo.entity.Dict;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author ChenXu
 * @create 2022-02-16-19:30
 */

@SpringBootTest(classes = ServerCoreMain8110.class)
@RunWith(SpringRunner.class)
public class RedisTest {
    @Resource
    RedisTemplate redisTemplate;

    @Resource
    DictMapper dictMapper;

    @Test
    public void testRedisIn(){
        Dict dict = dictMapper.selectById(1);
        redisTemplate.opsForValue().set("dict",dict,5, TimeUnit.MINUTES);
    }

    @Test
    public void getRedisDict(){
        Dict dict = (Dict) redisTemplate.opsForValue().get("dict");
        System.out.println(dict);
    }

}
