package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


/**
 * 配置spring和junit整合，junit启动时加载springIOC容器
 */
@RunWith(SpringJUnit4ClassRunner.class)
// 需要告诉junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

    // 注入Dao实现类依赖
    @Resource
    private SeckillDao seckillDao;

    @Test
    public void reduceNumber() throws Exception {
        Date killTime = new Date();
        int count = seckillDao.reduceNumber(1000L, killTime);
        System.out.println("update count = " + count );
    }

    @Test
    public void queryById() throws Exception {
        long id = 1000;
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(seckill.toString());
    }

    @Test
    public void queryAll() throws Exception {
        /**
         * org.mybatis.spring.MyBatisSystemException: nested exception is org.apache.ibatis.binding.BindingException:
         * Parameter 'offset' not found. Available parameters are [0, 1, param1, param2]
         *
         * queryAll(int offset, int limit)
         * 没有保存形参的记录 queryAll(int offset, int limit) -> queryAll(arg0, arg1)
         * 只需要修改注解
         */
        List<Seckill> seckillList = seckillDao.queryAll(0, 100);
        for (Seckill in : seckillList) {
            System.out.println(in.toString());
        }

    }

}