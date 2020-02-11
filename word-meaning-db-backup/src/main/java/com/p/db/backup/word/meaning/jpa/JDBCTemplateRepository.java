package com.p.db.backup.word.meaning.jpa;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import com.p.db.backup.word.meaning.pojo.Word;

@Service
public class JDBCTemplateRepository {

	/**
	 * ##########################################
	 * 
	 * JDBC Template methods
	 * 
	 * ##########################################
	 */

	@Autowired
	JdbcTemplate jdbcTemplate;

	public int[][] saveWord(Word word) {

		List<Word> lw = new ArrayList<Word>();
		lw.add(word);
		int[][] updateCounts = jdbcTemplate.batchUpdate(
				"INSERT INTO t_word(id, unique_name, word, type, details, created_on, updated_on, last_read)"
						+ " values(?,?,?,?,?,?,?,?)",
				lw, 1, new ParameterizedPreparedStatementSetter<Word>() {
					public void setValues(PreparedStatement ps, Word argument) throws SQLException {
						int i = 0;
						
						ps.setInt(i++, argument.getId());
						ps.setString(i++, argument.getUnique_name());
						ps.setString(i++, argument.getWord());
						ps.setString(i++, argument.getType());
						ps.setString(i++, argument.getDetails());
						ps.setTimestamp(i++, new Timestamp(argument.getCreated_on().getTime()));
						ps.setTimestamp(i++, new Timestamp(argument.getUpdated_on().getTime()));
						ps.setTimestamp(i++, new Timestamp(argument.getLast_read().getTime()));

					}
				});
		return updateCounts;

	}

	public int[][] updateWord(Word word) {

		List<Word> lw = new ArrayList<Word>();
		lw.add(word);
		int[][] updateCounts = jdbcTemplate.batchUpdate(
				"update t_word set unique_name = ?, word = ? , type = ?, details = ?, updated_on = ? , last_read = ?"
						+ " where id = ? ",
				lw, 1, new ParameterizedPreparedStatementSetter<Word>() {
					public void setValues(PreparedStatement ps, Word argument) throws SQLException {
						int i = 1;

						ps.setString(i++, argument.getUnique_name());
						ps.setString(i++, argument.getWord());
						ps.setString(i++, argument.getType());
						ps.setString(i++, argument.getDetails());
						ps.setTimestamp(i++, new Timestamp(argument.getUpdated_on().getTime()));
						ps.setTimestamp(i++, new Timestamp(argument.getLast_read().getTime()));

						ps.setInt(i++, argument.getId());

					}
				});
		return updateCounts;

	}

	public boolean checkUniqueNameExists(String uniqueName) {
		String sql = "SELECT COUNT(*) FROM T_WORD WHERE UNIQUE_NAME = ?";
		boolean result = false;
		int count = jdbcTemplate.queryForObject(sql, new Object[] { uniqueName }, Integer.class);
		if (count > 0) {
			result = true;
		}
		return result;
	}

}
