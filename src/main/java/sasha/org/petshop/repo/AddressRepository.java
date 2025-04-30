package sasha.org.petshop.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sasha.org.petshop.model.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

    Object getAddressesByCustomerId(Integer id);
}
