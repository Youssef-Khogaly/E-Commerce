package com.ecommerce.inventory.infra;

import com.ecommerce.inventory.service.repo.InventoryMetaDataRepo;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@AllArgsConstructor
@Repository
class InventoryMetaDataRepoImpl implements InventoryMetaDataRepo{
    private final JdbcTemplate template;
    private static final int METAQUERY_NUMBER_OF_ID_CLAUSES = 3;
    private static final int AVAILABLE_QUERY_NUMBER_OF_ID_CLAUSES = 2;
    private final  RowMapper<ProductAvailableStockView> ProductAvailableStockViewMapper = new RowMapper<>(){
        @Override
        public @Nullable ProductAvailableStockView mapRow(ResultSet rs, int rowNum) throws SQLException {
            return ProductAvailableStockView.builder()
                    .productId(rs.getLong("id"))
                    .totalQuantity(rs.getInt("totalQuantity"))
                    .availableStock(rs.getInt("available")).build();
        }
    };

    private final RowMapper<InventoryMetaDataView> MetaDataMapper = new RowMapper<InventoryMetaDataView>(){

        @Override
        public @Nullable InventoryMetaDataView mapRow(ResultSet rs, int rowNum) throws SQLException {
            return InventoryMetaDataView.builder()
                    .productId(rs.getLong("id"))
                    .totalStock(rs.getInt("totalQuantity"))
                    .reserved(rs.getInt("reserved"))
                    .pooled(rs.getInt("pooled"))
                    .build();
        }
    };

    private String metaDataViewQuery()
    {
        return """
            select id , totalQuantity,  IFNULL(res.reserved,0) as reserved ,ifnull(pool.pooled,0) as pooled
                                from inventory i
         left join (select r.productId , sum(r.quantity) as reserved from reserved_pool r where r.productId in(  %s ) and expireAt > now() group by r.productId) as res on i.id = res.productId
         left join (select p.productId , count(p.productId) as pooled from inventory_available_pool p  where p.productId in ( %s ) group by productId) as pool on pool.productId = i.id
                where  i.id in ( %s );
                """;
    }
//    private String metaDataViewQueryLocking()
//    {
//        return """
//        select s.product_id as productId , s.stock as totalQuantity,  IFNULL(res.reserved,0) as reserved ,ifnull(pool.pooled,0) as pooled
//                                from product_stock s
//         left join (select r.productId , sum(r.quantity,0) as reserved from reserved_pool r where r.productId in( %s ) and expireAt > now() for update group by r.productId) as res on s.product_id = res.productId
//         left join (select p.productId , count(p.productId) as pooled from inventory_available_pool p  where p.productId in ( %s ) for update  group by productId) as pool on pool.productId = s.product_id
//                where  s.product_id in ( %s ) for update ;
//                """;
//    }
    private String availableQuery()
    {
        return """
                select id  ,  totalQuantity,  totalQuantity -  IFNULL(res.reserved,0) as available
                                from inventory i
                    left join (select r.productId , sum(r.quantity) as reserved from reserved_pool r where r.productId in( %s ) and expireAt > now() group by r.productId) as res on i.id = res.productId
                 where  i.id in ( %s );
                """;
    }
    private String buildAvailableQuery(int numberOfIds)
    {
        final var placeHolders = String.join(",",Collections.nCopies(numberOfIds,"?"));
        final String[] placeHolderStrings = new String[METAQUERY_NUMBER_OF_ID_CLAUSES];
        Arrays.fill(placeHolderStrings,placeHolders);

        return availableQuery().formatted((Object[]) placeHolderStrings);
    }
    private String buildMetaDataViewQuery(int numberOfIds)
    {
        final var placeHolders = String.join(",",Collections.nCopies(numberOfIds,"?"));
        final String[] placeHolderStrings = new String[METAQUERY_NUMBER_OF_ID_CLAUSES];
        Arrays.fill(placeHolderStrings,placeHolders);

        return metaDataViewQuery().formatted((Object[]) placeHolderStrings);
    }
    @Override
    public Optional<InventoryMetaDataView> findByProductId(long id) {
        final String query = buildMetaDataViewQuery(1);
        try{
            return Optional.of(template.queryForObject(query,MetaDataMapper,id,id,id));
        }catch (EmptyResultDataAccessException e){
           return Optional.empty();
        }
    }
    @Override
    public List<InventoryMetaDataView> findAllByProductIds(final Set<Long> ids) {
        if(ids == null)
            throw new NullPointerException("null ids collection, cannot generate inventory metadata query!!");
        if(ids.isEmpty())
            throw new IllegalArgumentException("Empty collection, cannot generate inventory metadata query!!");

        final String query = buildMetaDataViewQuery(ids.size());
        return template.query(query,ps -> {
            int i = 0;
            int paraIndex = 1;
            for(; i < METAQUERY_NUMBER_OF_ID_CLAUSES; ++i)
            {
                for(var id : ids){
                    ps.setLong(paraIndex++,id);
                }
            }
        },MetaDataMapper);

    }

    @Override
    public List<ProductAvailableStockView> findAllByProductIdsAvailableView(final Set<Long> ids) {
        if(ids == null)
            throw new NullPointerException("null ids collection, cannot generate inventory available query!!");
        if(ids.isEmpty())
            throw new IllegalArgumentException("Empty collection, cannot generate inventory available query!!");
        final String query = buildAvailableQuery(ids.size());

        return template.query(query,ps -> {
            int i = 0;
            int paraIndex = 1;
            for(; i < AVAILABLE_QUERY_NUMBER_OF_ID_CLAUSES; ++i)
            {
                for(var id : ids){
                    ps.setLong(paraIndex++,id);
                }
            }
        },ProductAvailableStockViewMapper);
    }

}
