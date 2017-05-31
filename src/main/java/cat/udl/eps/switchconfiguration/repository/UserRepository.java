package cat.udl.eps.switchconfiguration.repository;

import cat.udl.eps.switchconfiguration.domain.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource
public interface UserRepository extends PagingAndSortingRepository<User, String> {
}
