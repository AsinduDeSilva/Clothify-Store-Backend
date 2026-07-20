package com.clothifystore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AdminResponseDTO {
    private int adminID;
    private String name;
    private String email;
}
