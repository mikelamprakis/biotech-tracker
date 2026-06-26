package com.bdit.tracker.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "trial")
public class Trial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disease_id", nullable = false)
    private Disease disease;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(name = "nct_id", nullable = false, unique = true)
    private String nctId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    private String phase;
    private String status;
    private String sponsor;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "source_url", columnDefinition = "TEXT")
    private String sourceUrl;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Disease getDisease() { return disease; }
    public void setDisease(Disease disease) { this.disease = disease; }
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    public String getNctId() { return nctId; }
    public void setNctId(String nctId) { this.nctId = nctId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getPhase() { return phase; }
    public void setPhase(String phase) { this.phase = phase; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSponsor() { return sponsor; }
    public void setSponsor(String sponsor) { this.sponsor = sponsor; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
}
