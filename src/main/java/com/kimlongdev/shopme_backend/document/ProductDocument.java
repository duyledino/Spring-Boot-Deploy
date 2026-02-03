package com.kimlongdev.shopme_backend.document;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.math.BigDecimal;
import java.util.List;

@Data
@Document(indexName = "products") // Tên index trong ES
@Setting(settingPath = "es_settings.json") // File cấu hình Tokenizer tiếng Việt
public class ProductDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "vi_analyzer")
    private String name;

    @Field(type = FieldType.Text, analyzer = "vi_analyzer")
    private String description;

    @Field(type = FieldType.Double)
    private BigDecimal price;

    @Field(type = FieldType.Keyword) // Keyword để filter chính xác (không tách từ)
    private String categoryName;

    @Field(type = FieldType.Keyword)
    private String brandName;

    @Field(type = FieldType.Keyword)
    private List<String> tags;       // Tags để gợi ý

    @Field(type = FieldType.Boolean)
    private Boolean active;
}
