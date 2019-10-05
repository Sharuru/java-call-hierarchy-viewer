package me.sharuru.jchv.frontend.repository;

import me.sharuru.jchv.frontend.entity.TblMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetaDataRepository extends JpaRepository<TblMetaData, Long>, QueryByExampleExecutor<TblMetaData> {

    @Query(value = "SELECT t FROM TblMetaData t WHERE t.context LIKE %:path% ORDER BY t.id")
    List<TblMetaData> findCallerByPath(@Param("path") String path);

    @Query(value = "SELECT t FROM TblMetaData t WHERE t.method LIKE %:path ORDER BY t.id")
    List<TblMetaData> findSelfByPath(@Param("path") String path);
}
