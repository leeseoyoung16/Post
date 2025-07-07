package com.example.memo.report;

import lombok.Getter;

@Getter
public class ReportResponse
{
    private Long id;
    private String username;
    private String postTitle;

    public ReportResponse(Report report) {
        this.id = report.getId();
        this.postTitle = report.getPost().getTitle();
        this.username = report.getUser().getUsername();
    }
}
