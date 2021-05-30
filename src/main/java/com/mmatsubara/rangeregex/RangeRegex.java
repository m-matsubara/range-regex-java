package com.mmatsubara.rangeregex;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RangeRegex {
	public static String boundedRegexForRange(long min_, long max_)
	{
		return boundedRegexForRange(min_, max_, false);
	}

	public static String boundedRegexForRange(long min_, long max_, boolean pre0)
	{
		//return r'\b({})\b'.format(regex_for_range(min_, max_))
		return String.format("\\b(%s)\\b", regexForRange(min_, max_, pre0));
	}

	public static String regexForRange(long min_, long max_)
	{
		return regexForRange(min_, max_, false);
	}

	public static String regexForRange(long min_, long max_, boolean pre0)
	{
		// matsubara Delphi version original specification
		String pre0Str = pre0 ? "0*" : "";

	    /*
	    > regex_for_range(12, 345)
	    '1[2-9]|[2-9]\d|[1-2]\d{2}|3[0-3]\d|34[0-5]'
	    */

	    /*
	    positive_subpatterns = []
	    negative_subpatterns = []
	    */
		List<String> negative_subpatterns;
		List<String> positive_subpatterns;

		/*
		if min_ < 0:
		  min__ = 1
		  if max_ < 0:
		    min__ = abs(max_)
		  max__ = abs(min_)

		  negative_subpatterns = split_to_patterns(min__, max__)
		  min_ = 0
		*/
		if (min_ < 0) {
			long min__ = 1;
			if (max_ < 0)
				min__ = Math.abs(max_);
			long max__ = Math.abs(min_);

			negative_subpatterns = splitToPatterns(min__, max__);
			min_ = 0;
		} else {
			negative_subpatterns = new ArrayList<String>();
		}

	    /*
		if max_ >= 0:
		  positive_subpatterns = split_to_patterns(min_, max_)
		*/
		if (max_ >= 0)
			positive_subpatterns = splitToPatterns(min_, max_);
		else
			positive_subpatterns = new ArrayList<String>();

		//negative_only_subpatterns = ['-' + val for val in negative_subpatterns if val not in positive_subpatterns]
		List<String> negative_only_subpatterns = new ArrayList<String>();
		for (var val: negative_subpatterns) {
			if (positive_subpatterns.contains(val) == false)
				negative_only_subpatterns.add("-" + pre0Str + val);
		}

		//positive_only_subpatterns = [val for val in positive_subpatterns if val not in negative_subpatterns]
		List<String> positive_only_subpatterns = new ArrayList<String>();
		for (var val: positive_subpatterns) {
			if (negative_subpatterns.contains(val) == false)
				positive_only_subpatterns.add(pre0Str + val);
		}

		/*
		intersected_subpatterns = ['-?' + val for val in negative_subpatterns if val in positive_subpatterns]

		subpatterns = negative_only_subpatterns + intersected_subpatterns + positive_only_subpatterns
		*/
		var subpatterns = new ArrayList<String>();

		subpatterns.addAll(negative_only_subpatterns);
		for (var val: negative_subpatterns) {
			if (positive_subpatterns.contains(val))
				subpatterns.add("-?" + pre0Str + val);
		}
		subpatterns.addAll(positive_only_subpatterns);

		//return '|'.join(subpatterns)
		return String.join("|", subpatterns);
	}


	public static List<String> splitToPatterns(long min_, long max_) {
    	//subpatterns = []
		List<String> subpatterns = new ArrayList<String>();

		//start = min_
		long start = min_;

		/*
		for stop in split_to_ranges(min_, max_):
		  subpatterns.append(range_to_pattern(start, stop))
		  start = stop + 1
		*/
		List<Long> ranges = splitToRanges(min_, max_);
		for (long stop: ranges) {
			subpatterns.add(rangeToPattern(start, stop));
			start = stop + 1;
		}

		//return subpatterns
		return subpatterns;
	}


	public static List<Long> splitToRanges(long min_, long max_) {
		//stops = {max_}
		List<Long> stops = new ArrayList<Long>();
		stops.add(max_);

		//nines_count = 1
		int nines_count = 1;

		//stop = fill_by_nines(min_, nines_count)
		long stop = fillByNines(min_, nines_count);

		/*
		while min_ <= stop < max_:
		  stops.add(stop)

		nines_count += 1
		stop = fill_by_nines(min_, nines_count)
		*/
		while (min_ <= stop && stop < max_) {
			if (stops.contains(stop) == false)
				stops.add(stop);

			nines_count++;
			stop = fillByNines(min_, nines_count);
		}

		//zeros_count = 1
		long zeros_count = 1;
		//stop = fill_by_zeros(max_ + 1, zeros_count) - 1
		stop = fillByZeros(max_ + 1, zeros_count) - 1;
		/*
		while min_ < stop <= max_:
			stops.add(stop)

			zeros_count += 1
			stop = fill_by_zeros(max_ + 1, zeros_count) - 1
		*/
		while (min_ < stop && stop <= max_) {
			if (stops.contains(stop) == false)
				stops.add(stop);

			zeros_count++;
			stop = fillByZeros(max_ + 1, zeros_count) - 1;
		}

		//stops = list(stops)
		//stops.sort()
		stops.sort(new Comparator<Long>() {
			public int compare(Long o1, Long o2) {
				if (o1 < o2)
					return -1;
				else if (o1 > o2)
					return 1;
				else
					return 0;
			}
		});

		//return stops
		return stops;
	}

	public static long fillByNines(long Integer_, int nines_count) {
		//return int(str(Int64_)[:-nines_count] + '9' * nines_count)
		String str = String.valueOf(Integer_);

		return Long.valueOf(
				str.substring(0, str.length() - nines_count >= 0 ? str.length() - nines_count : 0)
				+ "99999999999999999999".substring(0, nines_count)
		);
	}


	public static long fillByZeros(long Integer_, long zeros_count) {
		//return Int64 - Int64 % 10 ** zeros_count
		long pow = 1;
		for (var idx = 0; idx < zeros_count; idx++)
			pow = pow * 10;
		return Integer_ - Integer_ % pow;
	}


	public static String rangeToPattern(long start, long stop) {
		//pattern = ''
		//any_digit_count = 0
		String pattern = "";
		long any_digit_count = 0;

		/*
		for start_digit, stop_digit in zip(str(start), str(stop)):
		  if start_digit == stop_digit:
		    pattern += start_digit
		  elif start_digit != '0' or stop_digit != '9':
		    pattern += '[{}-{}]'.format(start_digit, stop_digit)
		  else:
		    any_digit_count += 1
		*/
		String str_start = String.valueOf(start);
		String str_stop = String.valueOf(stop);
		int str_start_len = str_start.length();
		int str_stop_len  = str_stop.length();
		int len = (str_start_len < str_stop_len) ? str_start_len : str_stop_len;

		for (int idx = 0; idx < len; idx++) {
			char start_digit = str_start.charAt(idx);
			char stop_digit = str_stop.charAt(idx);
			if (start_digit == stop_digit)
				pattern = pattern + start_digit;
			else if (start_digit != '0' || stop_digit != '9')
				pattern = pattern + String.format("[%s-%s]", start_digit, stop_digit);
			else
				any_digit_count++;
		}

		/*
		if any_digit_count:
		  pattern += r'\d'
		*/
		if (any_digit_count > 0)
			pattern = pattern + "\\d";

		/*
		if any_digit_count > 1:
		  pattern += '{{{}}}'.format(any_digit_count)
		*/
		if (any_digit_count > 1)
			pattern = pattern + String.format("{%d}", any_digit_count);

		//return pattern
		return pattern;
	}


	public static void main(String[] argv) {
		if (argv.length < 2) {
			System.err.println("argument error.");
			return;
		}

		long min = Long.parseLong(argv[0]);
		long max = Long.parseLong(argv[1]);
    	System.out.println(RangeRegex.regexForRange(min, max));
	}
}
