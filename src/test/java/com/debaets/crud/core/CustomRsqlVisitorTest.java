package com.debaets.crud.core;

import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.debaets.crud.core.model.Operators;
import com.debaets.crud.core.service.CustomRsqlVisitor;
import com.debaets.crud.core.service.DictionaryService;
import com.debaets.crud.core.service.model.Address;
import com.debaets.crud.core.service.model.Gender;
import com.debaets.crud.core.service.model.User;
import com.debaets.crud.core.service.repository.UserRepository;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;

@RunWith(SpringRunner.class)
@DataJpaTest
@Transactional
@EnableJpaRepositories(basePackages = "com.debaets.crud.core.service")
@EntityScan(basePackages = {"com.debaets.crud.core.service"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CustomRsqlVisitorTest {

	@Autowired
	private UserRepository repository;

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
		userTom.setFirstName("tom jean");
		userTom.setLastName("doe");
		userTom.setEmail("tom@doe.com");
		userTom.setAge(26);
		userTom.setAddress(Address.builder().street("street2").build());
		userTom.setGender(Gender.FEMALE);
		userTom.setBirthday(formatter.parse(birthDay_Tom));
		repository.save(userTom);
	}

	//Test equality
	@Test
	public void givenFirstAndLastName_whenGettingListOfUsers_thenCorrect() {
		Node rootNode = new RSQLParser().parse("firstName==john;lastName==doe");
		Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<>(new DictionaryService() {},false));
		List<User> results = repository.findAll(spec);

		assertThat(userJohn, isIn(results));
		assertThat(userTom, not(isIn(results)));
	}

	//Test equality (test ignoreCase)
	@Test
	public void givenFirstAndLastNameIgnoreCase_whenGettingListOfUsers_thenCorrect() {
		Node rootNode = new RSQLParser().parse("firstName==JOHN;lastName==Doe");
		Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<>(new DictionaryService() {
		}, false));
		List<User> results = repository.findAll(spec);

		assertThat(userJohn, isIn(results));
		assertThat(userTom, not(isIn(results)));
	}

	//Test equality (test space)
	@Test
	public void giveLastNameWithSpace_whenGettingListOfUsers_thenCorrect() {
		Node rootNode = new RSQLParser().parse("firstName==\"tom Jean\"");
		Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<>(new DictionaryService() {
		}, false));
		List<User> results = repository.findAll(spec);

		assertThat(userJohn, not(isIn(results)));
		assertThat(userTom, isIn(results));
	}

	//Test equality (embedded object)
	@Test
	public void givenAddressStreet_whenGettingListOfUsers_thenCorrect() {
		Node rootNode = new RSQLParser().parse("address.street==street1");
		Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<>(new DictionaryService() {},false));
		List<User> results = repository.findAll(spec);

		assertThat(userJohn, isIn(results));
		assertThat(userTom, not(isIn(results)));
	}

	//Test equality (enum)
	@Test
	public void givenGender_whenGettingListOfUsers_thenCorrect() {
		Node rootNode = new RSQLParser().parse("gender==MALE");
		Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<>(new DictionaryService() {},false));
		List<User> results = repository.findAll(spec);

		assertThat(userJohn, isIn(results));
		assertThat(userTom, not(isIn(results)));
	}

	//Test equality (date)
	@Test
	public void givenBirthday_whenGettingListOfUsers_thenCorrect() {
		Node rootNode = new RSQLParser().parse("birthday==" + birthDay_John);
		Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<>(new DictionaryService() {},false));
		List<User> results = repository.findAll(spec);

		assertThat(userJohn, isIn(results));
		assertThat(userTom, not(isIn(results)));
	}

	//Test >= (date)
	@Test
	public void givenBirthdayGE_whenGettingListOfUsers_thenCorrect() {
		Node rootNode = new RSQLParser().parse("birthday>=01/01/1975");
		Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<>(new DictionaryService() {},false));
		List<User> results = repository.findAll(spec);

		assertThat(userTom, isIn(results));
		assertThat(userJohn, not(isIn(results)));

		rootNode = new RSQLParser().parse("birthday>=01/01/1970");
		spec = rootNode.accept(new CustomRsqlVisitor<>(new DictionaryService() {
		}, false));
		results = repository.findAll(spec);

		assertThat(userTom, isIn(results));
		assertThat(userJohn, isIn(results));

		rootNode = new RSQLParser().parse("birthday>=01/01/1981");
		spec = rootNode.accept(new CustomRsqlVisitor<>(new DictionaryService() {
		}, false));
		results = repository.findAll(spec);

		assertThat(userTom, not(isIn(results)));
		assertThat(userJohn, not(isIn(results)));
	}

	//Test > (date)
	@Test
	public void givenBirthdayG_whenGettingListOfUsers_thenCorrect() {
		Node rootNode = new RSQLParser().parse("birthday>" + birthDay_John);
		Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<>(new DictionaryService() {
		}, false));
		List<User> results = repository.findAll(spec);

		assertThat(userTom, isIn(results));
		assertThat(userJohn, not(isIn(results)));
	}

	//Test < (date)
	@Test
	public void givenBirthdayL_whenGettingListOfUsers_thenCorrect() {
		Node rootNode = new RSQLParser().parse("birthday<" + birthDay_Tom);
		Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<>(new DictionaryService() {
		}, false));
		List<User> results = repository.findAll(spec);

		assertThat(userJohn, isIn(results));
		assertThat(userTom, not(isIn(results)));
	}

	//Test <= (date)
	@Test
	public void givenBirthdayLE_whenGettingListOfUsers_thenCorrect() {
		Node rootNode = new RSQLParser().parse("birthday<=" + birthDay_Tom);
		Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<>(new DictionaryService() {
		}, false));
		List<User> results = repository.findAll(spec);

		assertThat(userJohn, isIn(results));
		assertThat(userTom, isIn(results));
	}

	//Test between
	@Test
	public void givenBirthdayRange_whenGettingListOfUsers_thenCorrect() {
		Node rootNode = new RSQLParser(Operators.getOperators()).parse("birthday=between=(01/01/1960,01/02/1975)");
		Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<>(new DictionaryService() {},false));
		List<User> results = repository.findAll(spec);

		assertThat(userJohn, isIn(results));
		assertThat(userTom, not(isIn(results)));
	}

	//Test negation
	@Test
	public void givenFirstNameInverse_whenGettingListOfUsers_thenCorrect() {
		Node rootNode = new RSQLParser().parse("firstName!=john");
		Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<>(new DictionaryService() {},false));
		List<User> results = repository.findAll(spec);

		assertThat(userTom, isIn(results));
		assertThat(userJohn, not(isIn(results)));
	}

	//Test greater than
	@Test
	public void givenMinAge_whenGettingListOfUsers_thenCorrect() {
		Node rootNode = new RSQLParser().parse("age>25");
		Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<>(new DictionaryService() {},false));
		List<User> results = repository.findAll(spec);

		assertThat(userTom, isIn(results));
		assertThat(userJohn, not(isIn(results)));
	}

	//Test like
	@Test
	public void givenFirstNamePrefix_whenGettingListOfUsers_thenCorrect() {
		Node rootNode = new RSQLParser().parse("firstName==jo*");
		Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<>(new DictionaryService() {},false));
		List<User> results = repository.findAll(spec);

		assertThat(userJohn, isIn(results));
		assertThat(userTom, not(isIn(results)));
	}

	//Test in
	@Test
	public void givenListOfFirstName_whenGettingListOfUsers_thenCorrect() {
		Node rootNode = new RSQLParser().parse("firstName=in=(john,jack)");
		Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<>(new DictionaryService() {},false));
		List<User> results = repository.findAll(spec);

		assertThat(userJohn, isIn(results));
		assertThat(userTom, not(isIn(results)));
	}
}