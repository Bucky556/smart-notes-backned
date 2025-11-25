package code.uz.smartnotesbackned.controller;


import code.uz.smartnotesbackned.dto.AttachDTO;
import code.uz.smartnotesbackned.service.AttachService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/attach")
@RequiredArgsConstructor
@Tag(
        name = "Attachment Controller",
        description = "Provides APIs for uploading and opening files"
)
public class AttachController {
    private final AttachService attachService;

    @Operation(
            summary = "Upload a file",
            description = "Uploads a file (e.g., image, document) to the server and returns unique ID and download path."
    )
    @PostMapping("/upload")
    public ResponseEntity<AttachDTO> upload(@RequestParam("file") MultipartFile file){
        return ResponseEntity.ok(attachService.upload(file));
    }


    @Operation(
            summary = "Open a file",
            description = "Opens and returns the file resource by its unique ID. The file can be viewed or downloaded by clients."
    )
    @GetMapping("/open/{id}")
    public ResponseEntity<Resource> open(@PathVariable String id) {
        return attachService.open(id);
    }
}
