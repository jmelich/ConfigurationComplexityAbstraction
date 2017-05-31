package cat.udl.eps.switchconfiguration.handler;

import cat.udl.eps.switchconfiguration.domain.DataUser;
import cat.udl.eps.switchconfiguration.domain.Dataset;
import cat.udl.eps.switchconfiguration.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;


@Component
@Transactional
@RepositoryEventHandler(Dataset.class)
public class DatasetEventHandler {
    private final Logger logger = LoggerFactory.getLogger(DatasetEventHandler.class);

    @HandleBeforeCreate
    @PreAuthorize("hasRole('OWNER')")
    public void handleDatasetPreCreate(Dataset dataset) {
        logger.info("Before creating: {}", dataset);

        dataset.setDateTime(ZonedDateTime.now());
        dataset.setLastModified(dataset.getDateTime());
        DataUser principal = (DataUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @HandleBeforeSave
    @PreAuthorize("#dataset.owner.username == principal.username")
    public void handleDatasetPreSave(Dataset dataset) {
        logger.info("Before updating: {}", dataset);

        dataset.setLastModified(ZonedDateTime.now());
    }

    @HandleBeforeDelete
    @PreAuthorize("#dataset.owner.username == principal.username")
    public void handleDatasetPreDelete(Dataset dataset) {
        logger.info("Before deleting: {}", dataset);
    }

    @HandleBeforeLinkSave
    public void handleDatasetPreLinkSave(Dataset dataset, Object o) {
        logger.info("Before linking: {} to {}", dataset, o);

        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (o instanceof User) {
            User linkedUser = (User) o;
            if (linkedUser.getUsername().equals(principal.getUsername()))
                return; // Transferring ownership
            else
                throw new AccessDeniedException("Just dataset owner can transfer ownership");
        }
    }

    @HandleAfterCreate
    public void handleDatasetPostCreate(Dataset dataset) {
        logger.info("After creating dataset: {}", dataset);
    }

    @HandleAfterSave
    public void handleDatasetPostSave(Dataset dataset) {
        logger.info("After updating: {}", dataset);
    }

    @HandleAfterDelete
    public void handleDatasetPostDelete(Dataset dataset) {
        logger.info("After deleting: {}", dataset);
    }

    @HandleAfterLinkSave
    public void handleDatasetPostLinkSave(Dataset dataset, Object o) {
        logger.info("After linking: {} to {}", dataset, o);
    }
}
