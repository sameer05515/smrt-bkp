package com.p.db.backup.word.meaning.jpa;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonObjectDeserializer;
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
						int i = 1;

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

	public boolean checkUniqueNameExists(String uniqueName, int id) {

		String sql = "SELECT COUNT(*) FROM T_WORD WHERE UPPER(UNIQUE_NAME) = ? and ID=?";
		boolean result = false;
		int count = jdbcTemplate.queryForObject(sql, new Object[] { uniqueName.toUpperCase(), id }, Integer.class);
		if (count > 0) {
			result = true;
		}
		return result;
	}

	public int getIdForUniqueName(String uniqueName, int id) {

		String sql = "SELECT ID FROM T_WORD WHERE UPPER(UNIQUE_NAME) = ?";
		boolean result = false;
		int idFromDB = jdbcTemplate.queryForObject(sql, new Object[] { uniqueName.toUpperCase() }, Integer.class);
//		if (idFromDB > 0) {
//			result = idFromDB!=id;
//		}
		return idFromDB;
	}

	public List<Map<String, Object>> getDatewiseUpdateData() {
//		String sql = "SELECT Date(updated_on) as lastUpdatedOn,count(*) as count "
//				+ "FROM t_word group by lastUpdatedOn order by  lastUpdatedOn desc";

		String sql = "SELECT Date(changedate) as lastUpdatedOn, count(distinct(word_id)) as count \r\n"
				+ "FROM word_update_log \r\n" + "Where action='update'\r\n" + "group by lastUpdatedOn\r\n"
				+ "order by  lastUpdatedOn desc\r\n" + "";
		
		System.out.println(sql);
		
		List<Map<String, Object>> reportData = new ArrayList<>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

		Format f = new SimpleDateFormat("dd-MMM-yyyy");

		for (Map<String, Object> row : rows) {
			Map<String, Object> map = new HashMap<String, Object>();

			map.put("lastUpdatedOn", f.format(((java.sql.Date) row.get("lastUpdatedOn"))));
//			map.put("lastUpdatedOn",
//					new Timestamp(((java.sql.Date)row.get("lastUpdatedOn")).getTime()));
			map.put("count", ((Long) row.get("count")).intValue());
			reportData.add(map);
		}

		return reportData;
	}
	
	public static void main(String[] args) {
		String sql = "SELECT Date(changedate) as lastUpdatedOn, count(distinct(word_id)) as count \r\n"
				+ "FROM word_update_log \r\n" + "Where action='update'\r\n" + "group by lastUpdatedOn\r\n"
				+ "order by  lastUpdatedOn desc\r\n" + "";
		
		System.out.println(sql);
	}

}
