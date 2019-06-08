package org.seckill.dao;


import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SeckillDao {
    // 每一个Dao对应着一个实体对象

    /**
     * 减库存
     * @param seckillId
     * @param killTime 对应数据库的createTime
     * @return 如果影响行数 > 1 表示更新行数的记录行数
     */
    int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    /**
     * 通过id查询seckill对象
     * @param seckillId
     * @return
     */
    Seckill queryById(long seckillId);

    /**
     * 列表，分页用的。
     * @param offset
     * @param limit
     * @return
     */
    List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 使用存储过程执行秒杀
     * @param paramMap
     */
    void killByProcedure(Map<String, Object> paramMap);
}
