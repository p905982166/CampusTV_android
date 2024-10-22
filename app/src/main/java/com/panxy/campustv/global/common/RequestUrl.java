package com.panxy.campustv.global.common;

public interface RequestUrl {
    /** 加载首页url*/
    String LOAD_URL = Config.domain + "#/";
    String LOAD_URL_TEST = Config.test_domain + "#/";

    /** 上传用户头像 */
    String UPLOAD_HEAD_IMAGE = Config.campus_accept_web + "sys_user/uploadUserHeadImage";

    /** 创建新闻 */
    String CREATE_NEWS = Config.campus_accept_web + "news/createNews";
    /** 创建动态 */
    String CREATE_SOCIAL = Config.campus_accept_web + "social/createSocial";

    /** 创建球队 */
    String CREATE_TEAM = Config.campus_accept_web + "team/createTeam";

}
