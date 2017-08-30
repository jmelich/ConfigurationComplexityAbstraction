package cat.udl.eps.switchconfiguration.repository;


import cat.udl.eps.switchconfiguration.domain.EquipmentRoom;
import cat.udl.eps.switchconfiguration.domain.Equipment;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


@RepositoryRestResource
public interface EquipmentRepository extends PagingAndSortingRepository<Equipment, Long> {
    List<Equipment> findByTitleContainingIgnoreCase(@Param("title") String title);
    List<Equipment> findByTitleContainingIgnoreCaseAndIsInEquipmentRoom(@Param("title") String title, @Param("equipmentRoom") EquipmentRoom equipmentRoom);
}
