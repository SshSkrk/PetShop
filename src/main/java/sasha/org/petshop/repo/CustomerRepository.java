package sasha.org.petshop.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sasha.org.petshop.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Object getCustomerById(Integer id);

    Object getCustomersByUsername(String username);
}
