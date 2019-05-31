package com.debaets.crud.core;

import com.debaets.crud.core.model.Operators;
import com.debaets.crud.core.service.CustomRsqlVisitor;
import com.debaets.crud.core.service.DictionaryService;
import com.debaets.crud.core.service.model.Address;
import com.debaets.crud.core.service.model.Gender;
import com.debaets.crud.core.service.model.Period;
import com.debaets.crud.core.service.model.User;
import com.debaets.crud.core.service.repository.PeriodRepository;
import com.debaets.crud.core.service.repository.UserRepository;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@Transactional
@EnableJpaRepositories(basePackages = "com.debaets.crud.core.service")
@EntityScan(basePackages = {"com.debaets.crud.core.service"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CustomRsqlVisitorTest {

	@Autowired
	@Qualifier("testUserRepo")
	private UserRepository repository;

	@Autowired
	@Qualifier("testPeriodRepository")
	private PeriodRepository periodRepository;

	private User userJohn;

	private User userTom;

	private final String birthDay_John = "01/01/1970";
	private final String birthDay_Tom = "01/01/1980";
	private final LocalDate WEDDING_DATE_JOHN = LocalDate.of(1995,1,1);
	private final LocalDate WEDDING_DATE_TOM = LocalDate.of(2005,1,1);

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
		userJohn.setWeddingDate(WEDDING_DATE_JOHN);
		userJohn.setIsAlive(true);
		repository.save(userJohn);

		userTom = new User();
		userTom.setFirstName("tom jean");
		userTom.setLastName("doe");
		userTom.setEmail("tom@doe.com");
		userTom.setAge(26);
		userTom.setAddress(Address.builder().street("street2").build());
		userTom.setGender(Gender.FEMALE);
		userTom.setBirthday(formatter.parse(birthDay_Tom));
		userTom.setWeddingDate(WEDDING_DATE_TOM);
		userTom.setIsAlive(false);
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

	//Test equality (Localdate)
	@Test
	public void givenWedding_whenGettingListOfUsers_thenCorrect() {
		Node rootNode = new RSQLParser().parse("weddingDate==01/01/1995");
		Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<>(new DictionaryService() {},false));
		List<User> results = repository.findAll(spec);

		assertThat(userJohn, isIn(results));
		assertThat(userTom, not(isIn(results)));
	}

	//Test >= (LocalDate)
	@Test
	public void givenWeddingDateGE_whenGettingListOfUsers_thenCorrect() {
		Node rootNode = new RSQLParser().parse("weddingDate>=01/01/2000");
		Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<>(new DictionaryService() {},false));
		List<User> results = repository.findAll(spec);

		assertThat(userTom, isIn(results));
		assertThat(userJohn, not(isIn(results)));

		rootNode = new RSQLParser().parse("weddingDate>=01/01/1995");
		spec = rootNode.accept(new CustomRsqlVisitor<>(new DictionaryService() {
		}, false));
		results = repository.findAll(spec);

		assertThat(userTom, isIn(results));
		assertThat(userJohn, isIn(results));

		rootNode = new RSQLParser().parse("weddingDate>=01/01/2006");
		spec = rootNode.accept(new CustomRsqlVisitor<>(new DictionaryService() {
		}, false));
		results = repository.findAll(spec);

		assertThat(userTom, not(isIn(results)));
		assertThat(userJohn, not(isIn(results)));
	}

	//Test > (LocalDate)
	@Test
	public void givenWeddingDateG_whenGettingListOfUsers_thenCorrect() {
		Node rootNode = new RSQLParser().parse("weddingDate>01/01/1995");
		Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<>(new DictionaryService() {
		}, false));
		List<User> results = repository.findAll(spec);

		assertThat(userTom, isIn(results));
		assertThat(userJohn, not(isIn(results)));
	}

	//Test < (LocalDate)
	@Test
	public void givenWeddingDateL_whenGettingListOfUsers_thenCorrect() {
		Node rootNode = new RSQLParser().parse("weddingDate<01/01/2005");
		Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<>(new DictionaryService() {
		}, false));
		List<User> results = repository.findAll(spec);

		assertThat(userJohn, isIn(results));
		assertThat(userTom, not(isIn(results)));
	}

	//Test <= (LocalDate)
	@Test
	public void givenWeddingDateLE_whenGettingListOfUsers_thenCorrect() {
		Node rootNode = new RSQLParser().parse("weddingDate<=01/01/2005");
		Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<>(new DictionaryService() {
		}, false));
		List<User> results = repository.findAll(spec);

		assertThat(userJohn, isIn(results));
		assertThat(userTom, isIn(results));
	}

	//Test equality (Boolean)
	@Test
	public void givenIsAlive_thenCorrect(){
		Node rootNode = new RSQLParser().parse("isAlive==true");
		Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<>(new DictionaryService() {},false));
		List<User> results = repository.findAll(spec);

		assertThat(userJohn, isIn(results));
		assertThat(userTom, not(isIn(results)));

		rootNode = new RSQLParser().parse("isAlive==false");
		spec = rootNode.accept(new CustomRsqlVisitor<>(new DictionaryService() {},false));
		results = repository.findAll(spec);

		assertThat(userJohn, not(isIn(results)));
		assertThat(userTom, isIn(results));

	}

	//Test search on inside list
	@Test
	@Ignore
	public void insideList_start_should_return() {

		Period period = Period.builder().start(1L).start(15L).user(userJohn).build();
		periodRepository.save(period);
		periodRepository.save(Period.builder().start(20L).end(25L).user(userJohn).build());
		Node rootNode = new RSQLParser().parse("periods.start<=10");
		Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<>(new DictionaryService() {},true));
		List<User> results = repository.findAll(spec);

		assertThat(userJohn, isIn(results));
		assertThat(userTom, not(isIn(results)));

	}

	@Test
	public void insideList_end_should_return() {

		periodRepository.save(Period.builder().start(1L).end(15L).user(userJohn).build());
		periodRepository.save(Period.builder().start(20L).end(25L).user(userJohn).build());
		Node rootNode = new RSQLParser().parse("periods.end>=10");
		Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<>(new DictionaryService() {},true));
		List<User> results = repository.findAll(spec);

		assertThat(userJohn, isIn(results));
		assertThat(userTom, not(isIn(results)));

	}

	@Test
	public void insideList_start_end_should_return() {

		periodRepository.save(Period.builder().start(1L).end(15L).user(userJohn).build());
		periodRepository.save(Period.builder().start(20L).end(25L).user(userJohn).build());
		Node rootNode = new RSQLParser().parse("periods.start<=10;periods.end>=11");
		Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<>(new DictionaryService() {},true));
		List<User> results = repository.findAll(spec);

		assertThat(userJohn, isIn(results));
		assertThat(userTom, not(isIn(results)));

	}

	@Test
	public void oustideList_start_end_should_not_return() {

		periodRepository.save(Period.builder().start(1L).end(15L).user(userJohn).build());
		periodRepository.save(Period.builder().start(20L).end(25L).user(userJohn).build());
		Node rootNode = new RSQLParser().parse("periods.start<=15;periods.end>=19");
		Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<>(new DictionaryService() {},true));
		List<User> results = repository.findAll(spec);

		assertThat(userJohn, not(isIn(results)));
		assertThat(userTom, not(isIn(results)));

	}
}
