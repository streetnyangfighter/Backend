package com.snp.takealook.api.repository.cat;

import com.snp.takealook.api.domain.cat.CatCare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CatCareRepository extends JpaRepository<CatCare, Long> {

    List<CatCare> findCatCaresByModifiedAtBefore(LocalDateTime dateTime);

    List<CatCare> findCatCaresByCat_IdAndCreatedAtBetween(Long cat_id, LocalDateTime dayStart, LocalDateTime dayEnd);
}
