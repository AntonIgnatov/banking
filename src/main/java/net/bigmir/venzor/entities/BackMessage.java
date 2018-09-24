package net.bigmir.venzor.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "communications")
@Data
@NoArgsConstructor
public class BackMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String email;
    private String message;

    public BackMessage(String email, String message) {
        this.email = email;
        this.message = message;
    }
}
