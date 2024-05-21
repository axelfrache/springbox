package io.github.axelfrache.savesync.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "files")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String path;
    private Date uploadDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
