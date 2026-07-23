package com.chu.sih.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardResponse {

    private String title;
    private String role;
    private List<String> permissions;
}
