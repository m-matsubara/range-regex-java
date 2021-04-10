package com.mmatsubara.rangeregex;

import static org.junit.Assert.*;

import java.util.regex.Pattern;

import org.junit.Test;

public class RangeRegexTest {
	public static void verifyRange(String regex, long min_, long max_, long from_min_, long to_max_) {
		for (long nr = from_min_; nr <= to_max_; nr++) {
			if (min_ <= nr && nr <= max_)
				assertTrue(Pattern.matches(regex, String.valueOf(nr)));
			else
				assertFalse(Pattern.matches(regex, String.valueOf(nr)));
		}
    }


	@Test public void testQuality() {
		assertTrue("Failure: RangeRegex.regexForRange(1, 1)", RangeRegex.regexForRange(1, 1).equals("1"));
		assertTrue("Failure: RangeRegex.regexForRange(0, 1)", RangeRegex.regexForRange(0, 1).equals("[0-1]"));
		assertTrue("FailureRangeRegex.regexForRange(-1, -1)", RangeRegex.regexForRange(-1, -1).equals("-1"));
		assertTrue("Failure: RangeRegex.regexForRange(-1, 0)", RangeRegex.regexForRange(-1, 0).equals("-1|0"));
		assertTrue("Failure: RangeRegex.regexForRange(-1, 1)", RangeRegex.regexForRange(-1, 1).equals("-1|[0-1]"));
		assertTrue("FailureRangeRegex.regexForRange(-4, -2)", RangeRegex.regexForRange(-4, -2).equals("-[2-4]"));
		assertTrue("Failure: RangeRegex.regexForRange(-3, 1)", RangeRegex.regexForRange(-3, 1).equals("-[1-3]|[0-1]"));
		assertTrue("Failure: RangeRegex.regexForRange(-2, 0)", RangeRegex.regexForRange(-2, 0).equals("-[1-2]|0"));
		assertTrue("Failure: RangeRegex.regexForRange(0, 2)", RangeRegex.regexForRange(0, 2).equals("[0-2]"));
		assertTrue("Failure: RangeRegex.regexForRange(-1, 3)", RangeRegex.regexForRange(-1, 3).equals("-1|[0-3]"));
		assertTrue("Failure: RangeRegex.regexForRange(65666, 65667)", RangeRegex.regexForRange(65666, 65667).equals("6566[6-7]"));
		assertTrue("Failure: RangeRegex.regexForRange(12, 3456)", RangeRegex.regexForRange(12, 3456).equals("1[2-9]|[2-9]\\d|[1-9]\\d{2}|[1-2]\\d{3}|3[0-3]\\d{2}|34[0-4]\\d|345[0-6]"));
		assertTrue("Failure: RangeRegex.regexForRange(1, 19)", RangeRegex.regexForRange(1, 19).equals("[1-9]|1\\d"));
		assertTrue("Failure: RangeRegex.regexForRange(1, 99)", RangeRegex.regexForRange(1, 99).equals("[1-9]|[1-9]\\d"));
	}

	@Test public void testOptimization() {
		assertTrue("RangeRegEx.regexForRange(-9, 9)", RangeRegex.regexForRange(-9, 9).equals("-[1-9]|\\d"));
		assertTrue("RangeRegEx.regexForRange(-19, 19)", RangeRegex.regexForRange(-19, 19).equals("-[1-9]|-?1\\d|\\d"));
		assertTrue("RangeRegEx.regexForRange(-29, 29)", RangeRegex.regexForRange(-29, 29).equals("-[1-9]|-?[1-2]\\d|\\d"));
		assertTrue("RangeRegEx.regexForRange(-99, 99)", RangeRegex.regexForRange(-99, 99).equals("-[1-9]|-?[1-9]\\d|\\d"));
		assertTrue("RangeRegEx.regexForRange(-999, 999)", RangeRegex.regexForRange(-999, 999).equals("-[1-9]|-?[1-9]\\d|-?[1-9]\\d{2}|\\d"));
		assertTrue("RangeRegEx.regexForRange(-9999, 9999)", RangeRegex.regexForRange(-9999, 9999).equals("-[1-9]|-?[1-9]\\d|-?[1-9]\\d{2}|-?[1-9]\\d{3}|\\d"));
	}

	@Test public void test_equal() {
		String regex = RangeRegex.boundedRegexForRange(1, 1);
		verifyRange(regex, 1, 1, 0, 100);
	}

	@Test public void test_equal_2() {
		String regex = RangeRegex.boundedRegexForRange(65443, 65443);
		verifyRange(regex, 65443, 65443, 65000, 66000);
	}

