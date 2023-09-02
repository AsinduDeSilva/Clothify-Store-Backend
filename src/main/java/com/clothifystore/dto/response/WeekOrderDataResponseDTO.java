package com.clothifystore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class WeekOrderDataResponseDTO {
    List<Integer> orderCountList;
    List<String> dateList;
}
