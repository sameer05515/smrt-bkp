package com.p.db.backup.word.meaning.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.p.db.backup.word.meaning.exception.InvalidInputSuppliedException;
import com.p.db.backup.word.meaning.jpa.JDBCTemplateRepository;
import com.p.db.backup.word.meaning.jpa.WordRepository;
import com.p.db.backup.word.meaning.pojo.Word;

@Service
public class CurdService {

	@Autowired
	WordRepository wordRepository;

	@Autowired
	JDBCTemplateRepository jdbcTemplateRepository;

	public Word saveWord(Word word) throws InvalidInputSuppliedException {
		/**
		 * #################################################
		 * 
		 * Validation start
		 */
		if (word == null) {
			throw new InvalidInputSuppliedException("Null word object supplied. ");
		}

		if (word.getUnique_name() == null || word.getUnique_name().trim().equals("")) {
			throw new InvalidInputSuppliedException("Null unique name supplied. ");
		}

		boolean uniqueNameExists = jdbcTemplateRepository.checkUniqueNameExists(word.getUnique_name(),0);

		if (uniqueNameExists) {
			throw new InvalidInputSuppliedException("Unique name : " + word.getUnique_name()
					+ " : is already exists in database. Please check and provide some other unique name ");
		}

		/**
		 * Validation end
		 * 
		 * #################################################
		 */

		Word w = null;
		int count=(int)wordRepository.count();		
		count+=1;
		word.setId(count);
		word.setCreated_on(new Date());
		word.setUpdated_on(new Date());
		word.setLast_read(new Date());
		int[][] resp = jdbcTemplateRepository.saveWord(word);
		w = wordRepository.findById(word.getId()).get();
		return w;
	}

	public Word updateWord(Word word) throws InvalidInputSuppliedException {
		/**
		 * #################################################
		 * 
		 * Validation start
		 */
		if (word == null) {
			throw new InvalidInputSuppliedException("Null word object supplied. ");
		}

		if (word.getUnique_name() == null || word.getUnique_name().trim().equals("")) {
			throw new InvalidInputSuppliedException("Null unique name supplied. ");
		}

		if (word.getId() <= 0) {
			throw new InvalidInputSuppliedException("Invalid word id : " + word.getId() + " : supplied. ");
		}

		boolean uniqueNameExists = jdbcTemplateRepository.checkUniqueNameExists(word.getUnique_name(),word.getId());

		if (uniqueNameExists) {
			throw new InvalidInputSuppliedException("Unique name : " + word.getUnique_name()
					+ " : is already exists in database. Please check and provide some other unique name ");
		}

		/**
		 * Validation end
		 * 
		 * #################################################
		 */

		Word w = null;
		word.setUpdated_on(new Date());
		word.setLast_read(new Date());
		int[][] resp = jdbcTemplateRepository.updateWord(word);
		w = wordRepository.findById(word.getId()).get();
		return w;
	}

}
