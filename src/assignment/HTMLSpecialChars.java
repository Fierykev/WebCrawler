package assignment;

import java.util.HashMap;

public class HTMLSpecialChars {

    public static HashMap<String, Character> specialChars = new HashMap<>();

    // based on https://www.utexas.edu/learn/html/spchar.html

    static {
        specialChars.put("&ndash;", '–');
        specialChars.put("&mdash;", '—');
        specialChars.put("&iexcl;", '¡');
        specialChars.put("&iquest;", '¿');
        specialChars.put("&quot;", '"');
        specialChars.put("&ldquo;", '“');
        specialChars.put("&rdquo;", '”');
        specialChars.put("&lsquo;", '‘');
        specialChars.put("&rsquo;", '’');
        specialChars.put("&laquo;", '«');
        specialChars.put("&raquo;", '»');
        specialChars.put("&nbsp;", '\u00A0');
        specialChars.put("&amp;", '&');
        specialChars.put("&cent;", '¢');
        specialChars.put("&copy;", '©');
        specialChars.put("&divide;", '÷');
        specialChars.put("&gt;", '>');
        specialChars.put("&lt;", '<');
        specialChars.put("&micro;", 'µ');
        specialChars.put("&middot;", '·');
        specialChars.put("&para;", '¶');
        specialChars.put("&plusmn;", '±');
        specialChars.put("&euro;", '€');
        specialChars.put("&pound;", '£');
        specialChars.put("&reg;", '®');
        specialChars.put("&sect;", '§');
        specialChars.put("&trade;", '™');
        specialChars.put("&yen;", '¥');
        specialChars.put("&ampdeg;", '°');
        specialChars.put("&aacute;", 'á');
        specialChars.put("&Aacute;", 'Á');
        specialChars.put("&agrave;", 'à');
        specialChars.put("&Agrave;", 'À');
        specialChars.put("&acirc;", 'â');
        specialChars.put("&Acirc;", 'Â');
        specialChars.put("&aring;", 'å');
        specialChars.put("&Aring;", 'Å');
        specialChars.put("&atilde;", 'ã');
        specialChars.put("&Atilde;", 'Ã');
        specialChars.put("&auml;", 'ä');
        specialChars.put("&Auml;", 'Ä');
        specialChars.put("&aelig;", 'æ');
        specialChars.put("&AElig;", 'Æ');
        specialChars.put("&ccedil;", 'ç');
        specialChars.put("&Ccedil;", 'Ç');
        specialChars.put("&eacute;", 'é');
        specialChars.put("&Eacute;", 'É');
        specialChars.put("&egrave;", 'è');
        specialChars.put("&Egrave;", 'È');
        specialChars.put("&ecirc;", 'ê');
        specialChars.put("&Ecirc;", 'Ê');
        specialChars.put("&euml;", 'ë');
        specialChars.put("&Euml;", 'Ë');
        specialChars.put("&iacute;", 'í');
        specialChars.put("&Iacute;", 'Í');
        specialChars.put("&igrave;", 'ì');
        specialChars.put("&Igrave;", 'Ì');
        specialChars.put("&icirc;", 'î');
        specialChars.put("&Icirc;", 'Î');
        specialChars.put("&iuml;", 'ï');
        specialChars.put("&Iuml;", 'Ï');
        specialChars.put("&ntilde;", 'ñ');
        specialChars.put("&Ntilde;", 'Ñ');
        specialChars.put("&oacute;", 'ó');
        specialChars.put("&Oacute;", 'Ó');
        specialChars.put("&ograve;", 'ò');
        specialChars.put("&Ograve;", 'Ò');
        specialChars.put("&ocirc;", 'ô');
        specialChars.put("&Ocirc;", 'Ô');
        specialChars.put("&oslash;", 'ø');
        specialChars.put("&Oslash;", 'Ø');
        specialChars.put("&otilde;", 'õ');
        specialChars.put("&Otilde;", 'Õ');
        specialChars.put("&ouml;", 'ö');
        specialChars.put("&Ouml;", 'Ö');
        specialChars.put("&szlig;", 'ß');
        specialChars.put("&uacute;", 'ú');
        specialChars.put("&Uacute;", 'Ú');
        specialChars.put("&ugrave;", 'ù');
        specialChars.put("&Ugrave;", 'Ù');
        specialChars.put("&ucirc;", 'û');
        specialChars.put("&Ucirc;", 'Û');
        specialChars.put("&uuml;", 'ü');
        specialChars.put("&Uuml;", 'Ü');
        specialChars.put("&yuml;", 'ÿ');
    }

}
