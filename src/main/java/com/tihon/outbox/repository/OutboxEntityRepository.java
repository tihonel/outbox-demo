package com.tihon.outbox.repository;

import com.tihon.outbox.model.OutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface OutboxEntityRepository extends JpaRepository<OutboxEntity, UUID> {
    @Query(
            value = "SELECT * FROM outbox o WHERE o.status IN ('PENDING', 'RUNNING') and " +
                    "o.time_to_send <= now() " +
                    "ORDER BY o.time_to_send " +
                    "LIMIT :limit FOR UPDATE SKIP LOCKED",
            nativeQuery = true
    )
    List<OutboxEntity> findAndSkipLockedMessages(@Param("limit") int limit);
}
