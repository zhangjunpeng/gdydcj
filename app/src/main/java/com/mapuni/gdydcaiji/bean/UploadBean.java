package com.mapuni.gdydcaiji.bean;

/**
 * Created by yf on 2018/3/23.
 */

public class UploadBean {

    /**
     * tb_point : {"status":true,"message":"tb_point-解析成功！"}
     * tb_line : {"status":true,"message":"tb_line-解析成功！"}
     * tb_surface : {"status":true,"message":"tb_surface-解析成功！"}
     * result : 处理成功
     */

    private TbPointBean tb_point;
    private TbLineBean tb_line;
    private TbSurfaceBean tb_surface;
    private String result;

    public TbPointBean getTb_point() {
        return tb_point;
    }

    public void setTb_point(TbPointBean tb_point) {
        this.tb_point = tb_point;
    }

    public TbLineBean getTb_line() {
        return tb_line;
    }

    public void setTb_line(TbLineBean tb_line) {
        this.tb_line = tb_line;
    }

    public TbSurfaceBean getTb_surface() {
        return tb_surface;
    }

    public void setTb_surface(TbSurfaceBean tb_surface) {
        this.tb_surface = tb_surface;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public static class TbPointBean {
        /**
         * status : true
         * message : tb_point-解析成功！
         */

        private boolean status;
        private String message;

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "TbPointBean{" +
                    "status=" + status +
                    ", message='" + message + '\'' +
                    '}';
        }
    }

    public static class TbLineBean {
        /**
         * status : true
         * message : tb_line-解析成功！
         */

        private boolean status;
        private String message;

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "TbLineBean{" +
                    "status=" + status +
                    ", message='" + message + '\'' +
                    '}';
        }
    }

    public static class TbSurfaceBean {
        /**
         * status : true
         * message : tb_surface-解析成功！
         */

        private boolean status;
        private String message;

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "TbSurfaceBean{" +
                    "status=" + status +
                    ", message='" + message + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "UploadBean{" +
                "tb_point=" + tb_point +
                ", tb_line=" + tb_line +
                ", tb_surface=" + tb_surface +
                ", result='" + result + '\'' +
                '}';
    }
}
