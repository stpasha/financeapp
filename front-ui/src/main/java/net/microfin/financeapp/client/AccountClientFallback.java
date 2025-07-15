package net.microfin.financeapp.client;

import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class AccountClientFallback implements AccountClient {

    @Override
    public ResponseEntity<List<AccountDTO>> getAccountsByUser(Integer userId) {
        log.warn("Fallback: getAccountsByUser failed for userId={}", userId);
        return ResponseEntity.ok(List.of());
    }

    @Override
    public ResponseEntity<Void> disable(Integer id) {
        log.warn("Fallback: disable failed for id={}", id);
        return ResponseEntity.status(503).build();
    }





}
