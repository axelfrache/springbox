package io.github.axelfrache.springbox.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DOCUMENT")
public class Document extends File{
}