package com.sandbox.javers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.javers.core.JaversBuilder;
import org.javers.core.changelog.SimpleTextChangeLog;
import org.javers.core.diff.Change;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.QueryBuilder;
import org.javers.repository.sql.DialectName;
import org.javers.repository.sql.JaversSqlRepository;
import org.javers.repository.sql.SqlRepositoryBuilder;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class AuditTest extends BaseTest {

	@Test
	public void testAudit() {
		JaversSqlRepository sqlRepository = SqlRepositoryBuilder.sqlRepository()
				.withConnectionProvider(connectionProvider).withDialect(DialectName.MYSQL).build();
		javers = JaversBuilder.javers().registerJaversRepository(sqlRepository).build();

		Set<String> nicknames = Sets.newHashSet("oldie", "baldie");
		person1.setNicknames(nicknames);

		javers.commit(AUTHOR_APP, person1);

		person1.setName("Kevin");
		// and persist another commit
		javers.commit(AUTHOR_APP, person1);

		List<CdoSnapshot> snapshots = javers
				.findSnapshots(QueryBuilder.byInstanceId(12l, Person.class).limit(10).build());
		assertTrue(snapshots.size() >= 2);

		List<Change> changes = javers.findChanges(QueryBuilder.byInstanceId(12l, Person.class).limit(10).build());
		assertTrue(changes.size() >= 1);
		System.out.println("********Audit*********");
		System.out.println(prettyPrint.apply(changes));
		String changeLog = javers.processChangeList(changes, new SimpleTextChangeLog());
		System.out.println(changeLog);
	}

	@Test
	public void testJQL() {
		JaversSqlRepository sqlRepository = SqlRepositoryBuilder.sqlRepository()
				.withConnectionProvider(connectionProvider).withDialect(DialectName.MYSQL).build();
		javers = JaversBuilder.javers().registerJaversRepository(sqlRepository).build();

		Set<String> nicknames = Sets.newHashSet("oldie", "baldie");
		person2.setNicknames(nicknames);
		javers.commit(AUTHOR_APP, person2);

		LinkedHashSet<String> myNameKeepsChanging = Sets
				.newLinkedHashSet(Lists.newArrayList("Kohli", "Dhoni", "Hardik", "Rahul"));
		myNameKeepsChanging.forEach(name -> {
			// Keep changing names and commit changes for audit
			person2.setName(name);
			javers.commit(AUTHOR_APP, person2);
		});
		
		person1.setLogin("kassandra");
		javers.commit(AUTHOR_APP, person1);
		//Should be the last set
		assertEquals(person2.getName(), "Rahul");

		List<Change> changes = javers.findChanges(
				QueryBuilder.byInstanceId(12l, Person.class).withChangedProperty("name").limit(100).build());
		assertTrue(changes.size() >= 1);
		System.out.println("********JQL*********");
		changes.forEach(c -> {
			assertEquals(c.getAffectedGlobalId().value(),"com.sandbox.javers.Person/12");
		});
		
		changes = javers.findChanges(
				QueryBuilder.byInstanceId(12l, Person.class).byAuthor("RANDOM").limit(100).build());
		assertTrue(changes.isEmpty());

	}
}
