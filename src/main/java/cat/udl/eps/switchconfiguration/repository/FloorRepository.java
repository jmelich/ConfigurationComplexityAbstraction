package cat.udl.eps.switchconfiguration.repository;

import cat.udl.eps.switchconfiguration.domain.Floor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


@RepositoryRestResource
public interface FloorRepository extends PagingAndSortingRepository<Floor, Long> {
    List<Floor> findByDescription(@Param("description") String description);
    List<Floor> findByTitle(@Param("title") String title);
    List<Floor> findByDescriptionContaining(@Param("description") String description);
}
