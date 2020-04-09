package com.panxy.campustv.global.common;

public interface RequestUrl {
    /** 加载首页url*/
    String LOAD_URL = Config.domain + "#/";
    String LOAD_URL_TEST = Config.test_domain + "#/";

    /** 上传文档url*/
    String UPLOAD_URL = Config.campus_accept_web + "app/lawyerJob/uploadFile";

    /** 查看已上传文档url*/
    String QUERY_UPLOADED_URL = Config.campus_accept_web + "app/lawyerJob/queryUploaded";

    /** 删除已上传文档url*/
    String DELETE_UPLOADED_URL = Config.campus_accept_web + "app/lawyerJob/deleteFile";

}
