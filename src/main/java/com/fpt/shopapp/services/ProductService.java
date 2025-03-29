package com.fpt.shopapp.services;

import com.fpt.shopapp.dto.ProductDTO;
import com.fpt.shopapp.dto.ProductImageDTO;
import com.fpt.shopapp.model.Product;
import com.fpt.shopapp.model.ProductImage;
import com.fpt.shopapp.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ProductService {
    Product createProduct(ProductDTO productDTO);
    Product getProductById(Long id);
    Page<ProductResponse> getAllProducts(String keyword, Long categoryId, PageRequest pageRequest);
    Product updateProduct(Long id, ProductDTO productDTO);
    void deleteProduct(Long id);
    boolean existsByName(String name);
    ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO);
    List<Product> findProductsByIds(List<Long> productId);
}
