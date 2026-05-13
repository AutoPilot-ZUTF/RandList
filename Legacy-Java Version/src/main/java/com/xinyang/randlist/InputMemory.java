package com.xinyang.randlist;

public class InputMemory {

    private String simpleLowerBound = "";
    private String simpleUpperBound = "";
    private String simpleAmount = "";

    private String includeLowerBound = "";
    private String includeUpperBound = "";
    private String includeAmount = "";
    private String includeList = "";

    private String excludeLowerBound = "";
    private String excludeUpperBound = "";
    private String excludeAmount = "";
    private String excludeList = "";

    private String specifyList = "";
    private String specifyAmount = "";

    public void clear() {
        simpleLowerBound = "";
        simpleUpperBound = "";
        simpleAmount = "";

        includeLowerBound = "";
        includeUpperBound = "";
        includeAmount = "";
        includeList = "";

        excludeLowerBound = "";
        excludeUpperBound = "";
        excludeAmount = "";
        excludeList = "";

        specifyList = "";
        specifyAmount = "";
    }

    public String getSimpleLowerBound() {
        return simpleLowerBound;
    }

    public void setSimpleLowerBound(String simpleLowerBound) {
        this.simpleLowerBound = simpleLowerBound;
    }

    public String getSimpleUpperBound() {
        return simpleUpperBound;
    }

    public void setSimpleUpperBound(String simpleUpperBound) {
        this.simpleUpperBound = simpleUpperBound;
    }

    public String getSimpleAmount() {
        return simpleAmount;
    }

    public void setSimpleAmount(String simpleAmount) {
        this.simpleAmount = simpleAmount;
    }

    public String getIncludeLowerBound() {
        return includeLowerBound;
    }

    public void setIncludeLowerBound(String includeLowerBound) {
        this.includeLowerBound = includeLowerBound;
    }

    public String getIncludeUpperBound() {
        return includeUpperBound;
    }

    public void setIncludeUpperBound(String includeUpperBound) {
        this.includeUpperBound = includeUpperBound;
    }

    public String getIncludeAmount() {
        return includeAmount;
    }

    public void setIncludeAmount(String includeAmount) {
        this.includeAmount = includeAmount;
    }

    public String getIncludeList() {
        return includeList;
    }

    public void setIncludeList(String includeList) {
        this.includeList = includeList;
    }

    public String getExcludeLowerBound() {
        return excludeLowerBound;
    }

    public void setExcludeLowerBound(String excludeLowerBound) {
        this.excludeLowerBound = excludeLowerBound;
    }

    public String getExcludeUpperBound() {
        return excludeUpperBound;
    }

    public void setExcludeUpperBound(String excludeUpperBound) {
        this.excludeUpperBound = excludeUpperBound;
    }

    public String getExcludeAmount() {
        return excludeAmount;
    }

    public void setExcludeAmount(String excludeAmount) {
        this.excludeAmount = excludeAmount;
    }

    public String getExcludeList() {
        return excludeList;
    }

    public void setExcludeList(String excludeList) {
        this.excludeList = excludeList;
    }

    public String getSpecifyList() {
        return specifyList;
    }

    public void setSpecifyList(String specifyList) {
        this.specifyList = specifyList;
    }

    public String getSpecifyAmount() {
        return specifyAmount;
    }

    public void setSpecifyAmount(String specifyAmount) {
        this.specifyAmount = specifyAmount;
    }
}
