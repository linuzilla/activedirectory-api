package ncu.cc.activedirectory.repositories;

import ncu.cc.activedirectory.entities.Apilog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApilogRepository extends JpaRepository<Apilog,Integer> {
}
