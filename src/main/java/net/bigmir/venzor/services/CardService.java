package net.bigmir.venzor.services;

import net.bigmir.venzor.entities.cards.Card;
import net.bigmir.venzor.exeptions.WrongAccountExeption;
import net.bigmir.venzor.repositories.CreditCardRepository;
import net.bigmir.venzor.repositories.DebitCardRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class CardService {
    private CreditCardRepository creditCardRepository;
    private DebitCardRepository debitCardRepository;

    public CardService(CreditCardRepository creditCardRepository,
                       DebitCardRepository debitCardRepository) {
        this.creditCardRepository = creditCardRepository;
        this.debitCardRepository = debitCardRepository;
    }


    public <T extends Card> T getExistingCard(long id, int mounth, int year, int cvv) throws WrongAccountExeption {
        T card = getCard(id);
        if (card.getMounth() != mounth ||
                card.getYear() != year ||
                card.getCvv() != cvv) {
            throw new WrongAccountExeption();
        }
        return card;
    }

    public <K extends Card> K getCard(long id) throws WrongAccountExeption {
        if (debitCardRepository.existsById(id)) {
            return (K) debitCardRepository.getOne(id);
        } else if (creditCardRepository.existsById(id)) {
            return (K) creditCardRepository.getOne(id);
        } else {
            throw new WrongAccountExeption();
        }
    }

    public List<Long> getAllCardsId() {
        List<Long> resalt = new LinkedList<>();
        resalt.addAll(debitCardRepository.getAllid());
        resalt.addAll(creditCardRepository.getAllid());
        return resalt;
    }

    public boolean checkCardExisting(long id) {
        return debitCardRepository.existsById(id) || creditCardRepository.existsById(id);
    }

}
