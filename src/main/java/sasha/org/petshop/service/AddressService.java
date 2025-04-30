package sasha.org.petshop.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sasha.org.petshop.dto.AddressDTO;
import sasha.org.petshop.model.Address;
import sasha.org.petshop.model.Customer;
import sasha.org.petshop.repo.AddressRepository;
import sasha.org.petshop.repo.CustomerRepository;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;

    public AddressService(AddressRepository addressRepository, CustomerRepository customerRepository) {
        this.addressRepository = addressRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public boolean updateAddress(AddressDTO addressDTO) {
        var addressVar = addressRepository.getAddressesByCustomerId(addressDTO.getCustomerId());

        Customer customer = customerRepository.findById(addressDTO.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        if(addressVar == null){
            Address addressNew = Address.of(addressDTO, customer);
            //addressRepository.save(addressNew);

            customer.setAddress(addressNew);     // link address to customer
            customerRepository.save(customer);
            return true;
        }else if (addressVar != null) {
            Address address = (Address) addressVar;
            address.setAddress(addressDTO.getAddress());
            address.setCity(addressDTO.getCity());
            address.setCountry(addressDTO.getCountry());
            address.setCustomer(customer);
            //addressRepository.save(address);

            customer.setAddress(address);       // link address to customer
            customerRepository.save(customer);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public AddressDTO getAddress(Integer customerId) {
        var customerVar =  customerRepository.getCustomerById(customerId);
        if(customerVar != null){
            Customer customer = (Customer) customerVar;
            if(customer.getAddress() != null){
                Address address = customer.getAddress();
                AddressDTO addressDTO  = address.toAddressDTO();
                return  addressDTO;
            }
        }
        return null;
    }
}
