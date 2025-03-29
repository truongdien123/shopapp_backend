package com.fpt.shopapp.services.imp;

import com.fpt.shopapp.dto.ProductDTO;
import com.fpt.shopapp.dto.ProductImageDTO;
import com.fpt.shopapp.exceptions.DataNotFoundException;
import com.fpt.shopapp.exceptions.InvalidParamException;
import com.fpt.shopapp.model.Category;
import com.fpt.shopapp.model.Product;
import com.fpt.shopapp.model.ProductImage;
import com.fpt.shopapp.repositories.CategoryRepository;
import com.fpt.shopapp.repositories.ProductImageRepository;
import com.fpt.shopapp.repositories.ProductRepository;
import com.fpt.shopapp.responses.ProductResponse;
import com.fpt.shopapp.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImp implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;

    @Override
    public Product createProduct(ProductDTO productDTO) {
        Category existingCategory = categoryRepository.findById(productDTO.getCategoryId()).orElseThrow(() -> new DataNotFoundException("Cannot find category with id: "+productDTO.getCategoryId()));
        Product product = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .description(productDTO.getDescription())
                .category(existingCategory)
                .build();
        return productRepository.save(product);
    }

    @Override
    public Product getProductById(Long id) {
        Optional<Product> optionalProduct = productRepository.getDetailProduct(id);
        if (optionalProduct.isPresent()) {
            return optionalProduct.get();
        }
        throw new DataNotFoundException("Cannot find product");
    }

    @Override
    public Page<ProductResponse> getAllProducts(String keyword, Long categoryId, PageRequest pageRequest) {
        Page<Product> productsPage = productRepository.searchProducts(categoryId, keyword, pageRequest);
        return productsPage.map(ProductResponse::fromProduct);
    }

    @Override
    public Product updateProduct(Long id, ProductDTO productDTO) {
        Product existingProduct = getProductById(id);
        if (existingProduct != null){
            Category existingCategory = categoryRepository.findById(productDTO.getCategoryId()).orElseThrow(() -> new DataNotFoundException("Cannot find category with id: "+productDTO.getCategoryId()));
            existingProduct.setName(productDTO.getName());
            existingProduct.setCategory(existingCategory);
            existingProduct.setDescription(productDTO.getDescription());
            existingProduct.setThumbnail(productDTO.getThumbnail());
            return productRepository.save(existingProduct);
        }
        return null;
    }

    @Override
    public void deleteProduct(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        optionalProduct.ifPresent(productRepository::delete);
    }

    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    @Override
    public ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO){
        Product existingProduct = productRepository.findById(productId).orElseThrow(() -> new DataNotFoundException("Cannot find category with id: "+productImageDTO.getProductId()));
        ProductImage productImage = ProductImage.builder()
                .product(existingProduct)
                .imageUrl(productImageDTO.getImageUrl())
                .build();
        // Ko cho insert quá 5 ảnh cho 1 sản phẩm
        int size = productImageRepository.findByProductId(productId).size();
        if (size >= 5){
            throw new InvalidParamException("Number of images must be <= 5");
        }
        return productImageRepository.save(productImage);
    }

    @Override
    public List<Product> findProductsByIds(List<Long> productId) {
        return productRepository.findProductsByIds(productId);
    }
}
