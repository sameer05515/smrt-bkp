package com.p.db.backup.word.meaning.jpa;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.p.db.backup.word.meaning.pojo.Word;

public interface WordRepository extends JpaRepository<Word, Integer> {

	// @Query("FROM Word")
	@Query(value = "SELECT * from t_word w where DATE(w.updated_on)<= (SELECT DATE_SUB(CURRENT_DATE,INTERVAL :daysBack day)) ", nativeQuery = true)
	List<Word> findPagedData(Pageable pageable, @Param("daysBack") int daysBack);

//	List<Word> findPagedData(Pageable pageable);

	@Modifying
	@Transactional
	@Query(value = "INSERT INTO t_word(id, unique_name, word, type, details, created_on, updated_on, last_read) "
			+ "VALUES (:id, :unique_name, :word, :type, :details, :created_on, :updated_on, :last_read)", nativeQuery = true)
	void insertWord(@Param("id") int id, @Param("unique_name") String unique_name, @Param("word") String word,
			@Param("type") String type, @Param("details") String details, @Param("created_on") Date created_on,
			@Param("updated_on") Date updated_on, @Param("last_read") Date last_read);

	/**
	 * @ using @query with native
	 */
	@Query(value = "SELECT * from t_word w where w.word like %:name% ", nativeQuery = true)
	List<Word> findByNameNative(@Param("name") String name);

}