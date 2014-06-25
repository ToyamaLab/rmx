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
	public void ルールtestパラメータ1でSQLが返る() {
		
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
	public void ルールcheckパラメータ1でSQLが返る() {
		
		String tag = "<if check{1}>";
		
		String expectedQuery = "(select email from check where check = ?)";
		ArrayList<String> expectedPara = new ArrayList<String>();
		expectedPara.add("integer");
		expectedPara.add("1");
		sut.setParse(tag);
		assertThat(sut.getQuery(), is(expectedQuery));
		assertThat(sut.getParameter(), is(expectedPara));
	}
	

}
