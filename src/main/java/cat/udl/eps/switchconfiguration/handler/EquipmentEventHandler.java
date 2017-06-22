package cat.udl.eps.switchconfiguration.handler;

import cat.udl.eps.switchconfiguration.domain.*;
import cat.udl.eps.switchconfiguration.repository.PortRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


@Component
@Transactional
@RepositoryEventHandler(Equipment.class)
public class EquipmentEventHandler {
    private final Logger logger = LoggerFactory.getLogger(EquipmentEventHandler.class);
    @Autowired
    private PortRepository portRepository;

    @HandleAfterCreate
    //@PreAuthorize("hasRole('OWNER')")
    public void handleEquipmentAfterSave(Card card) {
        logger.info("After creating: {}", card);

        List<Port> portsList = new ArrayList<>();
        for(int i=1; i<=card.getNumberOfPorts(); i++){
            Port p = new Port();
            p.setTitle(String.valueOf(i));
            p.setIsInCard(card);
            portRepository.save(p);
            logger.info("port saved");
        }
        logger.info("After created ports");
        card.setPorts(portsList);
    }


}
