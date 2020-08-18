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
						ps.setString(i++, argument.getUnique_name().trim().toUpperCase());
						ps.setString(i++, argument.getWord().trim().toUpperCase());
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

						ps.setString(i++, argument.getUnique_name().trim().toUpperCase());
						ps.setString(i++, argument.getWord().trim().toUpperCase());
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

	public List<Map<String, Object>> getDatewiseUpdateData(String columnName) {

//"LASTUPDATED", "CREATEDON"
		List<Map<String, Object>> reportData = new ArrayList<>();

		switch (columnName) {
		case "LASTUPDATED":
			reportData = getLastUpdatedReport();
			break;
		case "CREATEDON":
			reportData = getCreatedOnReport();
			break;
		default:
			System.out.println("Invalid column name");
		}		
		return reportData;
	}

	private List<Map<String, Object>> getLastUpdatedReport() {

		String sql = "SELECT Date(changedate) as Action_date," + " count(distinct(word_id)) as count ,"
				+ "'updated' as Action\r\n" + "FROM word_update_log \r\n" + "Where action='update'\r\n"
				+ "group by Action_date\r\n" + "order by  Action_date desc";

		System.out.println(sql);

		List<Map<String, Object>> reportData = new ArrayList<>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

		Format f = new SimpleDateFormat("dd-MMM-yyyy");

		for (Map<String, Object> row : rows) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("ActionDate", f.format(((java.sql.Date) row.get("Action_date"))));
			map.put("Count", ((Long) row.get("count")).intValue());
			map.put("Action", (row.get("Action")));
			reportData.add(map);
		}
		return reportData;
	}

	private List<Map<String, Object>> getCreatedOnReport() {

		String sql = "SELECT Date(created_on) as Action_date, "
				+ "count(distinct(id)) as count , "
				+ "'created' as Action\r\n" + 
				"FROM t_word \r\n" + 
				"group by Action_date\r\n" + 
				"order by  created_on desc";

		System.out.println(sql);

		List<Map<String, Object>> reportData = new ArrayList<>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

		Format f = new SimpleDateFormat("dd-MMM-yyyy");

		for (Map<String, Object> row : rows) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("ActionDate", f.format(((java.sql.Date) row.get("Action_date"))));
			map.put("Count", ((Long) row.get("count")).intValue());
			map.put("Action", (row.get("Action")));
			reportData.add(map);
		}
		return reportData;
	}

	public static void main(String[] args) {
		String sql = "SELECT Date(created_on) as Action_date, " + "count(distinct(id)) as count , "
				+ "'created' as Action\r\n" + "FROM t_word \r\n" + "group by created_on\r\n"
				+ "order by  created_on desc";

		System.out.println(sql);
	}

}
