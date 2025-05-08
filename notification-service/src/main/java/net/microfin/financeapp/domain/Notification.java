package net.microfin.financeapp.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(schema = "notification_info", name = "notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Notification {
    @Id
    @Column(name = "notification_id", nullable = false)
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_id_seq_gen")
    @SequenceGenerator(name="notification_id_seq_gen", sequenceName = "notification_id_seq", schema = "notification_info")
    private Integer id;
    @Column(name = "operation_type", nullable = false)
    private Integer operationType;
    @Column
    private String notificationDescription;
    @Column
    private LocalDateTime createdAt;
}
