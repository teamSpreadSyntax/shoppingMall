package home.project.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "category", uniqueConstraints = {@UniqueConstraint(columnNames = {"phone", "email"})})

@Getter
@Setter
public class Category {
}
