package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.ViewStats;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    //  уникальные IP для всех URI
    @Query("""
            SELECT new ru.practicum.dto.ViewStats(e.app, e.uri, COUNT(DISTINCT e.ip))
            FROM EndpointHit e
            WHERE e.timestamp BETWEEN :start AND :end
            GROUP BY e.app, e.uri
            ORDER BY COUNT(DISTINCT e.ip) DESC
            """)
    List<ViewStats> findAllByTimestampBetweenStartAndEndWithUniqueIp(@Param("start") LocalDateTime start,
                                                                     @Param("end") LocalDateTime end);

    //  все посещения для всех URI.
    @Query("""
            SELECT new ru.practicum.dto.ViewStats(e.app, e.uri, COUNT(e.ip))
            FROM EndpointHit e
            WHERE e.timestamp BETWEEN :start AND :end
            GROUP BY e.app, e.uri
            ORDER BY COUNT(e.ip) DESC
            """)
    List<ViewStats> findAllByTimestampBetweenStartAndEndWhereIpNotUnique(@Param("start") LocalDateTime start,
                                                                         @Param("end") LocalDateTime end);

    //  уникальные IP для указанных URI
    @Query("""
            SELECT new ru.practicum.dto.ViewStats(e.app, e.uri, COUNT(DISTINCT e.ip))
            FROM EndpointHit e
            WHERE e.timestamp BETWEEN :start AND :end AND e.uri IN :uris
            GROUP BY e.app, e.uri
            ORDER BY COUNT(DISTINCT e.ip) DESC
            """)
    List<ViewStats> findAllByTimestampBetweenStartAndEndAndUriUniqueIp(@Param("start") LocalDateTime start,
                                                                       @Param("end") LocalDateTime end,
                                                                       @Param("uris") List<String> uris);

    //  все посещения для указанных URI
    @Query("""
            SELECT new ru.practicum.dto.ViewStats(e.app, e.uri, COUNT(e.ip))
            FROM EndpointHit e
            WHERE e.timestamp BETWEEN :start AND :end AND e.uri IN :uris
            GROUP BY e.app, e.uri
            ORDER BY COUNT(e.ip) DESC
            """)
    List<ViewStats> findAllByTimestampBetweenStartAndEndAndUriWhereIpNotUnique(@Param("start") LocalDateTime start,
                                                                               @Param("end") LocalDateTime end,
                                                                               @Param("uris") List<String> uris);
}