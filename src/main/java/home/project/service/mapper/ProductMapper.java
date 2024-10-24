package home.project.service.mapper;

import home.project.domain.Product;
import home.project.domain.elasticsearch.ProductDocument;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public ProductDocument toDocument(Product product) {
        ProductDocument doc = new ProductDocument();
        doc.setId(product.getId());
        doc.setName(product.getName());
        doc.setBrand(product.getBrand());
        doc.setCategoryCode(product.getCategory().getCode());
        doc.setProductNum(product.getProductNum());
        doc.setStock(product.getStock());
        doc.setSoldQuantity(product.getSoldQuantity());
        doc.setPrice(product.getPrice());
        doc.setDiscountRate(product.getDiscountRate());
        doc.setDefectiveStock(product.getDefectiveStock());
        doc.setDescription(product.getDescription());
        doc.setCreateAt(product.getCreateAt());
        doc.setImageUrl(product.getImageUrl());
        return doc;
    }
}