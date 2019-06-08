package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;

import java.util.List;

/**
 * 业务接口：站在"使用者角"度设计
 * 三个方面： 方法定义粒度（明确才行），参数，返回类型（return 类型要友好 / 异常）
 */
public interface SeckillService {

    /**
     * 查询所有秒杀商品的记录（并非秒杀成功）
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 查询单个秒杀商品的信息
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);

    /**
     * 秒杀开启时 输出秒杀接口地址，否则输出系统时间和秒杀时间
     * @param seckillId
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀操作
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException;

    /**
     * 执行秒杀操作（利用存储过程）
     * 异常去掉，因为之前是用来告诉Spring声明式事务
     * 现在都是利用存储过程来控制 rollback还是commit
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5);

}
