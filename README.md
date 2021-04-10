# Java numeric range regular expression generator

## Summary
The following is a module translated to work with Java.  
https://github.com/voronind/range-regex  

## Usage

```Java
import com.mmatsubara.rangeregex.RangeRegex;

//…

  String regexStr = RangeRegex.regexForRange(12, 34);
  // nenerates: "1[2-9]|2\d|3[0-4]"
```  
