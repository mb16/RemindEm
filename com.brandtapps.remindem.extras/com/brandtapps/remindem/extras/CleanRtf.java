package com.brandtapps.remindem.extras;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CleanRtf {

	//http://stackoverflow.com/questions/188545/regular-expression-for-extracting-text-from-an-rtf-string
	
	public static String cleanRtf(String text) {

		Pattern pattern = Pattern.compile("\\([a-z]{1,32})(-?[0-9]{1,10})?[ ]?|\\'([0-9a-f]{2})|\\([^a-z])|([{}])|[\r\n]+|(.)", Pattern.CASE_INSENSITIVE);

		List<String> destinations = new ArrayList<>(Arrays.asList("aftncn", "aftnsep", "aftnsepc", "annotation", "atnauthor", "atndate", "atnicn", "atnid",
				"atnparent", "atnref", "atntime", "atrfend", "atrfstart", "author", "background", "bkmkend", "bkmkstart", "blipuid", "buptim", "category",
				"colorschememapping", "colortbl", "comment", "company", "creatim", "datafield", "datastore", "defchp", "defpap", "do", "doccomm", "docvar",
				"dptxbxtext", "ebcend", "ebcstart", "factoidname", "falt", "fchars", "ffdeftext", "ffentrymcr", "ffexitmcr", "ffformat", "ffhelptext", "ffl",
				"ffname", "ffstattext", "field", "file", "filetbl", "fldinst", "fldrslt", "fldtype", "fname", "fontemb", "fontfile", "fonttbl", "footer",
				"footerf", "footerl", "footerr", "footnote", "formfield", "ftncn", "ftnsep", "ftnsepc", "g", "generator", "gridtbl", "header", "headerf",
				"headerl", "headerr", "hl", "hlfr", "hlinkbase", "hlloc", "hlsrc", "hsv", "htmltag", "info", "keycode", "keywords", "latentstyles", "lchars",
				"levelnumbers", "leveltext", "lfolevel", "linkval", "list", "listlevel", "listname", "listoverride", "listoverridetable", "listpicture",
				"liststylename", "listtable", "listtext", "lsdlockedexcept", "macc", "maccPr", "mailmerge", "maln", "malnScr", "manager", "margPr", "mbar",
				"mbarPr", "mbaseJc", "mbegChr", "mborderBox", "mborderBoxPr", "mbox", "mboxPr", "mchr", "mcount", "mctrlPr", "md", "mdeg", "mdegHide", "mden",
				"mdiff", "mdPr", "me", "mendChr", "meqArr", "meqArrPr", "mf", "mfName", "mfPr", "mfunc", "mfuncPr", "mgroupChr", "mgroupChrPr", "mgrow",
				"mhideBot", "mhideLeft", "mhideRight", "mhideTop", "mhtmltag", "mlim", "mlimloc", "mlimlow", "mlimlowPr", "mlimupp", "mlimuppPr", "mm",
				"mmaddfieldname", "mmath", "mmathPict", "mmathPr", "mmaxdist", "mmc", "mmcJc", "mmconnectstr", "mmconnectstrdata", "mmcPr", "mmcs",
				"mmdatasource", "mmheadersource", "mmmailsubject", "mmodso", "mmodsofilter", "mmodsofldmpdata", "mmodsomappedname", "mmodsoname",
				"mmodsorecipdata", "mmodsosort", "mmodsosrc", "mmodsotable", "mmodsoudl", "mmodsoudldata", "mmodsouniquetag", "mmPr", "mmquery", "mmr",
				"mnary", "mnaryPr", "mnoBreak", "mnum", "mobjDist", "moMath", "moMathPara", "moMathParaPr", "mopEmu", "mphant", "mphantPr", "mplcHide", "mpos",
				"mr", "mrad", "mradPr", "mrPr", "msepChr", "mshow", "mshp", "msPre", "msPrePr", "msSub", "msSubPr", "msSubSup", "msSubSupPr", "msSup",
				"msSupPr", "mstrikeBLTR", "mstrikeH", "mstrikeTLBR", "mstrikeV", "msub", "msubHide", "msup", "msupHide", "mtransp", "mtype", "mvertJc",
				"mvfmf", "mvfml", "mvtof", "mvtol", "mzeroAsc", "mzeroDesc", "mzeroWid", "nesttableprops", "nextfile", "nonesttables", "objalias", "objclass",
				"objdata", "object", "objname", "objsect", "objtime", "oldcprops", "oldpprops", "oldsprops", "oldtprops", "oleclsid", "operator", "panose",
				"password", "passwordhash", "pgp", "pgptbl", "picprop", "pict", "pn", "pnseclvl", "pntext", "pntxta", "pntxtb", "printim", "private",
				"propname", "protend", "protstart", "protusertbl", "pxe", "result", "revtbl", "revtim", "rsidtbl", "rxe", "shp", "shpgrp", "shpinst",
				"shppict", "shprslt", "shptxt", "sn", "sp", "staticval", "stylesheet", "subject", "sv", "svb", "tc", "template", "themedata", "title", "txe",
				"d", "pr", "serprops", "wgrffmtfilter", "windowcaption", "writereservation", "writereservhash", "xe", "xform", "xmlattrname", "xmlattrvalue",
				"xmlclose", "xmlname", "xmlnstbl", "xmlopen"));

		// Translation of some special characters.
		HashMap<String, String> specialChars = new HashMap<String, String>();
		specialChars.put("par", "\n");
		specialChars.put("sect", "\n\n");
		specialChars.put("page", "\n\n");
		specialChars.put("line", "\n");
		specialChars.put("tab", "\t");
		specialChars.put("emdash", "\u2014");
		specialChars.put("endash", "\u2013");
		specialChars.put("emspace", "\u2003");
		specialChars.put("enspace", "\u2002");
		specialChars.put("qmspace", "\u2005");
		specialChars.put("bullet", "\u2022");
		specialChars.put("lquote", "\u2018");
		specialChars.put("rquote", "\u2019");
		specialChars.put("ldblquote", "\201C");
		specialChars.put("rdblquote", "\u201D");

		StackSpecial stack = new StackSpecial();
		Boolean ignorable = false; // Whether this group (and all inside it) are
									// "ignorable".
		int ucskip = 1; // Number of ASCII characters to skip after a unicode
						// character.
		int curskip = 0; // Number of ASCII characters left to skip
		ArrayList<String> out = new ArrayList<String>(); // Output buffer.
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			String word = matcher.group(1);
			String arg = matcher.group(2);
			String hex = matcher.group(3);
			String chr = matcher.group(4);
			String brace = matcher.group(5);
			String tchar = matcher.group(6);
			if (brace != null) {
				curskip = 0;
				if (brace.equals("{")) {
					// Push state
					stack.push(new Special(ucskip, ignorable));
				} else if (brace.equals("}")) {
					// Pop state
					Special temp = stack.pop();
					ucskip = temp.ucskip;
					ignorable = temp.ignorable;
				}
			} else if (chr != null) { // \x (not a letter)
				curskip = 0;
				if (chr.equals("~")) {
					if (!ignorable)
						out.add("\u00A0"); // u'\xA0' non-breaking space
				} else if (chr.equals("{") || chr.equals("}") || chr.equals("\\")) { // *************
																						// check
					if (!ignorable)
						out.add(chr);
				} else if (chr.equals("*"))
					ignorable = true;
			} else if (word != null) { // foo
				curskip = 0;
				if (destinations.contains(word))
					ignorable = true;
				else if (ignorable)
					continue;
				else if (specialChars.containsKey(word))
					out.add(specialChars.get(word));
				else if (word.equals("uc"))
					ucskip = Integer.parseInt(arg);
				else if (word.equals("u")) {
					int c = Integer.parseInt(arg);
					if (c < 0)
						c += 0x10000;
					if (c > 127)
						out.add(Character.toString((char) c));
					else
						out.add(Character.toString((char) c));
					curskip = ucskip;
				}
			} else if (hex != null) { // \'xx
				if (curskip > 0)
					curskip -= 1;
				else if (!ignorable) {
					int c = Integer.parseInt(hex, 16);
					if (c > 127)
						out.add(Character.toString((char) c));
					else
						out.add(Character.toString((char) c));
				}
			} else if (tchar != null) {
				if (curskip > 0)
					curskip -= 1;
				else if (!ignorable)
					out.add(tchar);
			}
		}

		StringBuilder sb = new StringBuilder();
		for (String str : out) {
			sb.append(str).append(""); // separating contents using semi colon
		}

		return sb.toString();

	}
}
