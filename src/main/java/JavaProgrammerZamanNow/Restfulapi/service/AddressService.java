package JavaProgrammerZamanNow.Restfulapi.service;

import JavaProgrammerZamanNow.Restfulapi.entity.Address;
import JavaProgrammerZamanNow.Restfulapi.entity.Contact;
import JavaProgrammerZamanNow.Restfulapi.entity.User;
import JavaProgrammerZamanNow.Restfulapi.model.AddressResponse;
import JavaProgrammerZamanNow.Restfulapi.model.CreateAddressRequest;
import JavaProgrammerZamanNow.Restfulapi.model.UpdateAddressRequest;
import JavaProgrammerZamanNow.Restfulapi.repository.AddressRepository;
import JavaProgrammerZamanNow.Restfulapi.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class AddressService {
	@Autowired
	private ContactRepository contactRepository;

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private ValidationService validationService;


	public AddressResponse create(User user, CreateAddressRequest request) {
		validationService.validate(request);


		Contact contact = contactRepository.findFirstByUserAndId(user, request.getContactId())
				.orElseThrow(() ->
						new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

		Address address = new Address();
		address.setId(UUID.randomUUID().toString());
		address.setContact(contact);
		address.setStreet(request.getStreet());
		address.setCity(request.getCity());
		address.setProvince(request.getProvince());
		address.setPostalCode(request.getPostalCode());
		address.setCountry(request.getCountry());

		addressRepository.save(address);

		return toAddressResponse(address);

	}

	private AddressResponse toAddressResponse(Address address) {
		return AddressResponse.builder()
				.id(address.getId())
				.city(address.getCity())
				.country(address.getCountry())
				.province(address.getProvince())
				.street(address.getStreet())
				.postalCode(address.getPostalCode())
				.build();
	}

	@Transactional(readOnly = true)
	public AddressResponse get(User user, String contactId, String addressId) {
		Contact contact = contactRepository.findFirstByUserAndId(user, contactId).orElseThrow(() ->
				new ResponseStatusException(HttpStatus.NOT_FOUND, "contact not found"));
		Address address = addressRepository.findFirstByContactAndId(contact, addressId).orElseThrow(() ->
				new ResponseStatusException(HttpStatus.NOT_FOUND, "address not found"));

		return toAddressResponse(address);

	}

	@Transactional
	public AddressResponse update(User user, UpdateAddressRequest request) {
		validationService.validate(request);

		Contact contact = contactRepository.findFirstByUserAndId(user, request.getContactId()).orElseThrow(() ->
				new ResponseStatusException(HttpStatus.NOT_FOUND, "contact not found"));
		Address address = addressRepository.findFirstByContactAndId(contact, request.getAddressId()).orElseThrow(() ->
				new ResponseStatusException(HttpStatus.NOT_FOUND, "address not found"));

		address.setStreet(request.getStreet());
		address.setCity(request.getCity());
		address.setProvince(request.getProvince());
		address.setPostalCode(request.getPostalCode());
		address.setCountry(request.getCountry());

		addressRepository.save(address);

		return toAddressResponse(address);
	}

	@Transactional
	public void remove(User user, String contactId, String addressId) {
		Contact contact = contactRepository.findFirstByUserAndId(user, contactId).orElseThrow(() ->
				new ResponseStatusException(HttpStatus.NOT_FOUND, "contact not found"));
		Address address = addressRepository.findFirstByContactAndId(contact, addressId).orElseThrow(() ->
				new ResponseStatusException(HttpStatus.NOT_FOUND, "address not found"));
		addressRepository.delete(address);
	}

	@Transactional(readOnly = true)
	public List<AddressResponse> list(User user, String contactId) {
		Contact contact = contactRepository.findFirstByUserAndId(user, contactId).orElseThrow(() ->
				new ResponseStatusException(HttpStatus.NOT_FOUND, "contact not found"));

		List<Address> addresses = addressRepository.findAllByContact(contact);
		return addresses.stream().map(this::toAddressResponse).toList();
	}
}
