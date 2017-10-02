package com.sandbox.javers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.javers.common.collections.Lists;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class DiffTest extends BaseTest {
	
	@Test
	public void diffTest() {
		Diff diff = javers.compare(person1, person2);
		List<ValueChange> valueChanges = diff.getChangesByType(ValueChange.class);
		assertTrue(diff.getChanges().size() == 1);
		ValueChange valueChange = valueChanges.get(0);
		assertEquals("name", valueChange.getPropertyName());
		assertEquals("Tommy Smart", valueChange.getLeft());
		assertEquals("Tommy C. Smart", valueChange.getRight());
	}

	@Test
	public void diffCollections() {

		Set<String> p1 = Sets.newHashSet("oldie", "baldie");
		Set<String> p2 = Sets.newHashSet("baller", "oldie", "timer");
		Diff diff = javers.compare(p1, p2);
		List<SetChange> setChanges = diff.getChangesByType(SetChange.class);
		assertEquals(setChanges.get(0).getAddedValues().size(), 2);
		assertEquals(setChanges.get(0).getRemovedValues().size(), 1);

		person1.setNicknames(p1);
		person2.setNicknames(p2);

		diff = javers.compare(person1, person2);
		
		assertTrue(diff.getChanges().size() == 2);

		List<Change> changes = diff.getChanges();
		System.out.println(prettyPrint.apply(changes));

		Map<String, Double> asset1 = Maps.newHashMap(ImmutableMap.of("Cash", 10000.0, "Equity", 5000.0, "Gold", 1000.0));
		Map<String, Double> asset2 = Maps.newHashMap(ImmutableMap.of("Cash", 10000.0, "Equity", 5500.0, "Silver", 1000.0));
		
		person1.setAsset(asset1);
		person2.setAsset(asset2);

		diff = javers.compare(person1, person2);
		assertTrue(diff.getChanges().size() == 3);

		changes = diff.getChanges();
		System.out.println("********diffCollections*********");
		System.out.println(prettyPrint.apply(changes));
	}
	
	@Test
    public void testCompareCollections() {

        Set<String> p1 = Sets.newHashSet("oldie", "baldie");
        person1.setNicknames(p1);
        Map<String, Double> asset1 = Maps.newHashMap(ImmutableMap.of("Cash", 10000.0, "Equity", 5000.0, "Gold", 1000.0));
        person1.setAsset(asset1);
        
        Person person3 = new Person(13l, "tommy", "Hilfiger");
        Set<String> p3 = Sets.newHashSet("oldie", "baldie");
        person3.setNicknames(p3);
        Map<String, Double> asset3 = Maps.newHashMap(ImmutableMap.of("Cash", 10500.0, "Equity", 5000.0, "Gold", 1000.0));
        person3.setAsset(asset3);
        
        Person person4 = new Person(14l, "tommy", "Mardin");
        Set<String> p4 = Sets.newHashSet("oldie", "baldie");
        person4.setNicknames(p4);
        Map<String, Double> asset4 = Maps.newHashMap(ImmutableMap.of("Cash", 10000.0, "Equity", 5000.0, "Gold", 1000.0));
        person4.setAsset(asset4);
        
        Person person5 = new Person(15l, "tommy", "Chris");
        Set<String> p5 = Sets.newHashSet("oldie", "baldie");
        person5.setNicknames(p5);
        Map<String, Double> asset5 = Maps.newHashMap(ImmutableMap.of("Cash", 10000.0, "Equity", 5000.0, "Silver", 1000.0));
        person5.setAsset(asset5);

        List<Person> listA = Lists.asList(person1, person3);
        
        List<Person> listB = Lists.asList(person4, person5);

        Diff diff = javers.compareCollections(listA, listB, Person.class);
        List<Change> changes = diff.getChanges();
        System.out.println("********testCompareCollections*********");
        System.out.println(prettyPrint.apply(changes));
    }
}
