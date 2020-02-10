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

    @Query(value = "SELECT c FROM TblMetaContent c WHERE c.methodCalleeQualifiedName = :methodCalleeQualifiedName AND c.methodType <> 'BASE' AND c.methodType <> 'ITFS' ORDER BY c.id ASC")
    List<TblMetaContent> findCallerByMethodCalleeQualifiedName(@Param("methodCalleeQualifiedName") String methodCalleeQualifiedName);

    @Query(value = "SELECT c FROM TblMetaContent c WHERE (c.methodCalleeClass = :methodCalleeClass OR :methodCalleeClass = '') AND (c.methodCalleeMethod = :methodCalleeMethod OR :methodCalleeMethod = '') AND (c.methodType = 'BASE' OR c.methodType = 'ITFS') ORDER BY c.id ASC")
    List<TblMetaContent> findQualifiedBySimpleName(@Param("methodCalleeClass") String methodCalleeClass, @Param("methodCalleeMethod") String methodCalleeMethod);
}
