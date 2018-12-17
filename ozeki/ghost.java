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
    		   }
    	   }
    	   flag = false;
    	   connectpsql(query);
    }

    public static String getTableName(String s) {
    		System.out.print("-- getTableName -- column name " + s);
		String name = s.substring(12, s.indexOf("_", 13));
		System.out.println("　-->  table name " + name + "--");
    		return name;
    }

    public static String getColumnName(String s) {
    		System.out.print("-- getColumnName -- column name " + s);
    		int last = s.lastIndexOf("_");
    		int next = s.lastIndexOf("__");
    		if(last - next <= 1) {
    			last = s.lastIndexOf("_", next - 1);
    		}
    		String name = s.substring(last + 1, s.length());
    		System.out.println("　-->  column name " + name + "--");
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
            ArrayList<String> normalColumn = new ArrayList<String>();
       		ArrayList<ArrayList<String>> corrects = new ArrayList<ArrayList<String>>();
       		ArrayList<ArrayList<String>> differents = new ArrayList<ArrayList<String>>();
       		ArrayList<Integer> ci = new ArrayList<Integer>();
       		ArrayList<DifferentRow> di = new ArrayList<DifferentRow>();

       		int normal = - 1;



            for(int i = 0; i < rset.getMetaData().getColumnCount(); i++) {
            	System.out.println("Column No : " + i + " / " + rset.getMetaData().getColumnCount());
            		columnName.add(rset.getMetaData().getColumnName(i + 1));
             	System.out.println("ColumnName --- " + columnName.get(i));
             	if(columnName.get(i).contains("prov_public_")) {
             		if(normal == -1)normal = i;
             		String tableName = getTableName(columnName.get(i));
             		if(isExist(tables, tableName)) {
             			System.out.println("TABLE " + tableName + " is Exist");
             			int tableIndex = getTableIndex(tables, tableName);
             			if(tableIndex < 0) System.out.println("Not correct tableIndex");
             			if(columnName.get(i).matches(".*[_]+[0-9]+[_]+.*")) {
             				//System.out.println("MATCH");
             				String cname = getColumnName(columnName.get(i));
             				//System.out.println(cname);
             				for(int j = 0; j < tables.get(tableIndex).getCheckColumn().size(); j++) {
             					if(cname.equals(tables.get(tableIndex).getCheckColumn().get(j))) {
                 					System.out.println("MATCH with Check Column");
                 					tables.get(tableIndex).setTrue();
                 					tables.get(tableIndex).addCheckColumns(columnName.get(i));
                 				}
             				}
             				int x = tables.get(tableIndex).checksIndex(cname);
             				if(x != -1) {
             					tables.get(tableIndex).addCheck(x, columnName.get(i));
             				}
             			}
             		} else {
             			System.out.print("TABLE " + tableName + " is NOT Exist. -- ");
             			TableInfo table = new TableInfo(tableName);
             			tables.add(table);
             			DatabaseMetaData dbmd = conn.getMetaData();
             			ResultSet rs2 = dbmd.getBestRowIdentifier(null, conn.getSchema(), tableName, 0, true);
             			try {
             				while (rs2.next()) {
             					String cname = rs2.getString("COLUMN_NAME");
             					System.out.print("Indentifier column(s) --->");
             					System.out.println(Arrays.toString(
             						new Object[] {cname}
             					));
             					table.addCheckColumn(cname);
             					table.addNewCheck(cname, columnName.get(i));
             				}
             				table.addCheckColumns(columnName.get(i));
             				//table.addCheck(0, columnName.get(i));
             			} finally {
             				rs2.close();
             			}
             			if(table.getCheckColumn().isEmpty()) {
             				rs2 = dbmd.getColumns(null, conn.getSchema(), tableName, "%");
             				while(rs2.next()) {
             					table.addCheckColumn(rs2.getString("COLUMN_NAME"));
             					table.addNewCheck(rs2.getString("COLUMN_NAME"), columnName.get(i));
             				}
             				System.out.println("Identifier column names --- " + table.getCheckColumn());
             			}
             		}
             	}else {
             		normalColumn.add(columnName.get(i));
             	}
            }
            //----ここまでカラムからテーブル情報（TableInfo）を作成・記入

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
            //----ここまでどこを検査すればいいかを一列に整列　→　indexで管理してしまいたい
            System.out.println("----checks----");
            for(int i = 0; i < tables.size(); i++) {
            		if(tables.get(i).checkable()) {
            			System.out.println(new Object[]{tables.get(i).getChecks()});
            		}else continue;
            }
            //----ここまで確認用

            //-----For Debug---------------------------------------
            System.out.println("---- Check Column Name ----");
            for(int i = 0; i < checkColumnName.size(); i++) {
            		System.out.print(checkColumnName.get(i) + " , ");
            }
            //-----------------------------------------------------
            System.out.println("");
            // 3. 結果の表示
            int correctCount = 0;
            int differentCount = 0;
            int index = 0;
            while (rset.next()) {
            		boolean dif = false;
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
            		}


            		for(int i = 0; i < tables.size(); i++) {
            			//Tableからどれが検査項目かを持ってくる
            			ArrayList<ArrayList<String>> checks = tables.get(i).getChecks();
            			//検査項目ごとに（同じカラムごとに）
            			for(int j = 0; j < checks.size(); j++) {
            				String res2 = new String();
                			String res3 = new String();
                			ArrayList<String> checker = new ArrayList<String>();
                			ArrayList<Integer> intChecker = new ArrayList<Integer>();
                			//検査用に同じカラムごとに一旦配列を作る
            				for(int k = 1; k < checks.get(j).size(); k++) {
            					if(k != 1) res2 += ", ";
                				if(k != 1) res3 += ", ";
                				res2 += rset.getString(checks.get(j).get(k));
                				checker.add(rset.getString(checks.get(j).get(k)));
                				res3 += checks.get(j).get(k);
            				}
            				System.out.println("RES3 (" + res3 + ")  (" + res2 + ")");
            				for(int k = 1; k < checker.size(); k++) {
            					if(!checker.get(k).equals(checker.get(0))) {
            						System.out.println("Column " + checks.get(j).get(0) + " has different provenance");
            						dif = true;
            					}
            				}
            			}
            		}
        			if(dif) {
        				ArrayList<String> ar = new ArrayList<String>();
        				//differentCount++;
        				for(int k = 0 ; k < normalColumn.size(); k++) {
        					ar.add(rset.getString(normalColumn.get(k)));
        				}
        				differents.add(ar);
        			}else{
        				//correctCount++;
        				ArrayList<String> ar = new ArrayList<String>();
        				//differentCount++;
        				for(int k = 0 ; k < normalColumn.size(); k++) {
        					ar.add(rset.getString(normalColumn.get(k)));

        				}
        				corrects.add(ar);
        			}
        			index++;
            }
            if(differents.size() < 1) {
            		for(int i = 0; i < normalColumn.size(); i++) {
            			if(i != 0) System.out.print(", ");
            			System.out.print(normalColumn.get(i));
            		}
            		System.out.println();
            		for(int i = 0; i < corrects.size(); i++) {
            			for(int j = 0; j < corrects.get(i).size(); j++) {
            				if(j != 0) System.out.print(", ");
                			System.out.print(corrects.get(i).get(j));
            			}
            			System.out.println();
            		}
            }else {
            		System.out.println("Correct column : " + corrects.size());
            		for(int i = 0; i < corrects.size(); i++) {
            			if(i != 0) System.out.print(", ");
            			System.out.print("{");
            			for(int j = 0 ; j < corrects.get(i).size(); j++) {
            				if(j != 0) System.out.print(", ");
            				System.out.print(corrects.get(i).get(j));
            			}
            			System.out.print("}");
            		}
            		System.out.println();
            		System.out.println("Different column : " + differents.size());
            		for(int i = 0; i < differents.size(); i++) {
            			if(i != 0) System.out.print(", ");
            			System.out.print("{");
            			for(int j = 0 ; j < differents.get(i).size(); j++) {
            				if(j != 0) System.out.print(", ");
            				System.out.print(differents.get(i).get(j));
            			}
            			System.out.print("}");
            		}
            		System.out.println();
            		System.out.println("Look Correct rows' provenances : Input 'c'");
            		System.out.println("Look Different rows' all provenances : Input 'd'");
            		System.out.println("Look Different rows' only different column provenances : Input 'd'");
            		System.out.println("Look all rows' provenances : Input 'a'");
            		System.out.println("Look provenances column name : Input 'p'");
            		System.out.println("Finish this query : Input 'q'");
            		Scanner s = new Scanner(System.in);
            		while(s.hasNext()) {
            			String input = s.next();
            			if(input.equals("c")) {

            			}
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
class DifferentRow{
	private int index;
	private ArrayList<ArrayList<Integer>> dColumns;

	public DifferentRow(int i, ArrayList<ArrayList<Integer>> d) {
		index = i;
		dColumns = d;
	}

	public int getIndex() {
		return index;
	}

	public ArrayList<ArrayList<Integer>> getDColumns(){
		return dColumns;
	}
}

class TableInfo{
	private String name;
	private ArrayList<String> checkColumns;
	private ArrayList<String> checkColumn;
	private ArrayList<ArrayList<String>> checks;
	private boolean check;

	private ArrayList<ArrayList<Integer>> intChecks;

	public TableInfo(String s) {
		name = s;
		checkColumns = new ArrayList<String>();
		checkColumn = new ArrayList<String>();
		check = false;
		checks= new ArrayList<ArrayList<String>>();

		intChecks = new ArrayList<ArrayList<Integer>>();
	}

	public void seeAll() {
		System.out.println("Name : " + name);
		System.out.println("checkcolumn : " + checkColumn);
		System.out.println("checkcolumns : " + checkColumns);
		System.out.println("check : " + check);
		System.out.println("checks : " + checks);
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

	public ArrayList<ArrayList<String>> getchecks(){
		return checks;
	}

	public void addNewCheck(String s, String c) {
		ArrayList<String> n = new ArrayList<String>();
		if(s.contains("_")) {
			System.out.println("contain _");
			s = s.replace("_", "__");
			System.out.println(s);
		}
		n.add(s);
		n.add("prov_public_" + name + "_" + s);
		checks.add(n);
	}

	public int checksIndex(String s) {
		for(int i = 0; i < checks.size(); i++) {
			if(s.equals(checks.get(i).get(0))) {
				return i;
			}
		}
		return -1;
	}

	public void addCheck(int x, String s) {
		checks.get(x).add(s);
	}

	public ArrayList<ArrayList<String>> getChecks(){
		return checks;
	}
}
//select * from artist;
//select provenance a from x;
/*
select provenance p.name
from person p, sigmod s, y2000 y
where s.name = y.name and p.name = s.name;

select provenance s.name, p.sex
from sigmod s, y2000 y, person p
where s.name = y.name and s.name = p.name;


*/
/*
 name
 prov_public_person_id  prov_public_person_1_id  prov_public_person_2_id
 prov_public_conference_id  prov_public_conference_1_id
 prov_public_attend_p__id prov_public_attend_1_p__id
 prov_public_attend_c__id  prov_public_attend_1_c__id

 prov_public_person_name
 prov_public_person_sex

 prov_public_person_1_name
 prov_public_person_1_sex

 prov_public_conference_name
 prov_public_conference_year

 prov_public_person_2_name
 prov_public_person_2_sex

 prov_public_conference_1_name
 prov_public_conference_1_year
*/
