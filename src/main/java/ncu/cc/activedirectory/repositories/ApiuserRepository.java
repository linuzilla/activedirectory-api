package ncu.cc.activedirectory.repositories;

import ncu.cc.activedirectory.entities.Apiuser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiuserRepository extends JpaRepository<Apiuser,String> {
}
