package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ListIterator;

import logic.parse.Parse4Body;

/**
 * データベースへのアクセスオブジェクト
 * SQLを実行し、データを取得
 */
public interface DatabaseDao {

	/**
	 * SQLの実行
	 * @param query SQLクエリ
	 * @param params メールアドレスに記述されたパラメータ
	 * @return SQLの結果の集合
	 * @throws Exception
	 */
	public ResultSet read(String query, ListIterator<String> params)
			throws SQLException, ClassNotFoundException;

	/**
	 * 本文編集機能で用いたパーザからSQLを実行
	 * @param parse 本文編集機能のパーザ
	 * @return SQLの結果の集合
	 * @throws Exception
	 */
	public ResultSet bodyRead(Parse4Body parse) throws Exception;

	/**
	 * データベースを操作するSQLの実行
	 * @param query SQLクエリ
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void write(String query) throws ClassNotFoundException,
			SQLException;

	public void close() throws SQLException;

}