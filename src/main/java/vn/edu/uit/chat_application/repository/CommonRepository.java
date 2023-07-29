package vn.edu.uit.chat_application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import vn.edu.uit.chat_application.entity.UuidIdEntity;

import java.util.List;
import java.util.UUID;

@NoRepositoryBean
public interface CommonRepository<T extends UuidIdEntity> extends JpaRepository<T, UUID> {
    List<T> findByIdIn(List<UUID> ids);
}
