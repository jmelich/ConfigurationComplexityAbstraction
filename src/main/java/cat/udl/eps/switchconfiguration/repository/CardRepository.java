package cat.udl.eps.switchconfiguration.repository;

import cat.udl.eps.switchconfiguration.domain.Card;
import cat.udl.eps.switchconfiguration.domain.Equipment;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


@RepositoryRestResource
public interface CardRepository extends PagingAndSortingRepository<Card, Long> {
    List<Card> findByTitleContainingIgnoreCase(@Param("title") String title);
    List<Card> findByTitleContainingIgnoreCaseAndIsInEquipment(@Param("title") String title, @Param("equipment") Equipment equipment);
}
