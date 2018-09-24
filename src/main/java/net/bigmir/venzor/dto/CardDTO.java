package net.bigmir.venzor.dto;

import lombok.Getter;
import lombok.Setter;
import net.bigmir.venzor.entities.cards.Card;

@Getter
@Setter
public class CardDTO {
    private long id;
    private int mounth;
    private int year;
    private int cvv;

    public<T extends Card> CardDTO(T card) {
        this.id = card.getId();
        this.mounth = card.getMounth();
        this.year = card.getYear();
        this.cvv = card.getCvv();
    }
}
