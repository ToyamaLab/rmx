package dao.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
		this.driver = domconfBundle.getString("DB_DRIVER");
		this.url = domconfBundle.getString("DB_URL");
		this.user = domconfBundle.getString("DB_ID");
		this.pass = domconfBundle.getString("DB_PASSWORD");
	}
	
	//
	@Override
	public ResultSet read(String query, ListIterator<String> params) throws SQLException, ClassNotFoundException {
		Class.forName(driver);
		//ユーザ名とパスワードのチェック
		if (user != null && pass != null) {
			conn = DriverManager.getConnection(url, user, pass);
			//パラメータがあればPreparedStatement、無ければStatementへ
			if (params == null) {
				stmt = conn.createStatement(
						ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_UPDATABLE
						);
			} else prepstmt = conn.prepareStatement(query);
		} else System.out.println("DBに接続できません。");
		
		if(prepstmt != null) {
			int num=1;
			while (params.hasNext()) {
				String testpara = params.next().toString();
				if (testpara.toString().equalsIgnoreCase("integer")) {
					int temp;
					temp = Integer.parseInt((params.next().toString()));
					prepstmt.setInt(num, temp);
					num++;
				} else {
					String str = params.next().toString();
					if (!str.equalsIgnoreCase("*")) {
						prepstmt.setString(num, str);
						num++;
					}
				}
			}
			resultSet = prepstmt.executeQuery();
		} else if (stmt!=null){
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
