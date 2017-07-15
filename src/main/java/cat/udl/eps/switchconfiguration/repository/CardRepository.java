package cat.udl.eps.switchconfiguration.repository;

import cat.udl.eps.switchconfiguration.domain.Building;
import cat.udl.eps.switchconfiguration.domain.Card;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


@RepositoryRestResource
public interface CardRepository extends PagingAndSortingRepository<Card, Long> {
    //List<Card> findByBelongsToAndNumberOfCard(@Param("equipment,number") String equipment, int number);
    //List<Card> findByDescription(@Param("description") String description);
    //List<Card> findByTitle(@Param("title") String title);
    //List<Card> findByDescriptionContaining(@Param("description") String description);
}
