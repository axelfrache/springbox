package io.github.axelfrache.springbox.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("OTHER")
public class Other extends File{
}