package com.bdit.tracker.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "publication")
public class Publication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disease_id", nullable = false)
    private Disease disease;

    @Column(name = "pubmed_id", unique = true)
    private String pubmedId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(name = "abstract", columnDefinition = "TEXT")
    private String abstrakt;

    @Column(columnDefinition = "TEXT")
    private String authors;

    private String journal;

    @Column(name = "published_date")
    private LocalDate publishedDate;

    @Column(name = "source_url", columnDefinition = "TEXT")
    private String sourceUrl;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Disease getDisease() { return disease; }
    public void setDisease(Disease disease) { this.disease = disease; }
    public String getPubmedId() { return pubmedId; }
    public void setPubmedId(String pubmedId) { this.pubmedId = pubmedId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAbstrakt() { return abstrakt; }
    public void setAbstrakt(String abstrakt) { this.abstrakt = abstrakt; }
    public String getAuthors() { return authors; }
    public void setAuthors(String authors) { this.authors = authors; }
    public String getJournal() { return journal; }
    public void setJournal(String journal) { this.journal = journal; }
    public LocalDate getPublishedDate() { return publishedDate; }
    public void setPublishedDate(LocalDate publishedDate) { this.publishedDate = publishedDate; }
    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
}
