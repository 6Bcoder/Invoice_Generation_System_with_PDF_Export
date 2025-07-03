package com.invoice.Invoice_System.service;

import com.invoice.Invoice_System.exception.CustomerNotFoundException;
import com.invoice.Invoice_System.model.Customer;
import com.invoice.Invoice_System.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer createCustomer(Customer customer){
         return customerRepository.save(customer);
    }


    public Customer getCustomerByEmail(String email){
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with email: " + email));
    }

    public Customer updateCustomer(Long id,Customer updatedCustomer){

        Optional<Customer> existingCustomerOptional = customerRepository.findById(id);

        if(existingCustomerOptional.isPresent()){

            Customer existingCustomer=existingCustomerOptional.get();
            if(updatedCustomer.getName()!=null){
                existingCustomer.setName(updatedCustomer.getName());
            }
            if(updatedCustomer.getEmail()!=null){
                existingCustomer.setEmail(updatedCustomer.getEmail());
            }
            if(updatedCustomer.getPhoneNumber()!=null){
                existingCustomer.setPhoneNumber(updatedCustomer.getPhoneNumber());
            }
            return customerRepository.save(existingCustomer);
        }
        else{
            throw new CustomerNotFoundException("Customer not found with ID: " + id);
        }
    }

    public void deleteCustomer(Long id){
        Optional<Customer> existingCustomerOptional = customerRepository.findById(id);

        if(existingCustomerOptional.isPresent()){
            Customer existingCustomer=existingCustomerOptional.get();
            customerRepository.delete(existingCustomer);
        }
        else{
            throw new CustomerNotFoundException("Customer not found with ID: " + id);
        }
    }

    public List<Customer> getAllCustomers(){
        return customerRepository.findAll();
    }


    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + id));
    }
}
