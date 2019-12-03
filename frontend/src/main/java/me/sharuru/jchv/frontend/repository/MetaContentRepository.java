package me.sharuru.jchv.frontend.repository;

import me.sharuru.jchv.frontend.entity.TblMetaContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Business working table repository
 */
@Repository
public interface MetaContentRepository extends JpaRepository<TblMetaContent, Long>, QueryByExampleExecutor<TblMetaContent> {

    @Query(value = "SELECT c FROM TblMetaContent c WHERE c.methodQualifiedName = :methodQualifiedName ORDER BY c.methodCalleeSeq ASC")
    List<TblMetaContent> findCalleeByMethodQualifiedName(@Param("methodQualifiedName") String methodQualifiedName);

    @Query(value = "SELECT c FROM TblMetaContent c WHERE c.methodCalleeQualifiedName = :methodCalleeQualifiedName AND c.methodType <> 'BASE' ORDER BY c.id ASC")
    List<TblMetaContent> findCallerByMethodCalleeQualifiedName(@Param("methodCalleeQualifiedName") String methodCalleeQualifiedName);
}
