package com.kimlongdev.shopme_backend.mapper.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface BaseMapperConfig {
    // Nơi chứa các method default chung nếu cần (ví dụ map Date -> String format chuẩn)
}
