import java.util.regex.Pattern;

// Word stemmer, utilizes Porter's Algorithm
public class Stemmer {
	private static Pattern containsVowelPattern = Pattern.compile("[a-z]*[aeiouy][a-z]*");
	
	public static String stem(String word) {
		word = word.toLowerCase().trim(); // remove capitalization and whitespace
		
		// if word contains non-alphabetical characters, return it without stemming
		if(!Pattern.matches("[a-z]*", word))
			return word;
		
		int m = countM(word);
		
		// step 1a
		
		if(checkSuffix(word, "sses")) {
			word = replaceSuffix(word, "ss", "sses");
		} else if(checkSuffix(word, "ies")) {
			word = replaceSuffix(word, "ies", "i");
		} else if(checkSuffix(word, "s")) {
			if(checkSuffix(word, "ss")) {
				word = replaceSuffix(word, "ss", "s");
			} else {
				word = replaceSuffix(word, "s", "");
			}
			// extension of 1a if it ends in 's' or 'ss'
			if(checkSuffix(word, "at")) {
				word = replaceSuffix(word, "at", "ate");
			} else if(checkSuffix(word, "bl")) {
				word = replaceSuffix(word, "bl", "ble");
			} else if(m == 1 && endsCVC(word)) {
				word = word + "e";
			}
		}
		
		m = countM(word);
		
		// step 1b
		
		if(m > 0 && checkSuffix(word, "eed")) {
			word = replaceSuffix(word, "eed", "ee");
		} else if(checkSuffix(word, "ed") && stemContainsVowel(word, "ed")) {
			word = replaceSuffix(word, "ed", "");
		} else if(checkSuffix(word, "ing") && stemContainsVowel(word, "ing")) {
			word = replaceSuffix(word, "ing", "");
		}
		
		m = countM(word);
		 
		// step 1c
		
		if(checkSuffix(word, "y") && stemContainsVowel(word, "y")) {
			word = replaceSuffix(word, "y", "i");
		}
		
		m = countM(word);
		
		// step 2
		// all parts of step 2 require m>0, so just check once
		
		if(m > 0) {
			if(checkSuffix(word,"ational")) {
				word = replaceSuffix(word, "ational", "ate");
			} else if(checkSuffix(word, "tional")) {
				word = replaceSuffix(word, "tional", "tion");
			} else if(checkSuffix(word, "enci")) {
				word = replaceSuffix(word, "enci", "ence");
			} else if(checkSuffix(word, "anci")) {
				word = replaceSuffix(word, "anci", "ance");
			} else if(checkSuffix(word, "izer")) {
				word = replaceSuffix(word, "izer", "ize");
			} else if(checkSuffix(word, "abli")) {
				word = replaceSuffix(word, "abli", "able");
			} else if(checkSuffix(word, "alli")) {
				word = replaceSuffix(word, "alli", "al");
			} else if(checkSuffix(word, "entli")) {
				word = replaceSuffix(word, "entli", "ent");
			} else if(checkSuffix(word, "eli")) {
				word = replaceSuffix(word, "eli", "e");
			} else if(checkSuffix(word, "ousli")) {
				word = replaceSuffix(word, "ousli", "ous");
			} else if(checkSuffix(word, "ization")) {
				word = replaceSuffix(word, "ization", "ize");
			} else if(checkSuffix(word, "ation")) {
				word = replaceSuffix(word, "ation", "ate");
			} else if(checkSuffix(word, "ator")) {
				word = replaceSuffix(word, "ator", "ate");
			} else if(checkSuffix(word, "alism")) {
				word = replaceSuffix(word, "alism", "al");
			} else if(checkSuffix(word, "iveness")) {
				word = replaceSuffix(word, "iveness", "ive");
			} else if(checkSuffix(word, "fulness")) {
				word = replaceSuffix(word, "fulness", "ful");
			} else if(checkSuffix(word, "ousness")) {
				word = replaceSuffix(word, "ousness", "ous");
			} else if(checkSuffix(word, "aliti")) {
				word = replaceSuffix(word, "aliti", "al");
			} else if(checkSuffix(word, "iviti")) {
				word = replaceSuffix(word, "iviti", "ive");
			} else if(checkSuffix(word, "biliti")) {
				word = replaceSuffix(word, "biliti", "ble");
			}
		}
		
		m = countM(word);
		
		// step 3
		// all parts of step 3 require m>0, so just check once
		
		if(m > 0) {
			if(checkSuffix(word, "icate")) {
				word = replaceSuffix(word, "icate", "ic");
			} else if(checkSuffix(word, "ative")) {
				word = replaceSuffix(word, "ative", "");
			} else if(checkSuffix(word, "alize")) {
				word = replaceSuffix(word, "alize", "al");
			} else if(checkSuffix(word, "iciti")) {
				word = replaceSuffix(word, "iciti", "ic");
			} else if(checkSuffix(word, "ical")) {
				word = replaceSuffix(word, "ical", "ic");
			} else if(checkSuffix(word, "ful")) {
				word = replaceSuffix(word, "ful", "");
			} else if(checkSuffix(word, "ness")) {
				word = replaceSuffix(word, "ness", "");
			}
		}
		
		// step 4
		// all parts of step 4 require m>1, so just check once
		
		if(m > 1) {
			if(checkSuffix(word, "al")) {
				word = replaceSuffix(word, "al", "");
			} else if(checkSuffix(word, "ance")) {
				word = replaceSuffix(word, "ance", "");
			} else if(checkSuffix(word, "ence")) {
				word = replaceSuffix(word, "ence", "");
			} else if(checkSuffix(word, "er")) {
				word = replaceSuffix(word, "er", "");
			} else if(checkSuffix(word, "ic")) {
				word = replaceSuffix(word, "ic", "");
			} else if(checkSuffix(word, "able")) {
				word = replaceSuffix(word, "able", "");
			} else if(checkSuffix(word, "ible")) {
				word = replaceSuffix(word, "ible", "");
			} else if(checkSuffix(word, "ant")) {
				word = replaceSuffix(word, "ant", "");
			} else if(checkSuffix(word, "ement")) {
				word = replaceSuffix(word, "ement", "");
			} else if(checkSuffix(word, "ment")) {
				word = replaceSuffix(word, "ment", "");
			} else if(checkSuffix(word, "ion") && stemEndsWith(word, "ion", "[st]")) {
				word = replaceSuffix(word, "ion", "");
			} else if(checkSuffix(word, "ou")) {
				word = replaceSuffix(word, "ou", "");
			} else if(checkSuffix(word, "ism")) {
				word = replaceSuffix(word, "ism", "");
			} else if(checkSuffix(word, "ate")) {
				 word = replaceSuffix(word, "ate", "");
			} else if(checkSuffix(word, "iti")) {
				 word = replaceSuffix(word, "iti", "");
			} else if(checkSuffix(word, "ous")) {
				 word = replaceSuffix(word, "ous", "");
			} else if(checkSuffix(word, "ive")) {
				 word = replaceSuffix(word, "ive", "");
			} else if(checkSuffix(word, "ize")) {
				 word = replaceSuffix(word, "ize", "");
			}
		}
		
		m = countM(word);
		
		// step 5a
		
		if(m > 1 && checkSuffix(word, "e")) {
			word = replaceSuffix(word, "e", "");
		} else if(m == 1 && checkSuffix(word, "e") && !endsCVC(word)) {
			word = replaceSuffix(word, "e", "");
		}
		
		m = countM(word);
		
		// step 5b
		
		if(m > 1 && checkSuffix(word, "ll") ) {
			word = replaceSuffix(word, "ll", "l");
		}
			
		return word;
	}
	
