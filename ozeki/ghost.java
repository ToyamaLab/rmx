
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
    			   if(tmp.equals("exit")) return;
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
    		System.out.println("---getTableName ----- column name " + s);
		String name = s.substring(12, s.indexOf("_", 13));
		System.out.println("table name " + name + "------------");
    		return name;
    }

    public static String getColumnName(String s) {
    		int last = s.lastIndexOf("_");
    		int next = s.lastIndexOf("__");
    		if(last - next <= 1) {
    			last = s.lastIndexOf("_", next - 1);
    		}
    		String name = s.substring(last + 1, s.length());
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
             	System.out.println("ColumnName --- " + columnName.get(i));
             	if(columnName.get(i).contains("prov_public_")) {
             		String tableName = getTableName(columnName.get(i));
             		if(isExist(tables, tableName)) {
             			System.out.println(tableName + " is Exist");
             			int tableIndex = getTableIndex(tables, tableName);
             			if(tableIndex < 0) System.out.println("Not correct tableIndex");
             			if(columnName.get(i).matches(".*[_]+[0-9]+[_]+.*")) {
             				System.out.println("MATCH");
             				String cname = getColumnName(columnName.get(i));
             				System.out.println(cname);
             				for(int j = 0; j < tables.get(tableIndex).getCheckColumn().size(); j++) {
             					if(cname.equals(tables.get(tableIndex).getCheckColumn().get(j))) {
                 					System.out.println("MATCH2");
                 					tables.get(tableIndex).setTrue();
                 					tables.get(tableIndex).addCheckColumns(columnName.get(i));
                 				}
             				}
             			}
             		} else {
             			System.out.println(tableName + " is NOT Exist");
             			TableInfo table = new TableInfo(tableName);
             			tables.add(table);
             			DatabaseMetaData dbmd = conn.getMetaData();
             			ResultSet rs2 = dbmd.getBestRowIdentifier(null, conn.getSchema(), tableName, 0, true);
             			try {
             				while (rs2.next()) {
             					String cname = rs2.getString("COLUMN_NAME");
             					System.out.print("Indentifier column name ---");
             					System.out.println(Arrays.toString(
             						new Object[] {cname}
             					));
             					table.addCheckColumn(cname);
             				}
             				table.addCheckColumns(columnName.get(i));
             			} finally {
             				rs2.close();
             			}
             			if(table.getCheckColumn().isEmpty()) {
             				rs2 = dbmd.getColumns(null, conn.getSchema(), tableName, "%");
             				while(rs2.next()) {
             					table.addCheckColumn(rs2.getString("COLUMN_NAME"));
             				}
             				System.out.println("Identifier column names --- " + table.getCheckColumn());
             			}
             		}
             	}
            }

        		System.out.println("Tables Size : " + tables.size());
            for(int i = 0; i < tables.size(); i++) {
            		tables.get(i).seeAll();
            		if(tables.get(i).checkable()) {
            			System.out.println(tables.get(i).getCheckColumns());
            			ArrayList<String> columns = tables.get(i).getCheckColumns();
            			System.out.println("columns : " + columns);
            			for(int j = 0; j < columns.size(); j++) {
                			checkColumnName.add(columns.get(j));
                		}
            		}else continue;
            }

            //-----For Debug---------------------------------------
            System.out.println("---- Check Column Name ----");
            for(int i = 0; i < checkColumnName.size(); i++) {
            		System.out.print(checkColumnName.get(i) + " , ");
            }
            //-----------------------------------------------------
            System.out.println("");
            // 3. 結果の表示
            while (rset.next()) {
            		String res = new String();
            		for(int i = 0; i < columnName.size(); i++) {
            			if(i != 0) res += ", ";
            			res += rset.getString(columnName.get(i));
            		}
            		System.out.println("RES " + res);



            		for(int i = 0; i < tables.size(); i++) {
            			String res2 = new String();
            			String res3 = new String();
            			ArrayList<String> checks = tables.get(i).getCheckColumns();
            			for(int j = 0; j < checks.size(); j++) {
            				if(j != 0) res2 += ", ";
            				if(j != 0) res3 += ", ";
            				res2 += rset.getString(checks.get(j));
            				res3 += checks.get(j);
            			}
            				System.out.println("RES2 (" + res3 + ")  (" + res2 + ")");

            			//res2 += rset.getString("prov_public_*");
            		}

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
	private ArrayList<String> checkColumn;
	private boolean check;

	public TableInfo(String s) {
		name = s;
		checkColumns = new ArrayList<String>();
		checkColumn = new ArrayList<String>();
		check = false;
	}

	public void seeAll() {
		System.out.println("Name : " + name);
		System.out.println("checkcolumn : " + checkColumn);
		System.out.println("checkcolumns : " + checkColumns);
		System.out.println("check : " + check);
	}

	public boolean checkable() {
		return check;
	}

	public void setTrue() {
		check = true;
	}

	public void addCheckColumn(String s) {
		//System.out.println("ADD Be");

		//if(this.name.contains("_")) this.name.replace("_", "__");
		if(s.contains("_")) {
			System.out.println("contain _");
			s = s.replace("_", "__");
			System.out.println(s);
		}
		checkColumn.add(s);
		String column = "prov_public_" + this.name + "_" + s;
		//checkColumns.add(column);
		//System.out.println("ADD Af");
	}

	public void addCheckColumns(String s) {
		checkColumns.add(s);
	}

	public String getTableName() {
		return name;
	}

	public ArrayList<String> getCheckColumn() {
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
