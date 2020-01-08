package com.bany.game.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

@Setter
@Getter
@Data
@TableName("sys_user")
public class User implements UserDetails {

    @TableId(type = IdType.AUTO)
    private Long id ;

    private String username;

    private String password;

    private boolean benable;

    @TableField(exist = false)

    private String roles; //需要修改

    private Date createTime;

    private String mobile;

    private String img;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

      Collection<GrantedAuthority> collection = new HashSet<>();
       /*   if (!CollectionUtils.isEmpty(this.getRoles())) {
            this.getRoles().parallelStream().forEach(role -> collection.add(new SimpleGrantedAuthority(role.getCode())));
        }*/
        collection.add(new SimpleGrantedAuthority(roles));


        return collection;

    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isBenable();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.isBenable();
    }

    @Override
    public boolean isEnabled() {
        return this.isBenable();
    }
}
