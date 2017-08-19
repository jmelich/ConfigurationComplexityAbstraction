package cat.udl.eps.switchconfiguration.repository;

import cat.udl.eps.switchconfiguration.domain.Dealer;
import cat.udl.eps.switchconfiguration.domain.Floor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


@RepositoryRestResource
public interface DealerRepository extends PagingAndSortingRepository<Dealer, Long> {
    List<Dealer> findByTitleContainingIgnoreCase(@Param("title") String title);
    List<Dealer> findByTitleContainingIgnoreCaseAndIsInFloor(@Param("title") String title, @Param("floor") Floor floor);
}
