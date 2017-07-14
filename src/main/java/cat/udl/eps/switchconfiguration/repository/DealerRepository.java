package cat.udl.eps.switchconfiguration.repository;

import cat.udl.eps.switchconfiguration.domain.Dealer;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


@RepositoryRestResource
public interface DealerRepository extends PagingAndSortingRepository<Dealer, Long> {
    List<Dealer> findByDescription(@Param("description") String description);
    List<Dealer> findByTitleContaining(@Param("title") String title);
    List<Dealer> findByDescriptionContaining(@Param("description") String description);
}
