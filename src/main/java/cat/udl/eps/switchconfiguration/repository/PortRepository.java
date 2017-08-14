package cat.udl.eps.switchconfiguration.repository;

import cat.udl.eps.switchconfiguration.domain.Port;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


@RepositoryRestResource
public interface PortRepository extends PagingAndSortingRepository<Port, Long> {
    List<Port> findByDescription(@Param("description") String description);
    List<Port> findByTitle(@Param("title") String title);
    List<Port> findByDescriptionContaining(@Param("description") String description);
}
