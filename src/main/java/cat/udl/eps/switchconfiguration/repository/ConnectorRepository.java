package cat.udl.eps.switchconfiguration.repository;


import cat.udl.eps.switchconfiguration.domain.Connector;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


@RepositoryRestResource
public interface ConnectorRepository extends PagingAndSortingRepository<Connector, Long> {
    List<Connector> findByDescription(@Param("description") String description);
    List<Connector> findByTitle(@Param("title") String title);
    List<Connector> findByDescriptionContaining(@Param("description") String description);
}
