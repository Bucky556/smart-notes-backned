package code.uz.smartnotesbackned.service;

import code.uz.smartnotesbackned.dto.AppResponse;
import code.uz.smartnotesbackned.dto.FilterResultDTO;
import code.uz.smartnotesbackned.dto.note.NoteCreateDTO;
import code.uz.smartnotesbackned.dto.note.NoteDTO;
import code.uz.smartnotesbackned.dto.FilterDTO;
import code.uz.smartnotesbackned.entity.NoteEntity;
import code.uz.smartnotesbackned.entity.ProfileEntity;
import code.uz.smartnotesbackned.exception.BadException;
import code.uz.smartnotesbackned.repository.FilterRepository;
import code.uz.smartnotesbackned.repository.NoteRepository;
import code.uz.smartnotesbackned.repository.ProfileRepository;
import code.uz.smartnotesbackned.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteRepository noteRepository;
    private final ProfileRepository profileRepository;
    private final FilterRepository filterRepository;

    public NoteDTO create(NoteCreateDTO dto) {
        UUID profileId = SecurityUtil.getID();
        Optional<ProfileEntity> visibleProfile = profileRepository.findByIdAndVisibleTrue(profileId);
        if (visibleProfile.isEmpty()) {
            throw new BadException("Profile not found");
        }

        NoteEntity noteEntity = new NoteEntity();
        noteEntity.setTitle(dto.getTitle());
        noteEntity.setContent(dto.getContent());
        noteEntity.setCreatedDate(LocalDateTime.now());
        if (dto.getReminderDate() != null) {  // may not be given
            noteEntity.setReminderDate(dto.getReminderDate());
        }
        if (dto.getFavorite() != null) {      // may not be given
            noteEntity.setFavorite(dto.getFavorite());
        }
        noteEntity.setProfileId(visibleProfile.get().getId());
        noteRepository.save(noteEntity);

        return toDTO(noteEntity);
    }

    public PageImpl<NoteDTO> getAll(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        UUID profileId = SecurityUtil.getID();
        Page<NoteEntity> visibleNotes = noteRepository.findAllByProfileIdAndVisibleTrueOrderByCreatedDateDesc(profileId, pageRequest);
        if (visibleNotes.isEmpty()) {
            return new PageImpl<>(Collections.emptyList()); // returns empty list
        }

        List<NoteDTO> noteDTOList = visibleNotes.stream()
                .map(this::toDTO)
                .toList();

        long totalElements = visibleNotes.getTotalElements();

        return new PageImpl<>(noteDTOList, pageRequest, totalElements);
    }

    public NoteDTO getById(Integer id) {
        Optional<NoteEntity> byId = noteRepository.findByIdAndVisibleTrue(id);
        if (byId.isEmpty()) {
            throw new BadException("Note not found");
        }
        return toAllDTO(byId.get());
    }

    public NoteDTO updateById(Integer id, NoteCreateDTO noteCreateDTO) {
        Optional<NoteEntity> byId = noteRepository.findById(id);
        if (byId.isEmpty()) {
            throw new BadException("Note not found");
        }
        NoteEntity noteEntity = byId.get();
        if (noteEntity.getVisible().equals(false)) {
            throw new BadException("This note cannot be updated");
        }
        UUID profileId = SecurityUtil.getID();
        if (!noteEntity.getProfileId().equals(profileId)) {
            throw new BadException("You do not have permission to update this note");
        }
        noteEntity.setTitle(noteCreateDTO.getTitle());
        noteEntity.setContent(noteCreateDTO.getContent());
        if (noteCreateDTO.getFavorite() != null) {     // buni bermasligi ha mumkin
            noteEntity.setFavorite(noteCreateDTO.getFavorite());
        }
        if (noteCreateDTO.getReminderDate() != null) { // buni bermasligi ha mumkin
            noteEntity.setReminderDate(noteCreateDTO.getReminderDate());
        }
        noteRepository.save(noteEntity);

        return toAllDTO(noteEntity);
    }

    public void deleteById(Integer id) {
        Optional<NoteEntity> byId = noteRepository.findById(id);
        if (byId.isEmpty()) {
            throw new BadException("Note not found");
        }
        UUID profileId = SecurityUtil.getID();
        if (!byId.get().getProfileId().equals(profileId)) {
            throw new BadException("You do not have permission to delete this note");
        }
        noteRepository.changeVisibleById(id);
    }

    public PageImpl<NoteDTO> getFavourites(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        UUID profileId = SecurityUtil.getID();
        Page<NoteEntity> favoriteNotes = noteRepository.findAllByProfileIdAndVisibleTrueAndFavoriteTrueOrderByCreatedDateDesc(profileId, pageRequest);
        if (favoriteNotes.isEmpty()) {
            return new PageImpl<>(Collections.emptyList());
        }
        List<NoteDTO> noteDTOList = favoriteNotes.stream()
                .map(this::toDTO)
                .toList();

        long totalElements = favoriteNotes.getTotalElements();

        return new PageImpl<>(noteDTOList, pageRequest, totalElements);
    }

    public AppResponse<String> changeFavorite(Integer id, Boolean dto) {
        noteRepository.changeFavoriteById(id, dto);
        String message;
        if (Boolean.TRUE.equals(dto)) {
            message = "Note has been added to Favorites.";
        } else {
            message = "Note has been removed from Favorites.";
        }

        return new AppResponse<>(message);
    }

    public PageImpl<NoteDTO> filter(FilterDTO dto, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        UUID profileId = SecurityUtil.getID();

        FilterResultDTO<NoteEntity> filtered = filterRepository.filter(dto, profileId, page, size);
        List<NoteDTO> noteDTOList = filtered.getList()
                .stream()
                .map(this::toDTO)
                .toList();

        long totalElements = filtered.getTotalCount();

        return new PageImpl<>(noteDTOList, pageRequest, totalElements);
    }

    private NoteDTO toDTO(NoteEntity noteEntity) {
        NoteDTO dto = new NoteDTO();
        dto.setId(noteEntity.getId());
        dto.setTitle(noteEntity.getTitle());
        // content isn't given
        dto.setReminderDate(noteEntity.getReminderDate());
        dto.setCreatedDate(noteEntity.getCreatedDate());
        dto.setFavorite(noteEntity.getFavorite());
        return dto;
    }

    private NoteDTO toAllDTO(NoteEntity noteEntity) {
        NoteDTO dto = new NoteDTO();
        dto.setId(noteEntity.getId());
        dto.setTitle(noteEntity.getTitle());
        dto.setContent(noteEntity.getContent());
        dto.setReminderDate(noteEntity.getReminderDate());
        dto.setCreatedDate(noteEntity.getCreatedDate());
        dto.setFavorite(noteEntity.getFavorite());
        return dto;
    }
}
