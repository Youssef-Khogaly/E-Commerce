package com.ecommerce.inventory.infra;

import com.ecommerce.inventory.service.repo.AvailablePoolRepo;
import com.fayupable.snowflake.SnowflakeIdGenerator;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@AllArgsConstructor
class AvailablePoolRepoImpl implements AvailablePoolRepo , RowMapper<AvailableUnit> {
    private final JdbcTemplate template;
    private final SnowflakeIdGenerator snowflakeIdGenerator;
    private String getSelectForUpdateQuerySkipLocked()
    {
        return  """
                select * from inventory_available_pool where productId = ?  order by productId asc , id asc limit ? for update skip locked
                """;
    }
    private String getSelectForShareQuery()
    {
        return  """
                select * from inventory_available_pool where productId in ( %s ) for share
                """;
    }
    private String getSelectForUpdateQuery()
    {
        return  """
                select * from inventory_available_pool where productId in ( %s ) for update
                """;
    }
    private String buildSelectForUpdateQuery(int numberOfIds)
    {
        return getSelectForUpdateQuery().formatted(String.join(",",Collections.nCopies(numberOfIds,"?")));
    }
    private String buildSelectForShareQuery(int numberOfIds)
    {
        return getSelectForShareQuery().formatted(String.join(",",Collections.nCopies(numberOfIds,"?")));
    }
    @Override
    public List<AvailableUnit> selectForUpdateSkipLocked(final long productId, final int quantity) {
        final String query = getSelectForUpdateQuerySkipLocked();


        return template.query(query,ps -> {
            ps.setLong(1,productId);
            ps.setInt(2,quantity);
        }, this);
    }
    private String buildSelectAllQuery(final int numberOfElements){
        final String query = "("+ getSelectForUpdateQuerySkipLocked() + ")";
        final String joining = " union all ";
        final StringBuilder builder = new StringBuilder(query.length() + joining.length() * numberOfElements);
        for(int i = 0 ; i < numberOfElements ; ++i)
        {
            if(i > 0)
                builder.append(joining);

            builder.append(query);
        }
        return builder.toString();
    }

    @Override
    public List<AvailableUnit> selectAllForUpdateSkipLocked(final Map<Long, Integer> productId_quantity) {
        if(productId_quantity == null || productId_quantity.isEmpty())
            throw new IllegalArgumentException("empty or null product id quantity for select all for update method!!");
        if(productId_quantity.size() == 1)
        {
            var entry = productId_quantity.entrySet().stream().findFirst().get();
            return selectForUpdateSkipLocked(entry.getKey(),entry.getValue());
        }
        final String query  = buildSelectAllQuery(productId_quantity.size());
        final var entrySet = productId_quantity.entrySet();

        return template.query(query,ps -> {
            int i = 1;
            for(var entry : entrySet){
                ps.setLong(i++,entry.getKey());
                ps.setInt(i++,entry.getValue());
            }
        } , this);

    }

    @Override
    public List<AvailableUnit> selectAllForShare(Collection<Long>productIds) {
        if(productIds == null || productIds.isEmpty())
            throw new IllegalArgumentException("empty or null product ids  for select all for share method!!");
        final String query = buildSelectForShareQuery(productIds.size());

        return template.query(query,ps -> {
            int index = 1;
            for(var id :productIds){
                ps.setLong(index,id);
            }
        },this);
    }

    @Override
    public List<AvailableUnit> selectAllForUpdate(Collection<Long> productIds) {
        if(productIds == null || productIds.isEmpty())
            throw new IllegalArgumentException("empty or null product ids  for select all for share method!!");

        final String query = buildSelectForUpdateQuery(productIds.size());
        return template.query(query,ps -> {
            int index = 1;
            for(var id :productIds){
                ps.setLong(index,id);
            }
        },this);
    }

    private String deleteAllQuery()
    {
        return """
                delete from inventory_available_pool where productId = ?  and id = ?;
                """;
    }
    @Override
    public void deleteAll(Collection<AvailableUnit> units) {
        if(units == null || units.isEmpty())
            throw  new IllegalArgumentException("empty id list cannot perform batch delete");
        final String query = deleteAllQuery();
        template.batchUpdate(query,units,units.size(),(ps,arg) -> {
            ps.setLong(1,arg.productId());
            ps.setLong(2,arg.id());
        });
    }


    @Override
    public void fill(long productId, int quantity) {
        final String query = getInsertQuery();
        List<Long> ids = Collections.nCopies((int)(productId * quantity) , productId);

        template.batchUpdate(query,ids,quantity, (ps,id) ->{
            ps.setLong(1,id);
            ps.setLong(2,snowflakeIdGenerator.nextId());
        });
    }

    private String getInsertQuery(){
        return """
           insert into inventory_available_pool(productId,id) values (?,?)
        """;
    }
    @Override
    public void fill(Map<Long, Integer> productId_quantity) {
        final String query = getInsertQuery();
        List<Long>ids = new ArrayList<>(productId_quantity.size() * 2);
        productId_quantity.forEach((id,q) -> {
            int it = q;
            while(it-- > 0){
                ids.add(id);

            }
        });
        template.batchUpdate(query,ids,500, (ps,id) ->{
            ps.setLong(1,id);
            ps.setLong(2,snowflakeIdGenerator.nextId());
        });
    }

    @Override
    public @Nullable AvailableUnit mapRow(ResultSet rs, int rowNum) throws SQLException {
        return AvailableUnit.builder()
                .id(rs.getLong("id"))
                .productId(rs.getLong("productId"))
                .build();
    }
}
