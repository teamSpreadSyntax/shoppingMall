package home.project.repository;

import home.project.domain.ShippingMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingMessageRepository extends JpaRepository<ShippingMessage, Long> {

}
