package com.debaets.crud.core.service;

import com.debaets.crud.core.model.exception.EntityAlreadyExistsException;
import com.debaets.crud.core.model.exception.ResourceNotFoundException;
import com.debaets.crud.core.service.model.Address;
import com.debaets.crud.core.service.model.Gender;
import com.debaets.crud.core.service.model.User;
import com.debaets.crud.core.service.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@SuppressWarnings("Duplicates")
@RunWith(SpringRunner.class)
@DataJpaTest
@Transactional
@EnableJpaRepositories(basePackages = "com.debaets.crud.core.service")
@EntityScan(basePackages = { "com.debaets.crud.core.service" })
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ComponentScan("com.debaets.crud.core.service")
public class CrudServiceImplTest {
	@Autowired
	@Qualifier("testUserRepo")
	private UserRepository repository;

	@Autowired
	private UserService userService;

	@MockBean
	private ConversionService conversionService;

	private User userJohn;

	private User userTom;

	private final String birthDay_John = "01/01/1970";
	private final String birthDay_Tom = "01/01/1980";

	@Before
	public void init() throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

		userJohn = new User();
		userJohn.setFirstName("john");
		userJohn.setLastName("doe");
		userJohn.setEmail("john@doe.com");
		userJohn.setAge(22);
		userJohn.setAddress(Address.builder().street("street1").build());
		userJohn.setGender(Gender.MALE);
		userJohn.setBirthday(formatter.parse(birthDay_John));
		repository.save(userJohn);

		userTom = new User();
		userTom.setFirstName("tom");
		userTom.setLastName("doe");
		userTom.setEmail("tom@doe.com");
		userTom.setAge(26);
		userTom.setAddress(Address.builder().street("street2").build());
		userTom.setGender(Gender.FEMALE);
		userTom.setBirthday(formatter.parse(birthDay_Tom));
		repository.save(userTom);

		when(conversionService.convert(any(User.class), eq(User.class)))
				.thenAnswer((Answer<User>) invocation -> {
					Object[] args = invocation.getArguments();
					return (User) args[0];
				});
		when(conversionService.convert(anyListOf(User.class),
				eq(TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(User.class))),
				eq(TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(User.class)))
		))
				.thenAnswer((Answer<List<User>>) invocation -> {
					Object[] args = invocation.getArguments();
					return (List<User>) args[0];
				});
		when(conversionService.canConvert(User.class, User.class)).thenReturn(true);
	}

	@After
	public void tearDown() throws Exception {
		repository.deleteAll();
	}

	@Test
	public void findOne() {
		User response = userService.findOne(userJohn.getId());
		assertEquals(userJohn, response);
	}

	@Test
	public void findByIds() {
		List<User> byIds = userService.findByIds(Arrays.asList(userJohn.getId(), userTom.getId()));
		assertEquals(2, byIds.size());
		assertTrue(byIds.contains(userJohn));
		assertTrue(byIds.contains(userTom));
	}

	@Test(expected = EntityAlreadyExistsException.class)
	public void create() {
		//Given
		User user = new User();
		user.setFirstName("toto");

		//When
		User response = userService.create(user);

		//Then
		assertEquals("toto", user.getFirstName());
		assertNotNull(user.getId());

		userService.create(response);
	}

	@Test
	public void update() {
		//Given
		userTom.setFirstName("toto");

		//When
		User response = userService.update(userTom.getId(), userTom);

		//Then
		assertEquals("toto", response.getFirstName());
		assertEquals(userTom.getLastName(), response.getLastName());
	}

	@Test(expected = ResourceNotFoundException.class)
	public void delete() {
		Long id = userJohn.getId();
		userService.delete(id);
		userService.findOne(id);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void deleteById() {
		Long id = userJohn.getId();
		userService.deleteById(id);
		userService.findOne(id);
	}

	@Test
	public void search() {
		List<User> response = userService.search("firstName==" + userTom.getFirstName());
		assertEquals(1, response.size());
		assertEquals(userTom, response.get(0));
	}

	@Test
	public void search1() {
		Page<User> response = userService.search("firstName==" + userTom.getFirstName(), new PageRequest(0, 10));
		assertEquals(1, response.getTotalElements());
		assertEquals(userTom, response.getContent().get(0));
	}
}
