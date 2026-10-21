package ru.study.officeops.repository;
import ru.study.officeops.domain.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface AssetRepository extends JpaRepository<Asset, Long> { Optional<Asset> findByInventoryNumber(String inventoryNumber); }
