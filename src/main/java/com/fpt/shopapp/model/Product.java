package com.fpt.shopapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(ProductListener.class)
public class Product extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 350)
    private String name;

    private Float price;

    @Column(name = "thumbnail", length = 300)
    private String thumbnail;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductImage> productImages;
    // cascade = CascadeType.ALL: Khi có bất kỳ hành động nào (INSERT, UPDATE, DELETE) trên thực thể Product, các thực thể ProductImage liên kết cũng sẽ bị ảnh hưởng
    /* fetch = FetchType.LAZY:
    * Thiết lập cơ chế tải dữ liệu lười (lazy).
      Các ProductImage liên kết sẽ không được tải ngay lập tức khi truy vấn Product.
      Dữ liệu chỉ được truy xuất khi thực sự cần thiết (khi gọi product.getProductImages()).
      Điều này giúp cải thiện hiệu suất khi không cần tải dữ liệu không cần thiết.
    * */

}
