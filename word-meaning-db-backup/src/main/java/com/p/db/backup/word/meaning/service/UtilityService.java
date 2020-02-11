package com.p.db.backup.word.meaning.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.p.db.backup.word.meaning.exception.InvalidInputSuppliedException;

@Service
public class UtilityService {

	private ObjectMapper prettyObjectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

	private ObjectMapper simpleObjectMapper = new ObjectMapper();

	public String toJsonString(Object obj, boolean prettyJson)
			throws InvalidInputSuppliedException, JsonProcessingException {
		if (obj == null) {
			throw new InvalidInputSuppliedException("Null object supplied (" + obj + ") ");
		}
		if (prettyJson) {
			return prettyObjectMapper.writeValueAsString(obj);
		} else {
			return simpleObjectMapper.writeValueAsString(obj);
		}
	}

}
