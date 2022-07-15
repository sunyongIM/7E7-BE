package com.seven.marketclip.files.repository;

import com.seven.marketclip.files.domain.GoodsImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoodsImageRepository extends JpaRepository<GoodsImage, Long> {
    void deleteAllByGoodsId(Long goodsId);
    List<GoodsImage> findAllByGoodsId(Long goodsId);
}
