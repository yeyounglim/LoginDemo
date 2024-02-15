package demo.logindemo.repository;

import demo.logindemo.model.Users;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<Users,Long> {// CrudRepository - jpa crud연산코드 자동생성 인터페이스.<user테이블, ID>
    List<Users> findByEmail(String email); // 이메일 넣으면 조회
    List<Users> findById(int id);
}
