package com.study.mybatismapperpage.mapper;

import com.study.mybatismapperpage.entity.User;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * Created by IntelliJ IDEA.
 * User: KingRainGrey
 * Date: 2020/9/1
 */
@Component
public interface UserMapper extends Mapper<User>, MySqlMapper<User> {
}
