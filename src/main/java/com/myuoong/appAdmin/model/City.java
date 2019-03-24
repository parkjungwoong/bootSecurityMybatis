package com.myuoong.appAdmin.model;

import lombok.*;
import org.apache.ibatis.type.Alias;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Alias("city")
public class City {
    private Long id;
    @NonNull
    private String name;
    @NonNull
    private String country;
    @NonNull
    private Long population;
}