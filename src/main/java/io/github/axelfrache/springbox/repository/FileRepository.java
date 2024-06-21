package io.github.axelfrache.springbox.repository;

import io.github.axelfrache.springbox.model.File;
import io.github.axelfrache.springbox.model.Folder;
import io.github.axelfrache.springbox.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByUser(User user);
    List<File> findByFolderAndUser(Folder folder, User user);
    List<File> findByUserAndFolderIsNull(User user);
    List<File> findByUserAndNameContaining(User user, String name);

}
