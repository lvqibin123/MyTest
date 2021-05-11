package com.lqb.mytest.util;


import lombok.Data;

@Data
public class Status  {

    private String success;
    private String msg;
    private String id;
    private User user;
}
