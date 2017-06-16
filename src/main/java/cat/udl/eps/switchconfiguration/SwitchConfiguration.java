package cat.udl.eps.switchconfiguration;

import cat.udl.eps.switchconfiguration.domain.*;
import cat.udl.eps.switchconfiguration.handler.EquipmentEventHandler;
import cat.udl.eps.switchconfiguration.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;

import javax.annotation.PostConstruct;
import java.util.*;

@SpringBootApplication
public class SwitchConfiguration {

	@Autowired private CampusRepository campusRepository;
	@Autowired private BuildingRepository buildingRepository;
	@Autowired private FloorRepository floorRepository;
	@Autowired private DealerRepository dealerRepository;
	@Autowired private EquipmentRepository equipmentRepository;
	@Autowired private ConnectorRepository connectorRepository;
    @Autowired private PortRepository portRepository;
    private final Logger logger = LoggerFactory.getLogger(SwitchConfiguration.class);



	public static void main(String[] args) {
		SpringApplication.run(SwitchConfiguration.class, args);
	}

	@PostConstruct
	public void starting(){

		Campus c = new Campus();
		c.setTitle("Cappont Campus");


		Building b = new Building();
		b.setTitle("EPS");
		b.setIsInCampus(c);


		Floor f = new Floor();
		f.setTitle("Planta 1");
		f.setIsInBuilding(b);


		Dealer d = new Dealer();
		d.setTitle("Repartidor 1");
		d.setIsInFloor(f);


		Equipment e = new Equipment();
		e.setTitle("Switch 1");
		e.setNumberOfPorts(25);
		e.setIP("192.168.1.1");
		e.setUsername("admin");
		e.setPassword("switch");
		e.setIsInDealer(d);


        Connector con = new Connector();

        con.setTitle("Conector 1");
        con.setIsInEquipment(e);
        con.setIsInFloor(f);

		d.setEquipments(Arrays.asList(e));
		f.setDealers(Arrays.asList(d));
		b.setFloors(Arrays.asList(f));
		c.setBuildings(Arrays.asList(b));



        campusRepository.save(c);
        buildingRepository.save(b);
        floorRepository.save(f);
        dealerRepository.save(d);

        equipmentRepository.save(e);
        List<Port> portsList = new ArrayList<>();
        for(int i=1; i<=e.getNumberOfPorts(); i++){
            Port p = new Port();
            p.setTitle(String.valueOf(i));
            p.setBelongsTo(e);
            portRepository.save(p);
            logger.info("port saved");
        }
        logger.info("After created ports");
        e.setPorts(portsList);

        equipmentRepository.save(e);
        con.setConnectedTo(portRepository.findAll().iterator().next());
        connectorRepository.save(con);

	}
}
