
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class dbtest {
	public static void main(String[] args) {
		inputQuery();
		return;
	}

    public static void inputQuery() {
    	   boolean flag = false;
    	   String query = new String();
    	   System.out.println("invoid");
    	   while(!flag) {
    		   Scanner scanner = new Scanner(System.in);
    		   System.out.print("input : ");

    		   while(scanner.hasNext()) {
    			   String tmp = scanner.next();
    			   if( tmp.matches("exit")) return;
    			   query += " ";
    			   query += tmp;
    			   //if(tmp.contains("select")) query += " PROVENANCE";
    			   if(tmp.contains(";")) {
    				   flag = true;
    				   break;
    			   }
    			   //System.out.println(query);
    		   }
    		   //System.out.println(query);
    	   }
    	   //System.out.println("End");
    	   flag = false;
    	   connectpsql(query);
    }

    public static String getTableName(String s) {
    		System.out.println("column name " + s);
				String name = s.substring(12, s.indexOf("_", 13));
				System.out.println("table name " + name);
    		return name;
    }

    public static String getColumnName(String s) {
    		int last = s.lastIndexOf("_");
    		int next = s.lastIndexOf("__");
    		if(last - next < 1);
    		String name = s.substring(12, last);
    		return name;
    }

    public static String getPColumn(String s) {
    		return s;
    }

    public static boolean isExist(ArrayList<TableInfo> ar, String s) {
    		for(int i = 0; i < ar.size(); i++) {
    			if(s.equals(ar.get(i).getTableName())) return true;
    		}
    		return false;
    }

    public static int getTableIndex(ArrayList<TableInfo> ar, String s) {
    		for(int i = 0; i < ar.size(); i++) {
					if(s.equals(ar.get(i).getTableName())) return i;
				}
				return -1;
    }

    public static void connectpsql(String q) {
    		System.out.println(q);
        // データベースへの接続情報を格納する変数
        Connection conn = null;
        // JDBCドライバの読み込み
        try {
            // postgreSQLのJDBCドライバを読み込み
            Class.forName("org.postgresql.Driver");
        } catch(ClassNotFoundException e) {
            // JDBCドライバが見つからない場合
            e.printStackTrace();
        }
        try {
            // 1. データベースへの接続
            conn = DriverManager.getConnection("jdbc:postgresql:testdb", "perm", "pass");
            System.out.println("Connect");

            // 2. SELECT文の発光と結果の取得
            // Statementオブジェクトを生成
            Statement stmt = conn.createStatement();
            // SELECT文の発行と検索結果を格納する
            ResultSet rset = stmt.executeQuery(q);
            ArrayList<TableInfo> tables = new ArrayList<TableInfo>();
            ArrayList<String> columnName = new ArrayList<String>();
            ArrayList<String> checkColumnName = new ArrayList<String>();

            for(int i = 0; i < rset.getMetaData().getColumnCount(); i++) {
            	System.out.println("i = " + i + " " + rset.getMetaData().getColumnCount());
            		columnName.add(rset.getMetaData().getColumnName(i + 1));
             	System.out.println("AAA " + columnName.get(i));

             	if(columnName.get(i).contains("prov_public_")) {
             		String tableName = getTableName(columnName.get(i));
             		if(isExist(tables, tableName)) {
             			int tableIndex = getTableIndex(tables, tableName);
             			if(tableIndex < 0) System.out.println("Not correct tableIndex");
             			if(columnName.get(i).matches(".*[_]+[0-9]+[_]+.*")) {
             				String cname = getColumnName(columnName.get(i));
             				if(cname.equals(tables.get(tableIndex).getCheckColumn())) {
             					tables.get(tableIndex).addCheckColumns(columnName.get(i));
             				}
             			}
             		} else {
             			TableInfo table = new TableInfo(tableName);
             			DatabaseMetaData dbmd = conn.getMetaData();
             			ResultSet rs2 = dbmd.getBestRowIdentifier(null, conn.getSchema(), tableName, 0, true);
             			try {
             				while (rs2.next()) {
             					String cname = rs2.getString("COLUMN_NAME");
             					System.out.println(Arrays.toString(
             						new Object[] {cname}
             					));
             					table.addCheckColumn(cname);
             				}
             				table.addCheckColumns(columnName.get(i));
             			} finally {
             				rs2.close();
             			}
             		}
             	}
            }
            // 3. 結果の表示
            while (rset.next()) {
            		String res = new String();
            		for(int i = 0; i < columnName.size(); i++) {
            			if(i != 0) res += ", ";
            			res += rset.getString(columnName.get(i));
            		}
            		System.out.println("RES" + res);


            		String res2 = new String();
            		for(int i = 0; i < checkColumnName.size(); i++) {
            			if(i != 0) res2 += ", ";
            			//res2 += rset.getString(checkColumnName.get(i));
            			res2 += rset.getString("prov_public_*");
            		}
            		System.out.println("RES" + res2);
            }
        } catch(SQLException e) {
            // 接続、SELECT文の発行でエラーが発生した場合
            e.printStackTrace();
        } finally {
            // 4.データベース接続の切断
            if (conn != null) {
                try {
                    conn.close();
                    conn = null;
                } catch (SQLException e) {
                    // データベース接続の切断でエラーが発生した場合
                    e.printStackTrace();
                }finally {
                	 System.out.println("Fin");
                	 inputQuery();
                }
            }
        }
    }
}

class TableInfo{
	private String name;
	private ArrayList<String> checkColumns;
	private String checkColumn;
	boolean check;

	public TableInfo(String s) {
		name = s;
		checkColumns = new ArrayList<String>();
		checkColumn = new String();
		check = false;
	}

	public boolean checkable() {
		return check;
	}

	public void addCheckColumn(String s) {
		//System.out.println("ADD Be");
		checkColumn = s;
		if(this.name.contains("_")) this.name.replace("_", "__");
		if(s.contains("_")) s.replace("_", "__");
		String column = "prov_public_" + this.name + "_" + s;
		checkColumns.add(column);
		//System.out.println("ADD Af");
	}

	public void addCheckColumns(String s) {
		checkColumns.add(s);
	}

	public String getTableName() {
		return name;
	}

	public String getCheckColumn() {
		return checkColumn;
	}

	public ArrayList<String> getCheckColumns(){
		return checkColumns;
	}
}
//select * from artist;
//select provenance a from x;
/*
select provenance p.name
from person p, sigmod s, y2000 y
where s.name = y.name and p.name = s.name;
*/
