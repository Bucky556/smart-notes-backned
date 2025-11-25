package code.uz.smartnotesbackned.repository;

import code.uz.smartnotesbackned.dto.FilterResultDTO;
import code.uz.smartnotesbackned.dto.FilterDTO;
import code.uz.smartnotesbackned.entity.NoteEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FilterRepository {
    private final EntityManager entityManager;

    public FilterResultDTO<NoteEntity> filter(FilterDTO dto, UUID profileId, int page, int size) {
        StringBuilder visibleQuery = new StringBuilder(" where n.visible = true and n.profile.id = :profileId ");
        Map<String, Object> params = new HashMap<>();
        params.put("profileId", profileId);

        if (dto.getQuery() != null) {
            visibleQuery.append(" and lower(n.title) like :query ");  // search by title
            params.put("query", "%" + dto.getQuery().toLowerCase() + "%");
        }

        Query selectQuery = entityManager.createQuery("select n from NoteEntity n " + visibleQuery + " order by n.createdDate desc");
        selectQuery.setFirstResult((page) * size);
        selectQuery.setMaxResults(size);
        params.forEach(selectQuery::setParameter);

        List<NoteEntity> resultList = selectQuery.getResultList();

        Query countQuery = entityManager.createQuery("select count (n) from NoteEntity n " + visibleQuery);
        params.forEach(countQuery::setParameter);
        Long totalElements = (Long) countQuery.getSingleResult();

        return new FilterResultDTO<>(resultList, totalElements);
    }
}
