package vn.project.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.project.jobhunter.domain.Subscriber;
import java.util.List;


@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber,Long>,JpaSpecificationExecutor<Subscriber>{

    boolean existsByEmail(String email);
    Subscriber findByEmail(String email);
}
