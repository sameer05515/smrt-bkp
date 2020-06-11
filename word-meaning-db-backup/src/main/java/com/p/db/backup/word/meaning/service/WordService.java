package com.p.db.backup.word.meaning.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.p.db.backup.word.meaning.constants.GlobalContants;
import com.p.db.backup.word.meaning.exception.InvalidInputSuppliedException;
import com.p.db.backup.word.meaning.jpa.WordRepository;
import com.p.db.backup.word.meaning.pojo.Word;

@Service
public class WordService {
	
	@Autowired
	WordRepository wordRepository;
	
	@Autowired
	GlobalContants globalContants;
	
	public List<Word> findPagedData(int pageNumber,int pageSize) throws InvalidInputSuppliedException{
		
		if(pageNumber<0) {
			throw new InvalidInputSuppliedException("Invalid page number ( "+pageNumber +" ) supplied. ");
		}
		
		if(pageSize<0 || pageSize>globalContants.getMAX_PAGE_SIZE()) {
			throw new InvalidInputSuppliedException("Invalid page size ( "+pageSize +" ) supplied. Value should be between 0 and "+globalContants.getMAX_PAGE_SIZE());
		}
		
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		List<Word> pagedData = wordRepository.findPagedData(pageable);
		
		return pagedData;
	}

}
