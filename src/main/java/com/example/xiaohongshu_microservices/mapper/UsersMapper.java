package com.example.xiaohongshu_microservices.mapper;

import com.example.xiaohongshu_microservices.domain.Users;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author qimeng
* @description 针对表【USERS】的数据库操作Mapper
* @createDate 2025-06-24 00:27:24
* @Entity generator.domain.Users
*/
@Mapper
public interface UsersMapper extends BaseMapper<Users> {

}




