package events.user.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import events.user.model.User;

import java.util.List;

public interface UserRepository extends PagingAndSortingRepository<User, Long>, JpaRepository<User, Long> {
    List<User> findAllByIdIn(List<Long> ids, Pageable pageable);

    boolean existsByEmail(String email);
}