package cat.udl.eps.switchconfiguration.repository;

import cat.udl.eps.switchconfiguration.domain.EquipmentRoom;
import cat.udl.eps.switchconfiguration.domain.Floor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


@RepositoryRestResource
public interface EquipmentRoomRepository extends PagingAndSortingRepository<EquipmentRoom, Long> {
    List<EquipmentRoom> findByTitleContainingIgnoreCase(@Param("title") String title);
    List<EquipmentRoom> findByTitleContainingIgnoreCaseAndIsInFloor(@Param("title") String title, @Param("floor") Floor floor);
}
