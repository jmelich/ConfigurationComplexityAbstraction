package cat.udl.eps.switchconfiguration.handler;

import cat.udl.eps.switchconfiguration.domain.*;
import cat.udl.eps.switchconfiguration.repository.PortRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;


@Component
@Transactional
@RepositoryEventHandler(Equipment.class)
public class CardEventHandler {
    private final Logger logger = LoggerFactory.getLogger(CardEventHandler.class);
    @Autowired
    private PortRepository portRepository;

    @HandleAfterCreate
    public void handleCardAfterSave(Card card) {

        List<Port> portsList = new ArrayList<>();
        for(int i=1; i<=card.getNumberOfPorts(); i++){
            Port p = new Port();
            p.setTitle(String.valueOf(i));
            p.setIsInCard(card);
            portRepository.save(p);
        }
        card.setPorts(portsList);
    }


}
