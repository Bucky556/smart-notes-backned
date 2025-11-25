package code.uz.smartnotesbackned.controller;


import code.uz.smartnotesbackned.dto.AppResponse;
import code.uz.smartnotesbackned.dto.note.FavoriteDTO;
import code.uz.smartnotesbackned.dto.note.NoteCreateDTO;
import code.uz.smartnotesbackned.dto.note.NoteDTO;
import code.uz.smartnotesbackned.dto.FilterDTO;
import code.uz.smartnotesbackned.service.NoteService;
import code.uz.smartnotesbackned.util.PageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/note/api/v1")
@RequiredArgsConstructor
@Tag(
        name = "Note Controller",
        description = "Provides APIs for creating, updating, deleting, and managing user notes, including favorites and filtering options."
)
@Slf4j
public class NoteController {
    private final NoteService noteService;

    @Operation(
            summary = "Create a new note",
            description = "Creates a new note with title, content and reminder-date. "
                    + "Returns the created note details along with a success message."
    )
    @PostMapping("/create")
    public ResponseEntity<AppResponse<NoteDTO>> create(@RequestBody @Valid NoteCreateDTO dto) {
        log.info("Note created successfully: title='{}'", dto.getTitle());
        NoteDTO createdNote = noteService.create(dto);
        return ResponseEntity.ok(new AppResponse<>(createdNote, "Note created successfully"));
    }

    @Operation(
            summary = "Get all notes",
            description = "Retrieves a paginated list of all notes belonging to the user."
    )
    @GetMapping("/list")
    public ResponseEntity<PageImpl<NoteDTO>> getAll(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "9") int size
    ) {
        log.debug("Fetching all notes: page={}, size={}", page, size);
        return ResponseEntity.ok(noteService.getAll(PageUtil.getPage(page), size));
    }

    @Operation(
            summary = "Get note by ID",
            description = "Returns note of user by ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<NoteDTO> getById(@PathVariable Integer id) {
        log.debug("Fetching note by ID: {}", id);
        return ResponseEntity.ok(noteService.getById(id));
    }

    @Operation(
            summary = "Update an existing note",
            description = "Updates the title, content, favorite, reminder-date by its ID"
    )
    @PutMapping("/update/{id}")
    public ResponseEntity<AppResponse<NoteDTO>> update(@PathVariable Integer id, @RequestBody @Valid NoteCreateDTO noteCreateDTO) {
        log.info("Note updated successfully: title='{}'", noteCreateDTO.getTitle());
        NoteDTO updatedNote = noteService.updateById(id, noteCreateDTO);
        return ResponseEntity.ok(new AppResponse<>(updatedNote, "Note updated successfully"));
    }

    @Operation(
            summary = "Delete a note",
            description = "Deletes a note by its ID by changing visible into false"
    )
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<AppResponse<String>> delete(@PathVariable Integer id) {
        log.info("Note deleted successfully: id={}", id);
        noteService.deleteById(id);
        return ResponseEntity.ok(new AppResponse<>("Note deleted successfully"));
    }

    @Operation(
            summary = "Get favorite notes",
            description = "Retrieves a paginated list of notes that the user has marked as favorites."
    )
    @GetMapping("/favourites")
    public ResponseEntity<PageImpl<NoteDTO>> getFavourites(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        log.debug("Fetching favorite notes: page={}, size={}", page, size);
        return ResponseEntity.ok(noteService.getFavourites(PageUtil.getPage(page), size));
    }

    @Operation(
            summary = "Mark or unmark note as favorite",
            description = "Toggles the 'favorite' status of a note. The user can mark a note as favorite or remove it from favorites."
    )
    @PutMapping("/update/favorite/{id}")
    public ResponseEntity<AppResponse<String>> changeFavorite(@PathVariable Integer id, @RequestBody FavoriteDTO dto) {
        log.info("Favorite status changed for note id={}", id);
        return ResponseEntity.ok(noteService.changeFavorite(id, dto.getFavorite()));
    }

    @Operation(
            summary = "Filter notes",
            description = "Filters notes based on query (title). "
                    + "Supports pagination for efficient data retrieval."
    )
    @PostMapping("/filter")
    public ResponseEntity<PageImpl<NoteDTO>> filter(@RequestBody FilterDTO dto,
                                                    @RequestParam(value = "page", defaultValue = "1") int page,
                                                    @RequestParam(value = "size", defaultValue = "9") int size) {
        log.info("Filtering notes by query='{}', page={}, size={}", dto.getQuery(), page, size);
        return ResponseEntity.ok(noteService.filter(dto, PageUtil.getPage(page), size));
    }
}
