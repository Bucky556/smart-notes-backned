package code.uz.smartnotesbackned.service;

import code.uz.smartnotesbackned.entity.EmailHistoryEntity;
import code.uz.smartnotesbackned.enums.EmailType;
import code.uz.smartnotesbackned.exception.BadException;
import code.uz.smartnotesbackned.repository.EmailHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailHistoryService {
    private final EmailHistoryRepository emailHistoryRepository;

    public void create(String email, String code, EmailType emailType) {
        EmailHistoryEntity historyEntity = new EmailHistoryEntity();
        historyEntity.setEmail(email);
        historyEntity.setCode(code);
        historyEntity.setEmailType(emailType);
        historyEntity.setSendTime(LocalDateTime.now());
        emailHistoryRepository.save(historyEntity);
    }

    public Long getEmailCountWhileSending(String email) {
        return emailHistoryRepository.countByEmailAndSendTimeBetween(email, LocalDateTime.now().minusMinutes(1), LocalDateTime.now());
    }


    public void checkCode(String toEmail, String code) {
        //get last code by email
        Optional<EmailHistoryEntity> emailHistoryEntity = emailHistoryRepository.findTop1ByEmailOrderBySendTimeDesc(toEmail);
        if (emailHistoryEntity.isEmpty()) {
            throw new BadException("Verification failed");
        }

        EmailHistoryEntity historyEntity = emailHistoryEntity.get();
        // check attempt count
        if (historyEntity.getAttemptCount() > 3) {
            throw new BadException("Attempt count exceeded");
        }
        // check code
        if (!historyEntity.getCode().equals(code)) {
            emailHistoryRepository.updateAttemptCountByEmail(historyEntity.getEmail());  // + 1 attempt count for every failing
            throw new BadException("Wrong verification code");
        }
        // check time
        LocalDateTime expDate = historyEntity.getSendTime().plusMinutes(2);
        if (LocalDateTime.now().isAfter(expDate)) {
            throw new BadException("Verification time expired");
        }
    }
}
