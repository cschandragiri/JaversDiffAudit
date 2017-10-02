package com.sandbox.javers;

import java.util.Map;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.javers.core.metamodel.annotation.DiffIgnore;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Entity
public class Person {

	@DiffIgnore
	@Id
	private Long id;

	private String login;

	private String name;

	private Set<String> nicknames = Sets.newHashSet();

	private Map<String, Double> asset = Maps.newHashMap();

	public Person(Long id, String login, String name) {
		this.id = id;
		this.login = login;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public String getLogin() {
		return login;
	}

	public String getName() {
		return name;
	}

	public Set<String> getNicknames() {
		return nicknames;
	}

	public void setNicknames(Set<String> nicknames) {
		this.nicknames = nicknames;
	}

	public Map<String, Double> getAsset() {
		return asset;
	}

	public void setAsset(Map<String, Double> asset) {
		this.asset = asset;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Person [id=" + id + ", login=" + login + ", name=" + name + ", nicknames=" + nicknames + ", asset="
				+ asset + "]";
	}
}
