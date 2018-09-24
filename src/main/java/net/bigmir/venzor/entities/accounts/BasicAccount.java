package net.bigmir.venzor.entities.accounts;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.bigmir.venzor.entities.SimpleUser;
import net.bigmir.venzor.entities.cards.Card;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@MappedSuperclass
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class BasicAccount <T extends Card> {
    @Id
    @Column(unique = true)
    private long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private SimpleUser user;
    private String currency;
    @OneToMany(mappedBy = "account", cascade = CascadeType.MERGE)
    private List<T> cards;
    private double amount = 0;

    public BasicAccount(long id, SimpleUser user, String currency) {
        this.id = id;
        this.user = user;
        this.currency = currency;
        this.cards = new LinkedList<>();
    }

    public List<Long> getCardsId(){
        List<Long> listId=new ArrayList<>();
        for(Card card : this.cards){
            listId.add(card.getId());
        }
        return listId;
    }

    public void increaseAmount(double sum){
        this.amount += sum;
    }

    public void descreaseAmount(double sum){
        this.amount -= sum;
    }

    public void addCard(T card){}

}
