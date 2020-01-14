package com.tina.mail.service;


import com.tina.mail.pojo.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public void insertUser(User user){
        System.out.println(user.getId()+";;"+user.getName());
    }

}
