package dao.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.ResourceBundle;

import logic.parse.Parse4Body;
import dao.DatabaseDao;

public class DatabaseDaoImpl implements DatabaseDao {
	private Connection conn;
	private ResultSet resultSet;
	private PreparedStatement prepstmt;
	private Statement stmt;
	private String driver;
	private String url;
	private String user;
	private String pass;
	
	//コンストラクタ
	public DatabaseDaoImpl(ResourceBundle domconfBundle) {
		this.driver = domconfBundle.getString("DB_DRIVER").trim();
		this.url = domconfBundle.getString("DB_URL").trim();
		this.user = domconfBundle.getString("DB_ID").trim();
		this.pass = domconfBundle.getString("DB_PASSWORD").trim();
	}
	
	//
	@Override
	public ResultSet read(String query, ListIterator<String> params) throws SQLException, ClassNotFoundException {
		Class.forName(driver);
		ArrayList<String> paraList = new ArrayList<String>(); 
		ArrayList<String> typeList = new ArrayList<String>();
		
		//ユーザ名とパスワードのチェック
		if (user != null && pass != null) {
			conn = DriverManager.getConnection(url, user, pass);
			//パラメータがあればPreparedStatement、無ければStatementへ
			if (params == null) {
				stmt = conn.createStatement(
						ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_UPDATABLE
						);
			} else {
				int holderIndex = -1;
				while (params.hasNext()) {
					String para = params.next().toString();
					if (para.equalsIgnoreCase("*"))
						continue;
					
					holderIndex = query.indexOf("?", holderIndex + 1);
					if (holderIndex == -1)
						break;
					
					paraList.add(para);
					if (String.valueOf(query.charAt(holderIndex - 1)).equals("'") && String.valueOf(query.charAt(holderIndex + 1)).equals("'")) {
						query = query.replaceFirst("'\\?'", "?");
						typeList.add("String");
					} else {
						typeList.add("int");
					}
				}
				prepstmt = conn.prepareStatement(query);
			}
		} else System.out.println("DBに接続できません。");
		
		if (prepstmt != null) {
			for (int i = 0; i < paraList.size(); i++) {
				if (typeList.get(i).equalsIgnoreCase("String"))
					prepstmt.setString(i + 1, paraList.get(i));
				else
					prepstmt.setInt(i + 1, Integer.parseInt(paraList.get(i)));
			}
			resultSet = prepstmt.executeQuery();
		} else if (stmt != null) {
			stmt.executeUpdate("SET enable_seqscan = false");
			resultSet = stmt.executeQuery(query);
		}
		return resultSet;
		
	}
	
	@Override
	public ResultSet bodyRead(Parse4Body parse) throws Exception{
		return read(parse.getQuery(), parse.getParameter().listIterator());
	}
	
	@Override
	public void write(String query) throws ClassNotFoundException,SQLException{
		Class.forName(driver);
		conn = DriverManager.getConnection(url,user,pass);
		stmt = conn.createStatement();
		stmt.execute(query);
	}
	
	//
	@Override
	public void close() throws SQLException {
		if(resultSet!=null)
			resultSet.close();
		if(prepstmt!=null)
			prepstmt.close();
		if(stmt!=null)
			stmt.close();
		conn.close();
	}
}
