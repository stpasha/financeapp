package net.microfin.financeapp.mapper;

import net.microfin.financeapp.domain.Notification;
import net.microfin.financeapp.dto.NotificationDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    Notification toEntity(NotificationDTO notificationDTO);
    NotificationDTO toDto(Notification notification);
}
