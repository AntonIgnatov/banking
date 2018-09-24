package net.bigmir.venzor.dto;

import lombok.Data;
import net.bigmir.venzor.entities.SimpleUser;

@Data
public class SimpleUserDTO {
    private String name;
    private String surname;
    private String phone;
    private String email;
    private String addres;
    private String password;




    public SimpleUserDTO(SimpleUser user) {
        this.name=user.getName();
        this.surname=user.getSurname();
        this.phone=user.getPhone();
        this.password=user.getPassword();
        this.addres= user.getAddres();
        this.email=user.getEmail();
    }
}
