package com.football_club.Scouting.model;

import com.football_club.Auth.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "search_templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "template_name", nullable = false)
    private String templateName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @OneToMany(mappedBy = "searchTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TemplatePart> templateParts;
}
