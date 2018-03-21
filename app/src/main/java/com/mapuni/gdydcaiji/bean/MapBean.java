package com.mapuni.gdydcaiji.bean;

import java.util.List;

/**
 * Created by Summer on 2017/3/31.
 */

public class MapBean {


    private List<SlicesBean> slices;

    public List<SlicesBean> getSlices() {
        return slices;
    }

    public void setSlices(List<SlicesBean> slices) {
        this.slices = slices;
    }

    public static class SlicesBean {
        /**
         * id : 53
         * filename : BJtest.zip
         * filepath : /fileslices/2017/3/31/3bcb0c3313544226a624ffc8c42cccbc.zip
         * detail : 北京
         * fileSize : 15194227
         */

        private int id;
        private String filename;
        private String filepath;
        private String detail;
        private int fileSize;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getFilepath() {
            return filepath;
        }

        public void setFilepath(String filepath) {
            this.filepath = filepath;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public int getFileSize() {
            return fileSize;
        }

        public void setFileSize(int fileSize) {
            this.fileSize = fileSize;
        }
    }
}
