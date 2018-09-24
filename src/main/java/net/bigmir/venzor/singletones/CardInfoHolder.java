package net.bigmir.venzor.singletones;

import net.bigmir.venzor.dto.CardDTO;
import net.bigmir.venzor.entities.cards.Card;

import java.util.HashMap;
import java.util.Map;

public class CardInfoHolder {
    private static volatile CardInfoHolder instance = new CardInfoHolder();
    private volatile Map<Long, CardDTO> cards = new HashMap<>();

    private CardInfoHolder() {
    }

    public static CardInfoHolder getInstance() {
        return instance;
    }

    public synchronized <T extends Card> void put(long userId, T card) {
        this.cards.put(userId, new CardDTO(card));
    }

    public synchronized CardDTO getInfo(long userId) {
        CardDTO info = this.cards.get(userId);
        this.cards.remove(userId);
        return info;
    }
}
