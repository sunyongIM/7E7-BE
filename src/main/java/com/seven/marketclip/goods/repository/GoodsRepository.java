package com.seven.marketclip.goods.repository;

import com.seven.marketclip.goods.domain.Goods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GoodsRepository extends JpaRepository<Goods, Long> {
    List<Goods> findAllByOrderByCreatedAtDesc();

    @Modifying
    @Query("UPDATE Goods p SET p.viewCount = p.viewCount + 1 where p.id = :id")
    void updateView(Long id);

    @Modifying
    @Query("update Goods p set p.wishCount = p.wishCount + :value where p.id = :id")
    void updateWishCount(Long id, Integer value);
    //@Query(value = "SELECT p FROM Goods p left join WishLists w on p = p.id + order by count(w) desc")
    //List<Goods> findAllByOrderByWishListsIdsCount();
}
