package code.uz.smartnotesbackned.repository;

import code.uz.smartnotesbackned.entity.ProfileEntity;
import code.uz.smartnotesbackned.enums.GeneralStatus;
import code.uz.smartnotesbackned.mapper.ProfileMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ProfileRepository extends JpaRepository<ProfileEntity, UUID> {
    Optional<ProfileEntity> findByEmailAndVisibleTrue(String email);

    Optional<ProfileEntity> findByIdAndVisibleTrue(UUID id);

    @Modifying
    @Query("update ProfileEntity p set p.status = :status where p.id = :id")
    void updateStatusById(GeneralStatus status, UUID id);

    @Modifying
    @Query("update ProfileEntity p set p.password = :password where p.id = :id")
    void updatePasswordById(UUID id, String password);

    @Modifying
    @Query("update ProfileEntity p set p.name = :name where p.id = :id")
    void updateNameById(UUID id, String name);

    @Modifying
    @Query("update ProfileEntity p set p.updatedUsername = :username where p.id = :id")
    void updateUpd_username(UUID id, String username);

    boolean existsByEmailAndVisibleTrueAndStatus(String email, GeneralStatus status);

    @Modifying
    @Query("update ProfileEntity p set p.email = :upd_username where p.id = :id")
    void updateEmailById(UUID id, String upd_username);

    @Modifying
    @Query("update ProfileEntity p set p.photoId = :photo_id where p.id = :id")
    void updatePhotoById(UUID id, String photo_id);

    @Query("update ProfileEntity set visible = false where id = :profileId")
    @Modifying
    void changeVisibleById(UUID profileId);

    @Query(value = "select p.id as id, p.name as name, p.email as email, p.created_date as createdDate, p.status as status, p.photo_id as photoId," +
            " ( select string_agg(role, ',') from profile_role as pr where pr.profile_id = p.id ) as roles " +
            " from profile p " +
            " where p.visible = true and p.status <> 'IN_REGISTRATION' order by p.name asc",
            countQuery = "select count(*) " +
                    "from profile p " +
                    "where p.visible = true ", nativeQuery = true)
    Page<ProfileMapper> findAllProfile(Pageable pageable);

    @Query(value = "select p.id as id, p.name as name, p.email as email, p.created_date as createdDate, p.status as status, p.photo_id as photoId," +
            " (select string_agg(role, ',') from profile_role as pr where pr.profile_id = p.id) as roles " +
            " from profile p " +
            "where (lower(p.name) like ?1 or lower(p.email) like ?1) and p.visible = true and p.status <> 'IN_REGISTRATION' ",
            countQuery = "select count(*) " +
                    "from profile p " +
                    "where (lower(p.name) like ?1 or lower(p.email) like ?1) " +
                    "and p.visible = true",
            nativeQuery = true)
    Page<ProfileMapper> findProfileWithQuery(String query, Pageable pageable);
}
