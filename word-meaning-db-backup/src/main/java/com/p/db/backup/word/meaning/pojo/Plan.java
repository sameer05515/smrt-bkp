package com.p.db.backup.word.meaning.pojo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.p.db.backup.word.meaning.exception.InvalidInputSuppliedException;

public class Plan {

	private String description;

	private List<Plan> children = new ArrayList<Plan>();

	public Plan(String description) {
		super();
		this.description = description;

	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Plan> getChildren() {
		return children;
	}

	public Plan addChild(Plan child) throws InvalidInputSuppliedException {
		if (child == null) {
			throw new InvalidInputSuppliedException("Invalid value supplied (" + child + ") ");
		}
		this.children.add(child);
		return this;
	}

	@Override
	public String toString() {
		return "Plan [description=" + description + ", children=" + children + "]";
	}

	public static void main(String... strings) {
		try {
			Plan p = new Plan("1");
			p.addChild(new Plan("1.1").addChild(new Plan("1.1.1"))).addChild(new Plan("1.2")).addChild(new Plan("1.3"));

			ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
			String jsonStr = objectMapper.writeValueAsString(p);

			System.out.println(jsonStr);

		} catch (InvalidInputSuppliedException e) {

			e.printStackTrace();
		} catch (JsonProcessingException e) {

			e.printStackTrace();
		}

	}

}
