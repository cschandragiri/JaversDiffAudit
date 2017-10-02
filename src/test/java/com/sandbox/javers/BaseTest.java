package com.sandbox.javers;

import java.sql.DriverManager;
import java.util.List;
import java.util.function.Function;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.container.ContainerChange;
import org.javers.core.diff.changetype.map.MapChange;
import org.javers.repository.sql.ConnectionProvider;
import org.junit.Before;

public class BaseTest {

	protected final String AUTHOR_APP = "APP";
	protected Javers javers = null;
	protected Person person1, person2 = null;
	protected ConnectionProvider connectionProvider = () -> DriverManager.getConnection("jdbc:mysql://localhost:3307/javers?user=root&password=######");

	Function<List<Change>, String> prettyPrint = changes -> {
		StringBuffer sb = new StringBuffer();
		changes.stream().forEachOrdered(change -> {
			sb.append(" Change:").append(change.getClass().getName());
			if (change instanceof ValueChange || change instanceof ReferenceChange) {
				sb.append("#").append(((ValueChange) change).getPropertyName());
				sb.append(" Left:").append(((ValueChange) change).getLeft().toString());
				sb.append(" Right").append(((ValueChange) change).getRight().toString());
			} else if (change instanceof ContainerChange) {
				sb.append("#").append(((ContainerChange) change).getPropertyName());
				sb.append(" Removed Values:").append(((ContainerChange) change).getRemovedValues());
				sb.append(" Added Values").append(((ContainerChange) change).getAddedValues());
			} else if (change instanceof MapChange) {
				sb.append("#").append(((MapChange) change).getPropertyName());
				sb.append(" Entries Added:").append(((MapChange) change).getEntryAddedChanges());
				sb.append(" Entries Removed:").append(((MapChange) change).getEntryRemovedChanges());
			} else if (change instanceof NewObject) {
				sb.append(" Added: ").append(change.getAffectedObject().map(Object::toString).orElse("NULL"));
			} else if (change instanceof ObjectRemoved) {
				sb.append(" Removed: ").append(change.getAffectedObject().map(Object::toString).orElse("NULL"));
			}
			sb.append("\n");
		});
		return sb.toString();
	};

	@Before
	public void init() {
		javers = JaversBuilder.javers().build();
		person1 = new Person(12l, "tommy", "Tommy Smart");
		person2 = new Person(12l, "tommy", "Tommy C. Smart");
	}
}
