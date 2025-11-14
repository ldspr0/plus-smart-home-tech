package ru.yandex.practicum.interactionapi.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageableDto {
    @Min(0)
    private Integer page = 0;
    @Min(1)
    private Integer size = 20;
    private List<String> sort = Collections.emptyList();
}