package com.nihongodev.platform.domain.model;

import java.util.List;

public class WorkExperience {

    private String company;
    private String role;
    private int durationMonths;
    private List<String> highlights;

    public WorkExperience() {}

    public WorkExperience(String company, String role, int durationMonths, List<String> highlights) {
        this.company = company;
        this.role = role;
        this.durationMonths = durationMonths;
        this.highlights = highlights;
    }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public int getDurationMonths() { return durationMonths; }
    public void setDurationMonths(int durationMonths) { this.durationMonths = durationMonths; }
    public List<String> getHighlights() { return highlights; }
    public void setHighlights(List<String> highlights) { this.highlights = highlights; }
}
