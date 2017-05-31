package cat.udl.eps.switchconfiguration.repository;

import cat.udl.eps.switchconfiguration.domain.Dataset;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


@RepositoryRestResource
public interface DatasetRepository extends PagingAndSortingRepository<Dataset, Long> {
    List<Dataset> findByDescription(@Param("description") String description);
    List<Dataset> findByTitle(@Param("title") String title);
    List<Dataset> findByDescriptionContaining(@Param("description") String description);
}
