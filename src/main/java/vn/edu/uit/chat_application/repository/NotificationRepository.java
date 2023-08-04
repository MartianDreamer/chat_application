package vn.edu.uit.chat_application.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.uit.chat_application.entity.Notification;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByToId(UUID toId, Pageable pageable);
    List<Notification> findByToIdAndType(UUID toId, Notification.Type type);
    void deleteByEntityIdAndType(UUID entityId, Notification.Type type);
}
