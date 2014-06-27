package logic.bodyfunction;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.*;

import org.junit.*;

public class ContentsMatchTest {

	ContentsMatch sut;
	
	ArrayList<String> _body1 = new ArrayList<String>();
	ArrayList<String> _body2 = new ArrayList<String>();
	ArrayList<String> _body3 = new ArrayList<String>();
	ArrayList<String> _body4 = new ArrayList<String>();
	ArrayList<String> _body5 = new ArrayList<String>();
	ArrayList<String> _body6 = new ArrayList<String>();
	ArrayList<String> _body7 = new ArrayList<String>();
	
	ResourceBundle domBundle;

	@Before
	public void setUp() {

		String body1 = "This line is 1.";
		String body2 = "This line is 2.";
		String body3 = "This line is 3.";
		String body4 = "This line is 4.";
		String body5 = "This line is 5.";
		String check1start = "This line is check point! <if check{1}>";
		String check2start = "This line is check point! <if check{2}>";
		String checkend = "This line is check point! </if>";

		domBundle = ResourceBundle.getBundle("test");
		//普通のメール本文
		_body1.add(body1);
		_body1.add(body2);
		_body1.add(body3);

		//正しく使用されているとき
		_body2.add(body1);
		_body2.add(check1start);
		_body2.add(body2);
		_body2.add(checkend);
		_body2.add(body3);

		//始めのタグしか無い -> エラーを返す。
		_body3.add(body1);
		_body3.add(check1start);
		_body3.add(body2);
		_body3.add(body3);

		//終わりのタグしかない。 -> エラーを返す。
		_body4.add(body1);
		_body4.add(body2);
		_body4.add(checkend);
		_body4.add(body3);
		
		//ネストされているとき
		_body5.add(body1);
		_body5.add(check1start);
		_body5.add(body2);
		_body5.add(check2start);
		_body5.add(body3);
		_body5.add(checkend);
		_body5.add(body4);
		_body5.add(checkend);
		_body5.add(body5);
		
		//終わりのタグのほうが多い -> エラーを返す。
		_body6.add(body1);
		_body6.add(check1start);
		_body6.add(body2);
		_body6.add(body3);
		_body6.add(checkend);
		_body6.add(body4);
		_body6.add(checkend);
		_body6.add(body5);

		//始めのタグのほうが多い -> エラーを返す。
		_body7.add(body1);
		_body7.add(check1start);
		_body7.add(body2);
		_body7.add(check2start);
		_body7.add(body3);
		_body7.add(body4);
		_body7.add(checkend);
		_body7.add(body5);

		
		sut = new ContentsMatch();
	}

	@Test
	public void checkUseTest() throws Exception {

		assertThat(sut.checkUse(_body1), is(false));
		assertThat(sut.checkUse(_body2), is(true));
		assertThat(sut.checkUse(_body3), is(true));
		assertThat(sut.checkUse(_body4), is(false));
		assertThat(sut.checkUse(_body5), is(true));
		assertThat(sut.checkUse(_body6), is(true));
		assertThat(sut.checkUse(_body7), is(true));
	}

	@Test
	public void checkErrTest() throws Exception {

		assertThat(sut.checkErr(_body2), is(false));
		assertThat(sut.checkErr(_body3), is(true));
		assertThat(sut.checkErr(_body4), is(true));
		assertThat(sut.checkErr(_body5), is(false));
		assertThat(sut.checkErr(_body6), is(true));
		assertThat(sut.checkErr(_body7), is(true));
		
	}
				
	@Test
	public void 該当アドレスが無いとき_body2の本文編集する() throws Exception {
		ArrayList<String> expected = new ArrayList<String>();
		expected.add("This line is 1.");
		expected.add("This line is check point!");
		expected.add("This line is 3.");
		String recipient = "matt@db.ics.keio.ac.jp";
		assertThat(sut.editBody(_body2, recipient),  is(expected));
	}
	
	@Test
	public void 該当アドレスが無いとき_body5の本文編集する() throws Exception {
		ArrayList<String> expected = new ArrayList<String>();
		expected.add("This line is 1.");
		expected.add("This line is check point!");
		expected.add("This line is 5.");
		String recipient = "matt@db.ics.keio.ac.jp";
		assertThat(sut.editBody(_body5, recipient), is(expected));
	}
}
