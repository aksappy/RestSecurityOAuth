package hello.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface URIAccessRepository extends JpaRepository<OAuthURIAccess, Integer> {

	@Query(value = "SELECT p.id, p.uri, p.role, p.secured FROM oauth_access_uri p WHERE p.uri LIKE %:searchTerm%", nativeQuery = true)
	public List<OAuthURIAccess> getURIConfiguration(@Param("searchTerm") String searchTerm);

}
