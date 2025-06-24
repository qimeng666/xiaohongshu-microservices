package com.example.xiaohongshu_microservices.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.xiaohongshu_microservices.domain.Users;
import com.example.xiaohongshu_microservices.service.UsersService;
import com.example.xiaohongshu_microservices.mapper.UsersMapper;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

/**
* @author qimeng
* @description 针对表【USERS】的数据库操作Service实现
* @createDate 2025-06-24 00:27:23
*/
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users>
    implements UsersService{

}