	private static int countM(String word) {
		word = word.replaceAll("[aeiou]", "a") // replace all non-y vowels with 'a', for easier counting
				   .replaceAll("[^aeiou]", "b") // replace all non-y consonants with 'b', for easier counting
				   .replaceAll("y+", "y") // get rid of any repeated y's (just in case)
				   .replaceAll("by", "ba") // replace vowel y's with 'a'
				   .replaceAll("y", "b") // replace the rest of the y's with 'b'
				   .replaceAll("a+", "a") // get rid of repeats
				   .replaceAll("b+", "b")
				   .replaceAll("(^b)|(a$)", ""); // remove starting consonant or ending vowel
		
		return word.length()/2;
	}
	
	private static boolean checkSuffix(String word, String s1) {
		return Pattern.matches("[a-z]*" + s1 + "$", word);
	}
	
	private static String replaceSuffix(String word, String s1, String s2) {
		return word.replaceAll(s1 + "$", s2);
	}
	
	private static boolean stemEndsWith(String word, String s1, String s) {
		return Pattern.matches("[a-z]*" + s + s1 + "$", word);
	}
	
	private static boolean endsCVC(String word) {
		return Pattern.matches("[a-z]*[^aeiouy][aeiouy][^aeiouywx]$", word);
	}
	
	private static boolean stemContainsVowel(String word, String s1) {
		return containsVowelPattern.matcher(replaceSuffix(word, s1, "")).matches();
	}
	
}