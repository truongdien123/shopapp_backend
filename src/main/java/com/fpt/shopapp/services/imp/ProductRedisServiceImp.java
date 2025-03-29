package com.fpt.shopapp.services.imp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fpt.shopapp.responses.ProductResponse;
import com.fpt.shopapp.services.ProductRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductRedisServiceImp implements ProductRedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper redisObjectMapper;

    private String getKeyFrom(String keyword, Long categoryId, PageRequest pageRequest) {
        int pageNumber = pageRequest.getPageNumber();
        int pageSize = pageRequest.getPageSize();
        Sort sort = pageRequest.getSort();
        String sortDirection = sort.getOrderFor("id").getDirection() == Sort.Direction.ASC ? "asc" : "desc";
        String key = String.format("all_product:%d:%d:%s", pageNumber, pageSize, sortDirection);
        return key;
    }

    @Override
    public void clear() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Override
    public List<ProductResponse> getAllProducts(String keyword, Long categoryId, PageRequest pageRequest) throws JsonProcessingException {
        String key = this.getKeyFrom(keyword, categoryId, pageRequest);
        String json = (String) redisTemplate.opsForValue().get(key);
        List<ProductResponse> productResponses = json != null ? redisObjectMapper.readValue(json, new TypeReference<List<ProductResponse>>() {
        }) : null;
        return productResponses;
    }

    @Override
    public void saveAllProduct(List<ProductResponse> productResponses, String keyword, Long categoryId, PageRequest pageRequest) throws JsonProcessingException {
        String key = this.getKeyFrom(keyword, categoryId, pageRequest);
        String json = redisObjectMapper.writeValueAsString(productResponses);
        redisTemplate.opsForValue().set(key, json);
    }
}
