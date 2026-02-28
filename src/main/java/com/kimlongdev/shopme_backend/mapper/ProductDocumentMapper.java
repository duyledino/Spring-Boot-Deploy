package com.kimlongdev.shopme_backend.mapper;

import com.kimlongdev.shopme_backend.document.ProductDocument;
import com.kimlongdev.shopme_backend.entity.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductDocumentMapper {

    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(target = "brandName", ignore = true)
    @Mapping(source = "productId", target = "id")
    ProductDocument toDocument(Product product);

    List<ProductDocument> toDocumentList(List<Product> products);
}
