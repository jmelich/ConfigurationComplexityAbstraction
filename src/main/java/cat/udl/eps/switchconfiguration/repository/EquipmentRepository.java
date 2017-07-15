package cat.udl.eps.switchconfiguration.repository;


import cat.udl.eps.switchconfiguration.domain.Equipment;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


@RepositoryRestResource
public interface EquipmentRepository extends PagingAndSortingRepository<Equipment, Long> {
    List<Equipment> findByDescription(@Param("description") String description);
    List<Equipment> findByTitleContaining(@Param("title") String title);
    List<Equipment> findByDescriptionContaining(@Param("description") String description);
}
