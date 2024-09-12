package events.compilations.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import events.compilations.model.Compilation;

import java.util.List;

public interface CompilationRepository extends PagingAndSortingRepository<Compilation, Long>, JpaRepository<Compilation, Long> {
    List<Compilation> findAllByPinned(boolean pinned, Pageable pageable);
}
