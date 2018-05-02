package com.eemp.dataprocess;

import com.eemp.utils.JedisUtils;



/**
 * Created by A03742 on 2018-04-28.
 */

public class RedisData {

    private JedisUtils redisUtil;


    public void wirteData(){
        JedisUtils.setString("redisUtilTest",1000,"my redis");
    }

    public String readData(){
        return JedisUtils.getString("redisUtilTest");
    }
}
