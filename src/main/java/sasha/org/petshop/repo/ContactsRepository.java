package sasha.org.petshop.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sasha.org.petshop.model.Contacts;

@Repository
public interface ContactsRepository extends JpaRepository<Contacts, Integer> {

    Object getContactsByCustomerId(Integer id);

}
