package com.bc.ywjphone.readily.business;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.bc.ywjphone.readily.business.base.BaseBusiness;
import com.bc.ywjphone.readily.database.dao.UserDao;
import com.bc.ywjphone.readily.entity.Users;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/14 0014.
 */
public class UserBusiness extends BaseBusiness {

    private UserDao userDao;
    List<Users> list;

    public UserBusiness(Context context) {
        super(context);
        userDao = new UserDao(context);
    }

    public boolean insertUser(Users user) {
        boolean result = userDao.insertUser(user);
        //谁调用谁处理此result
        return result;
    }

    //物理删除
    public boolean deleteUserByUserId(int userId) {
        String condition = " and userId=" + userId;
        boolean result = userDao.deleteUser(condition);
        return result;
    }

    public boolean updateUser(Users user) {
        //此处不用加and
        String condition = " userId=" + user.getUserId();
        boolean result = userDao.updateUser(condition, user);
        return result;
    }

    //查找全部用户
    public List<Users> getUsers(String condition) {
        return userDao.getUsers(condition);
    }

    //获取单个用户
    public Users getUserByUserId(int userId) {
        //有1=1 所以需要写and语句
        List<Users> list = userDao.getUsers(" and userId=" + userId);
        if (list != null && list.size() == 1) {
            return list.get(0);
        } else {
            return null;
        }
    }

    //查找所有被隐藏的用户
    public List<Users> getNotHideUser() {
        return userDao.getUsers(" and state=1 ");
    }

    //查找用户是否存在
    public boolean isExitUserByUserName(String username, Integer userId) {
        String condition = " and userName='" + username + "'";
        if (userId != null) {
            condition += " and userId <> " + userId;
        }
        list = userDao.getUsers(condition);
        if (list != null && list.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    //在这里封装一个方法，用来判断 已经存在的 ，状态为删除的 用户
    public boolean isStateDel(String username) {
        boolean boo = true;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getState() == 0) {
                //在这里进行修改用户状态的语句
                ContentValues contentValues = new ContentValues();
                contentValues.put("state", 1);
                String condition1 = " userName='" + username + "'";
                userDao.updateUser(condition1, contentValues);
                boo = false;
            }
        }
        return boo;
    }

    //查看单个的方法
    public Users getUser(String conticion) {
        List<Users> users = userDao.getUsers(conticion);
        return users.get(0);
    }

    public boolean hideUserByUserId(int userId) {
        String condition = " userId=" + userId;
        ContentValues contentValues = new ContentValues();
        contentValues.put("state", 0);
        return userDao.updateUser(condition, contentValues);
    }

    public String getUserNameByUserId(String UserId){//UserId : 1,2,3,
       // Log.e("TAG", "userId===="+UserId);
        List<Users> list = getUserListByUserIdArray(UserId.split(","));
        String name = "";
        for(int i = 0; i < list.size(); i++) {
            name+=list.get(i).getUserName()+",";
        }
        return name;
    }
    public List<Users> getUserListByUserIdArray(String[] userIds){
        List<Users> list = new ArrayList<>();
        for(int i = 0; i < userIds.length; i++) {
            list.add(getUserByUserId(Integer.valueOf(userIds[i])));
        }
        return list;
    }
}
