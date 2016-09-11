package assignment;

import java.util.HashMap;

public class HTMLSpecialChars {

    public static HashMap<String, Character> specialChars = new HashMap<>();

    // based on https://www.utexas.edu/learn/html/spchar.html

    static {
        specialChars.put("&ndash;", '�');
        specialChars.put("&mdash;", '�');
        specialChars.put("&iexcl;", '�');
        specialChars.put("&iquest;", '�');
        specialChars.put("&quot;", '"');
        specialChars.put("&ldquo;", '�');
        specialChars.put("&rdquo;", '�');
        specialChars.put("&lsquo;", '�');
        specialChars.put("&rsquo;", '�');
        specialChars.put("&laquo;", '�');
        specialChars.put("&raquo;", '�');
        specialChars.put("&nbsp;", '\u00A0');
        specialChars.put("&amp;", '&');
        specialChars.put("&cent;", '�');
        specialChars.put("&copy;", '�');
        specialChars.put("&divide;", '�');
        specialChars.put("&gt;", '>');
        specialChars.put("&lt;", '<');
        specialChars.put("&micro;", '�');
        specialChars.put("&middot;", '�');
        specialChars.put("&para;", '�');
        specialChars.put("&plusmn;", '�');
        specialChars.put("&euro;", '�');
        specialChars.put("&pound;", '�');
        specialChars.put("&reg;", '�');
        specialChars.put("&sect;", '�');
        specialChars.put("&trade;", '�');
        specialChars.put("&yen;", '�');
        specialChars.put("&ampdeg;", '�');
        specialChars.put("&aacute;", '�');
        specialChars.put("&Aacute;", '�');
        specialChars.put("&agrave;", '�');
        specialChars.put("&Agrave;", '�');
        specialChars.put("&acirc;", '�');
        specialChars.put("&Acirc;", '�');
        specialChars.put("&aring;", '�');
        specialChars.put("&Aring;", '�');
        specialChars.put("&atilde;", '�');
        specialChars.put("&Atilde;", '�');
        specialChars.put("&auml;", '�');
        specialChars.put("&Auml;", '�');
        specialChars.put("&aelig;", '�');
        specialChars.put("&AElig;", '�');
        specialChars.put("&ccedil;", '�');
        specialChars.put("&Ccedil;", '�');
        specialChars.put("&eacute;", '�');
        specialChars.put("&Eacute;", '�');
        specialChars.put("&egrave;", '�');
        specialChars.put("&Egrave;", '�');
        specialChars.put("&ecirc;", '�');
        specialChars.put("&Ecirc;", '�');
        specialChars.put("&euml;", '�');
        specialChars.put("&Euml;", '�');
        specialChars.put("&iacute;", '�');
        specialChars.put("&Iacute;", '�');
        specialChars.put("&igrave;", '�');
        specialChars.put("&Igrave;", '�');
        specialChars.put("&icirc;", '�');
        specialChars.put("&Icirc;", '�');
        specialChars.put("&iuml;", '�');
        specialChars.put("&Iuml;", '�');
        specialChars.put("&ntilde;", '�');
        specialChars.put("&Ntilde;", '�');
        specialChars.put("&oacute;", '�');
        specialChars.put("&Oacute;", '�');
        specialChars.put("&ograve;", '�');
        specialChars.put("&Ograve;", '�');
        specialChars.put("&ocirc;", '�');
        specialChars.put("&Ocirc;", '�');
        specialChars.put("&oslash;", '�');
        specialChars.put("&Oslash;", '�');
        specialChars.put("&otilde;", '�');
        specialChars.put("&Otilde;", '�');
        specialChars.put("&ouml;", '�');
        specialChars.put("&Ouml;", '�');
        specialChars.put("&szlig;", '�');
        specialChars.put("&uacute;", '�');
        specialChars.put("&Uacute;", '�');
        specialChars.put("&ugrave;", '�');
        specialChars.put("&Ugrave;", '�');
        specialChars.put("&ucirc;", '�');
        specialChars.put("&Ucirc;", '�');
        specialChars.put("&uuml;", '�');
        specialChars.put("&Uuml;", '�');
        specialChars.put("&yuml;", '�');
    }

}
