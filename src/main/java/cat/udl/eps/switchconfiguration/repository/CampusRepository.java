package cat.udl.eps.switchconfiguration.repository;

import cat.udl.eps.switchconfiguration.domain.Campus;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


@RepositoryRestResource
public interface CampusRepository extends PagingAndSortingRepository<Campus, Long> {
    List<Campus> findByTitleContainingIgnoreCase(@Param("title") String title);
}
