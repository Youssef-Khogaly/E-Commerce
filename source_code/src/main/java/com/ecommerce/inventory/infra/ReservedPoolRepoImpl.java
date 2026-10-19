package com.ecommerce.inventory.infra;

import com.ecommerce.inventory.service.repo.ReservedPoolRepo;
import com.fayupable.snowflake.SnowflakeIdGenerator;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@AllArgsConstructor
@Repository
public class ReservedPoolRepoImpl implements ReservedPoolRepo , RowMapper<ReservedUnit> {
    private final JdbcTemplate template;
    private final SnowflakeIdGenerator idGenerator;
    @Override
    public List<ReservedUnit> selectByOperationIdForUpdate(final Long operationId) {
        final String query = """
                    select * from reserved_pool r where r.operationId = ? for update
                    """;
        return template.query(query,ps-> {
            ps.setLong(1,operationId);
        } ,this);
    }

    @Override
    public List<ReservedUnit> selectAllExpiredForUpdate(int limit) {
        final String query = """
                    select * from reserved_pool r where expireAt < now() order by r.id limit ?
                    """;
        return template.query(query,ps -> ps.setInt(1,limit),this);
    }

    @Override
    public void deleteExpired() {
        final String query = "delete from reserved_pool where expireAt <= now()";
        template.update(query);

    }

    @Override
    public void deleteAllByOperation(long operationId) {
        final String query = """
                    delete from reserved_pool  where operationId = ?
                    """;
        template.update(query, ps-> ps.setLong(1,operationId));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public long reserve(final Map<Long, Integer> productQuantityMap , final Instant expireAt) {
        if(productQuantityMap == null)
            throw new NullPointerException("productQuantityMap is null, cannot reserve stock!!");
        if(productQuantityMap.isEmpty())
            throw new IllegalArgumentException("cannot reserve stock for empty productQuantityMap");
        final String query = """
                 insert  into  reserved_pool(id,operationId,productId,quantity,expireAt) values (?,?,?,?,?)
                 """;
        final int size = productQuantityMap.size();
        final long[] ids = idGenerator.nextIds(size);
        final long operationId = idGenerator.nextId();
        final var entries = productQuantityMap.entrySet().stream().toList();
        final var timeStamp = Timestamp.from(expireAt);
        template.batchUpdate(query, new BatchPreparedStatementSetter() {
            @Override
            public void setValues( PreparedStatement ps, int i) throws SQLException {
                var entry = entries.get(i);
                ps.setLong(1,ids[i]);
                ps.setLong(2,operationId);
                ps.setLong(3,entry.getKey());
                ps.setInt(4,entry.getValue());
                ps.setTimestamp(5,timeStamp);
            }
            @Override
            public int getBatchSize() {
                return size;
            }
        });

        return operationId;
    }

    @Override
    public @Nullable ReservedUnit mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ReservedUnit.builder()
                .id(rs.getLong("id"))
                .operationId(rs.getLong("operationId"))
                .productId(rs.getLong("productId"))
                .quantity(rs.getInt("quantity"))
                .expireAt(rs.getTimestamp("expireAt").toInstant())
                .build();
    }
}
