package com.example.blitzbuy.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Heather
 * @version 1.0
 */
@Data
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionID=1L;

    // id: user's phone number
    @TableId(value="id", type = IdType.ASSIGN_ID)
    private Long id;

    private String nickname;

    private String password;

    private String salt;

    private String head;

    private Date registerDate;

    private Date lastLoginDate;

    private Integer loginCount;


}
