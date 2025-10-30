package com.tihon.outbox.repository;

import com.tihon.outbox.model.OutboxEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OutboxEntryRepository extends JpaRepository<OutboxEntry, Long> {
    @Query(
            value = "SELECT * FROM outbox o WHERE o.status = 'PENDING' and " +
                    "o.time_to_send <= now() " +
                    "ORDER BY o.time_to_send " +
                    "LIMIT :limit FOR UPDATE SKIP LOCKED",
            nativeQuery = true
    )
    List<OutboxEntry> findAndSkipLockedMessages(@Param("limit") int limit);
}
