package com.GGT.gmailJobTracker.model;

package com.jobtracker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;

    private String jobTitle;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private String emailThreadId;

    private LocalDateTime appliedDate;

    private LocalDateTime lastUpdated;

    private String notes;

    // Additional fields can be added as needed
}