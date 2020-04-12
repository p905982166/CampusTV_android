package com.panxy.campustv.web.entity;

import java.util.Date;

public class NewsLists {
    private Long newsId;

    private Integer subTypesId;

    private String newsUrl;

    private String newsTitle;

    private String newsIcon;

    private Integer newsStatus;

    private Integer createBy;

    private Date createDate;

    private Integer reviewBy;

    private Date reviewDate;

    private Integer reviewResult;

    private String reviewRemark;

    private Integer lastEditBy;

    private Date lastEdit;

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    private String videoTitle;

    private Long likes;

    private Long dislikes;

    private Long commentsNum;

    private Integer sharable;

    private Long shareTimes;

    private Integer belongTo;

    private Long pageViews;

    public Long getNewsId() {
        return newsId;
    }

    public void setNewsId(Long newsId) {
        this.newsId = newsId;
    }

    public Integer getSubTypesId() {
        return subTypesId;
    }

    public void setSubTypesId(Integer subTypesId) {
        this.subTypesId = subTypesId;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    public void setNewsUrl(String newsUrl) {
        this.newsUrl = newsUrl == null ? null : newsUrl.trim();
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle == null ? null : newsTitle.trim();
    }

    public String getNewsIcon() {
        return newsIcon;
    }

    public void setNewsIcon(String newsIcon) {
        this.newsIcon = newsIcon == null ? null : newsIcon.trim();
    }

    public Integer getNewsStatus() {
        return newsStatus;
    }

    public void setNewsStatus(Integer newsStatus) {
        this.newsStatus = newsStatus;
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getReviewBy() {
        return reviewBy;
    }

    public void setReviewBy(Integer reviewBy) {
        this.reviewBy = reviewBy;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }

    public Integer getReviewResult() {
        return reviewResult;
    }

    public void setReviewResult(Integer reviewResult) {
        this.reviewResult = reviewResult;
    }

    public String getReviewRemark() {
        return reviewRemark;
    }

    public void setReviewRemark(String reviewRemark) {
        this.reviewRemark = reviewRemark == null ? null : reviewRemark.trim();
    }

    public Integer getLastEditBy() {
        return lastEditBy;
    }

    public void setLastEditBy(Integer lastEditBy) {
        this.lastEditBy = lastEditBy;
    }

    public Date getLastEdit() {
        return lastEdit;
    }

    public void setLastEdit(Date lastEdit) {
        this.lastEdit = lastEdit;
    }

    public Long getLikes() {
        return likes;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public Long getDislikes() {
        return dislikes;
    }

    public void setDislikes(Long dislikes) {
        this.dislikes = dislikes;
    }

    public Long getCommentsNum() {
        return commentsNum;
    }

    public void setCommentsNum(Long commentsNum) {
        this.commentsNum = commentsNum;
    }

    public Integer getSharable() {
        return sharable;
    }

    public void setSharable(Integer sharable) {
        this.sharable = sharable;
    }

    public Long getShareTimes() {
        return shareTimes;
    }

    public void setShareTimes(Long shareTimes) {
        this.shareTimes = shareTimes;
    }

    public Integer getBelongTo() {
        return belongTo;
    }

    public void setBelongTo(Integer belongTo) {
        this.belongTo = belongTo;
    }

    public Long getPageViews() {
        return pageViews;
    }

    public void setPageViews(Long pageViews) {
        this.pageViews = pageViews;
    }
}