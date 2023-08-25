package com.clothifystore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderStatsResponseDTO {
    private double incomeOfToday;
    private double incomeOfYesterday;
    private double incomeOfLast30Days;
    private int pendingOrderCount;
    private int processingOrderCount;
    private int deliveringOrderCount;
}
