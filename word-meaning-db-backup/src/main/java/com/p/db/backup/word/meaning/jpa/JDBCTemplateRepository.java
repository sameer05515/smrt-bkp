package com.p.db.backup.word.meaning.jpa;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
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

		String sql = "SELECT COUNT(*) FROM t_word WHERE UPPER(UNIQUE_NAME) = ? and ID=?";
		boolean result = false;
		int count = jdbcTemplate.queryForObject(sql, new Object[] { uniqueName.toUpperCase(), id }, Integer.class);
		if (count > 0) {
			result = true;
		}
		return result;
	}

	public int getIdForUniqueName(String uniqueName, int id) {

		String sql = "SELECT ID FROM t_word WHERE UPPER(UNIQUE_NAME) = ?";
		// boolean result = false;
		int idFromDB = jdbcTemplate.queryForObject(sql, new Object[] { uniqueName.toUpperCase() }, Integer.class);
//		if (idFromDB > 0) {
//			result = idFromDB!=id;
//		}
		return idFromDB;
	}

	public List<Map<String, Object>> getDatewiseUpdateData(String columnName) {

		// "LASTUPDATED", "CREATEDON" , "READON"
		List<Map<String, Object>> reportData = new ArrayList<>();

		switch (columnName) {
		case "LASTUPDATED":
			reportData = getLastUpdatedReport();
			break;
		case "CREATEDON":
			reportData = getCreatedOnReport();
			break;
		case "READON":
			reportData = getReadOnReport();
			break;
		default:
			System.out.println("Invalid column name");
		}
		return reportData;
	}

	private List<Map<String, Object>> getReadOnReport() {
		String sql = "SELECT Date(changedate) as Action_date," + " count(distinct(word_id)) as count ,"
				+ "'read' as Action\r\n" + "FROM word_update_log \r\n" + "Where action='read'\r\n"
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

		String sql = "SELECT Date(created_on) as Action_date, " + "count(distinct(id)) as count , "
				+ "'created' as Action\r\n" + "FROM t_word \r\n" + "group by Action_date\r\n"
				+ "order by  created_on desc";

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

	public boolean addRead(Word word) {

		String[] sqlArray = {
				"INSERT INTO word_update_log SET action = 'read', word_id = " + word.getId() + ", unique_name = '"
						+ word.getUnique_name() + "' , changedate = NOW()",
				"update t_word set last_read= NOW() where id=" + word.getId() + "" };
		for(String sql:sqlArray) {
			System.out.println(sql);
		}

		int[] resp = jdbcTemplate.batchUpdate(sqlArray);

		return true;
	}

	public List<Map<String,Object>> getReads(int id) {

		String sql = "SELECT * FROM word_update_log wul " + "where wul.word_id='" + id + "' " + "and wul.action='read'";

		System.out.println(sql);

		List<Map<String, Object>> reportData = new ArrayList<>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		
		for (Map<String, Object> row : rows) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("readDate", (java.sql.Timestamp)row.get("changedate"));
			map.put("action", row.get("action"));
			map.put("wordId", row.get("word_id"));
			map.put("id", ((Integer) row.get("id")));
			map.put("uniqueName", (row.get("unique_name")));
			reportData.add(map);
		}

		return reportData;

	}

}
