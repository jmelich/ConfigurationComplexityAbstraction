package cat.udl.eps.switchconfiguration.repository;


import cat.udl.eps.switchconfiguration.domain.Connector;
import cat.udl.eps.switchconfiguration.domain.Dealer;
import cat.udl.eps.switchconfiguration.domain.Equipment;
import cat.udl.eps.switchconfiguration.domain.Floor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


@RepositoryRestResource
public interface ConnectorRepository extends PagingAndSortingRepository<Connector, Long> {
    List<Connector> findByDescription(@Param("description") String description);
    List<Connector> findByTitleContainingIgnoreCase(@Param("title") String title);
    List<Connector> findByDescriptionContaining(@Param("description") String description);
    List<Connector> findByTitleContainingIgnoreCaseAndIsInFloor(@Param("title") String title, @Param("floor") Floor floor);
}
