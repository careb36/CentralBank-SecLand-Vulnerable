package com.secland.centralbank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private Long id;
    private String username;
    private String fullName;
    private LocalDateTime createdAt;
    private List<Long> accountIds;
    private int accountCount;
}