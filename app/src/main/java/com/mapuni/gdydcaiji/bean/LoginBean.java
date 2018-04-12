package com.mapuni.gdydcaiji.bean;

/**
 * Created by yf on 2018/3/23.
 */

public class LoginBean {

    /**
     * user : {"createTime1":"","createUsername":"","depName":"北京大地图科技有限公司研发部","desc":"","emp_id":"","gridId":"","gridName":"","id":11,"imeiName":"","name":"手机端测试","password":"E10ADC3949BA59ABBE56E057F20F883E","phone":"","roleid":"1","sex":"","updateTime1":"","updateUsername":"admin","username":"mobiletest"}
     * key : YOMQy75GTC3Dj+lKypxlVxysVs9Xh22RjI5SqDuX0IyWNSDXG+ROnA==
     * keyType : zg7I6XWUeho=
     * status : 1
     * msg : 登录成功
     */

    private UserBean user;
    private String key;
    private String keyType;
    private int status;
    private String msg;

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class UserBean {
        /**
         * createTime1 : 
         * createUsername : 
         * depName : 北京大地图科技有限公司研发部
         * desc : 
         * emp_id : 
         * gridId : 
         * gridName : 
         * id : 11
         * imeiName : 
         * name : 手机端测试
         * password : E10ADC3949BA59ABBE56E057F20F883E
         * phone : 
         * roleid : 1   //2-》质检  6-》外业
         * sex : 
         * updateTime1 : 
         * updateUsername : admin
         * username : mobiletest
         */

        private String createTime1;
        private String createUsername;
        private String depName;
        private String desc;
        private String emp_id;
        private String gridId;
        private String gridName;
        private int id;
        private String imeiName;
        private String name;
        private String password;
        private String phone;
        private String roleid;
        private String sex;
        private String updateTime1;
        private String updateUsername;
        private String username;

        public String getCreateTime1() {
            return createTime1;
        }

        public void setCreateTime1(String createTime1) {
            this.createTime1 = createTime1;
        }

        public String getCreateUsername() {
            return createUsername;
        }

        public void setCreateUsername(String createUsername) {
            this.createUsername = createUsername;
        }

        public String getDepName() {
            return depName;
        }

        public void setDepName(String depName) {
            this.depName = depName;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getEmp_id() {
            return emp_id;
        }

        public void setEmp_id(String emp_id) {
            this.emp_id = emp_id;
        }

        public String getGridId() {
            return gridId;
        }

        public void setGridId(String gridId) {
            this.gridId = gridId;
        }

        public String getGridName() {
            return gridName;
        }

        public void setGridName(String gridName) {
            this.gridName = gridName;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getImeiName() {
            return imeiName;
        }

        public void setImeiName(String imeiName) {
            this.imeiName = imeiName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getRoleid() {
            return roleid;
        }

        public void setRoleid(String roleid) {
            this.roleid = roleid;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getUpdateTime1() {
            return updateTime1;
        }

        public void setUpdateTime1(String updateTime1) {
            this.updateTime1 = updateTime1;
        }

        public String getUpdateUsername() {
            return updateUsername;
        }

        public void setUpdateUsername(String updateUsername) {
            this.updateUsername = updateUsername;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
