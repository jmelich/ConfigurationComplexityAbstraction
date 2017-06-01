package cat.udl.eps.switchconfiguration.repository;

import cat.udl.eps.switchconfiguration.domain.Room;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


@RepositoryRestResource
public interface RoomRepository extends PagingAndSortingRepository<Room, Long> {
    List<Room> findByDescription(@Param("description") String description);
    List<Room> findByTitle(@Param("title") String title);
    List<Room> findByDescriptionContaining(@Param("description") String description);
}
