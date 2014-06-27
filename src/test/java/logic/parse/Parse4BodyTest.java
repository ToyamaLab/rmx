package logic.parse;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.ArrayList;
import java.util.ResourceBundle;

import org.junit.*;

public class Parse4BodyTest {
	
	Parse4Body sut;
	ResourceBundle domBundle;

	@Before
	public void setUp() {

		domBundle = ResourceBundle.getBundle("test");
		sut = new Parse4Body(domBundle);
	}


	@Test
	public void ルールtestパラメータ1でSQLが返る() throws Exception {
		
		String tag = "<if test{1}>";
		
		String expectedQuery = "(select email from check where test = ?)";
		ArrayList<String> expectedPara = new ArrayList<String>();
		expectedPara.add("integer");
		expectedPara.add("1");
		sut.setParse(tag);
		assertThat(sut.getQuery(), is(expectedQuery));
		assertThat(sut.getParameter(), is(expectedPara));
		
	}
	
	@Test
	public void ルールcheckパラメータ1でSQLが返る() throws Exception {
		
		String tag = "<if check{1}>";
		
		String expectedQuery = "(select email from check where check = ?)";
		ArrayList<String> expectedPara = new ArrayList<String>();
		expectedPara.add("integer");
		expectedPara.add("1");
		sut.setParse(tag);
		assertThat(sut.getQuery(), is(expectedQuery));
		assertThat(sut.getParameter(), is(expectedPara));
	}
	
	@Test
	public void ルールtest2パラメータtestでSQLが返る() throws Exception {
		String tag = "<if test2{test}>";
		
		String expectedQuery = "(select email from test2 where test2 = ?)";
		ArrayList<String> expectedPara = new ArrayList<String>();
		expectedPara.add("string");
		expectedPara.add("test");
		sut.setParse(tag);
		assertThat(sut.getQuery(), is(expectedQuery));
		assertThat(sut.getParameter(), is(expectedPara));
	}
	
	@Test //現時点で失敗
	public void ルールtestパラメータ1と2でSQLが返る() throws Exception {
		String tag = "<if test{1}>";
		
		String expectedQuery = "(select email from check where test >= ? and test <= ?)";
		ArrayList<String> expectedPara = new ArrayList<String>();
		expectedPara.add("integer");
		expectedPara.add("1");
		expectedPara.add("integer");
		expectedPara.add("2");
		sut.setParse(tag);
		assertThat(sut.getQuery(), is(expectedQuery));
		assertThat(sut.getParameter(), is(expectedPara));

	}

}