	@Test public void test_equal_3() {
		String regex = RangeRegex.boundedRegexForRange(192, 100020000300000L);
		verifyRange(regex,                              192, 100020000300000L, 0, 1000);
		verifyRange(regex,                              192, 100020000300000L, 100019999200000L, 100020000400000L);
	}

	@Test public void test_repeated_digit() {
		String regex = RangeRegex.boundedRegexForRange(10331, 20381);
		verifyRange(regex,                              10331, 20381, 0, 99999);
	}

	@Test public void test_repeated_zeros() {
		String regex = RangeRegex.boundedRegexForRange(10031, 20081);
		verifyRange(regex,                              10031, 20081, 0, 99999);
	}

	@Test public void test_zero_one() {
		String regex = RangeRegex.boundedRegexForRange(10301, 20101);
		verifyRange(regex,                              10301, 20101, 0, 99999);
	}

	@Test public void test_different_len_numbers_1() {
		String regex = RangeRegex.boundedRegexForRange(1030, 20101);
		verifyRange(regex,                              1030, 20101, 0, 99999);
	}

	@Test public void test_repetead_one() {
		String regex = RangeRegex.boundedRegexForRange(102, 111);
		verifyRange(regex,                              102, 111, 0, 1000);
	}

	@Test public void test_small_diff_1() {
		String regex = RangeRegex.boundedRegexForRange(102, 110);
		verifyRange(regex,                              102, 110, 0, 1000);
	}

	@Test public void test_small_diff_2() {
		String regex = RangeRegex.boundedRegexForRange(102, 130);
		verifyRange(regex,                              102, 130, 0, 1000);
	}

	@Test public void test_random_range_1() {
		String regex = RangeRegex.boundedRegexForRange(4173, 7981);
		verifyRange(regex,                              4173, 7981, 0, 99999);
	}

	@Test public void test_one_digit_numbers() {
		String regex = RangeRegex.boundedRegexForRange(3, 7);
		verifyRange(regex,                              3, 7, 0, 99);
	}

	@Test public void test_one_digit_at_bounds() {
		String regex = RangeRegex.boundedRegexForRange(1, 9);
		verifyRange(regex,                              1, 9, 0, 1000);
	}

	@Test public void test_power_of_ten() {
		String regex = RangeRegex.boundedRegexForRange(1000, 8632);
		verifyRange(regex,                              1000, 8632, 0, 99999);
	}

	@Test public void test_different_len_numbers_2() {
		String regex = RangeRegex.boundedRegexForRange(13, 8632);
		verifyRange(regex,                              13, 8632, 0, 10000);
	}

	@Test public void test_different_len_numbers_small_diff() {
		String regex = RangeRegex.boundedRegexForRange(9, 11);
		verifyRange(regex,                              9, 11, 0, 100);
	}

	@Test public void test_different_len_zero_eight_nine() {
		String regex = RangeRegex.boundedRegexForRange(90, 980099);
		verifyRange(regex,                              90, 980099, 0, 999999);
	}

	@Test public void test_small_diff() {
		String regex = RangeRegex.boundedRegexForRange(19, 21);
		verifyRange(regex,                              19, 21, 0, 100);
	}

	@Test public void test_different_len_zero_one_nine() {
		String regex = RangeRegex.boundedRegexForRange(999, 10000);
		verifyRange(regex,                              999, 10000, 1, 20000);
	}

	// matsubara Delphi version original specification
	@Test public void testAddSpecification() {
		assertEquals(RangeRegex.regexForRange(-999, 999, true), "-0*[1-9]|-?0*[1-9]\\d|-?0*[1-9]\\d{2}|0*\\d");
		assertEquals(RangeRegex.regexForRange(-9999, 9999, true), "-0*[1-9]|-?0*[1-9]\\d|-?0*[1-9]\\d{2}|-?0*[1-9]\\d{3}|0*\\d");

		String regex = RangeRegex.regexForRange(-15, 99, true);
		for (int idx = 1; idx <= 15; idx++) {
			assertTrue(Pattern.matches(regex, String.format("-%d", idx)));
			assertTrue(Pattern.matches(regex, String.format("-0%d", idx)));
			assertTrue(Pattern.matches(regex, String.format("-00%d", idx)));
			assertTrue(Pattern.matches(regex, String.format("-000%d", idx)));
		}
		assertTrue(Pattern.matches(regex, "0"));
		assertTrue(Pattern.matches(regex, "00"));
		assertTrue(Pattern.matches(regex, "000"));
		for (int idx = 1; idx <= 99; idx++) {
			assertTrue(Pattern.matches(regex, String.format("%d", idx)));
			assertTrue(Pattern.matches(regex, String.format("0%d", idx)));
			assertTrue(Pattern.matches(regex, String.format("00%d", idx)));
			assertTrue(Pattern.matches(regex, String.format("000%d", idx)));
		}
	}

}
