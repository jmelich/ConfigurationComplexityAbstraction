package cat.udl.eps.switchconfiguration.repository;

import cat.udl.eps.switchconfiguration.domain.Building;
import cat.udl.eps.switchconfiguration.domain.Campus;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


@RepositoryRestResource
public interface BuildingRepository extends PagingAndSortingRepository<Building, Long> {
    List<Building> findByDescription(@Param("description") String description);
    List<Building> findByTitleContainingIgnoreCase(@Param("title") String title);
    List<Building> findByDescriptionContaining(@Param("description") String description);
    List<Building> findByTitleContainingIgnoreCaseAndIsInCampus(@Param("title") String title, @Param("campus") Campus campus);
}
