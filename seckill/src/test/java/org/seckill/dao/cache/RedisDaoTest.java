package org.seckill.dao.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SeckillDao;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Created by longxingyu on 2019/6/1.
 */
@RunWith(SpringJUnit4ClassRunner.class)
// 需要告诉junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class RedisDaoTest {

    private long id = 1001;
    @Autowired
    private RedisDao redisDao;

    @Autowired
    private SeckillDao seckillDao;

    @Test
    public void testSeckill() throws Exception {
        Seckill seckill = redisDao.getSeckill(id);
        if (seckill == null) {
            // 则需要从数据库中获取
            seckill = seckillDao.queryById(id);
            if (seckill != null) {
                String res = redisDao.putSeckill(seckill);
                System.out.println(res);
                seckill = redisDao.getSeckill(id);
                System.out.println(seckill);
            }
        } else {
            System.out.println(seckill);
        }
    }
}