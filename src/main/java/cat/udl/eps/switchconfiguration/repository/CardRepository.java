package cat.udl.eps.switchconfiguration.repository;

import cat.udl.eps.switchconfiguration.domain.Building;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


@RepositoryRestResource
public interface BuildingRepository extends PagingAndSortingRepository<Building, Long> {
    List<Building> findByDescription(@Param("description") String description);
    List<Building> findByTitle(@Param("title") String title);
    List<Building> findByDescriptionContaining(@Param("description") String description);
}
