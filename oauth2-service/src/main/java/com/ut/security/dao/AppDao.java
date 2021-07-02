package com.ut.security.dao;

import com.ut.security.model.AppEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface AppDao extends JpaRepository<AppEntity, String> {
    AppEntity findByAppKeyAndDeveloperAndStatus(String appKey, String developer, boolean status);

    AppEntity findByAppKeyAndDeveloper(String appKey, String developer);

    Page<AppEntity> findByDeveloper(String developer, Pageable pageable);

    Page<AppEntity> findByDeveloperIn(Collection<String> developers, Pageable pageable);

    List<AppEntity> findAllByDeveloperAndStatus(String developer, boolean status);

    AppEntity findByAppKeyAndStatus(String appKey, boolean status);

    AppEntity findByAppKey(String appKey);

    List<AppEntity> findByName(String appName);

    List<AppEntity> findAllByStatus(boolean status);

}
