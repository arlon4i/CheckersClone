package fi.bb.checkers.utils;

import java.util.Vector;

import me.regexp.RE;
import net.rim.device.api.ui.Font;

public class StringUtil
{
	public static final String EMAIL_REGEX = "^[_A-Z0-9-\\+]+(\\.[_A-Z0-9-]+)*@[A-Z0-9-]+(\\.[A-Z0-9]+)*(\\.[A-Z]{2,})$";
	public static boolean isEmailAddress(String email_address)
	{
		if (email_address.length() == 0) return false;

		RE regex = new RE(EMAIL_REGEX);
		regex.setMatchFlags(RE.MATCH_CASEINDEPENDENT);

		if (regex.match(email_address)) return true;

		return false;
	}

	/**
	 * Splits a string into sections of n size, the last section will contain the remainder characters
	 * 
	 * <p>
	 * eg.<br>
	 * <code>formatDivisions(2, "1111111")</code> = "11 11 111"<br>
	 * <code>formatDivisions(3, "1111111")</code> = "111 1111"
	 * </p>
	 * 
	 * @param n
	 * @param string
	 * @return
	 */
	public static String formatDivisions(int n, String string)
	{
		int nr_spaces = (string.length() - n) / n;
		while (nr_spaces > 0)
		{
			string = string.substring(0, nr_spaces * n) + " " + string.substring(nr_spaces * n);
			nr_spaces--;
		}

		return string;
	}

	public static String toProperCase(String arg)
	{
		arg = arg.toLowerCase();
		String[] tokens = split(arg, " ");
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < tokens.length; i++)
		{
			String token = tokens[i];
			buffer.append(token.substring(0, 1).toUpperCase());
			if (token.length() > 1)
			{
				buffer.append(token.substring(1).toLowerCase());
			}

			if (i != tokens.length)
			{
				buffer.append(" ");
			}
		}

		return buffer.toString();
	}

	public static String removeNonDigit(String arg)
	{
		StringBuffer builder = new StringBuffer();
		for (int i = 0; i < arg.length(); i++)
		{
			char c = arg.charAt(i);
			if (Character.isDigit(c)) builder.append(c);
		}
		return builder.toString();
	}

	// return arg1 with all occurrences of arg2 removed
	public static String remove(String arg1, String arg2)
	{
		int index = -1;
		while ((index = arg1.indexOf(arg2)) >= 0)
		{
			String temp = arg1.substring(0, index);
			temp += arg1.substring(index + arg2.length());

			arg1 = temp;
		}

		return arg1;
	}

	public static String replace(String string, String find, String replace_with)
	{
		int current_index = 0; // only perform 1 run through
		int index = -1;
		do
		{
			index = string.indexOf(find, current_index);
			if (index != -1)
			{
				string = string.substring(0, index) + replace_with + string.substring(index + find.length());
				current_index = index + replace_with.length();
			}
		} while (index != -1);

		return string;
	}

	public static String[] split(String inString, String delimeter)
	{
		String[] retAr = new String[0];

		try
		{
			Vector vec = new Vector();
			int indexA = 0;
			int indexB = inString.indexOf(delimeter);

			while (indexB != -1)
			{
				if (indexB > indexA) vec.addElement(new String(inString.substring(indexA, indexB)));
				indexA = indexB + delimeter.length();
				indexB = inString.indexOf(delimeter, indexA);
			}
			vec.addElement(new String(inString.substring(indexA, inString.length())));
			retAr = new String[vec.size()];
			for (int i = 0; i < vec.size(); i++)
			{
				retAr[i] = vec.elementAt(i).toString();
			}
		} catch (Exception e)
		{
		}
		return retAr;
	}

	/**
	 * Splits a string into a specified number of <code>lines</code> depending on the available <code>width</code>. This method honours the newline character, and <b>ignores</b> multiple whitespace characters. All words will have one space between them. Uses hyphenation as a soft break.
	 * 
	 * @param font
	 * @param string
	 * @param width
	 * @param lines
	 * @return
	 */
	public static String[] wrapText(Font font, String string, int width, int lines)
	{
		Vector strings = new Vector();
		String[] toreturn;
		if (string==null)
		{
			string = "";
		}
		string = replace(string, "\n", " \n ");
		String[] tokens = split(string, " ");// honour '\n' chars

		int i = 0;
		StringBuffer builder = new StringBuffer();
		loop_while : while (strings.size() < lines && i < tokens.length)
		{
			if (strings.size() == lines - 1)
			{
				// add rest
				for (; i < tokens.length; i++)
				{
					if (tokens[i].equals("\n"))
					{
						i++;
						builder.append("");
						break;
					}

					builder.append(tokens[i]);
					builder.append(" ");
				}
			}
			else
			{
				// add 1 line
				for (; i < tokens.length; i++)
				{
					if (tokens[i].equals("\n"))
					{
						i++;
						builder.append("");
						break;
					}

					if (font.getAdvance(builder.toString() + tokens[i] + " ") <= width)
					{
						builder.append(tokens[i]);
						builder.append(" ");
					}
					else
					{
						int index = tokens[i].indexOf('-');
						if (index != -1)
						{
							index++;
							if (font.getAdvance(builder.toString() + tokens[i].substring(0, index) + " ") <= width)
							{
								builder.append(tokens[i].substring(0, index));
								builder.append(" ");
								strings.addElement(builder.toString().trim());
								builder = new StringBuffer();
								builder.append(tokens[i].substring(index));
								continue loop_while;
							}
						}

						break;
					}
				}
			}

			strings.addElement(builder.toString().trim());
			builder = new StringBuffer();
		}

		toreturn = new String[strings.size()];
		strings.copyInto(toreturn);

		return toreturn;
	}

	/**
	 * Wraps the text according to 
	 * @param font
	 * @param string
	 * @param width
	 * @param lines
	 * @return
	 */
	public static String[] ellipsize(Font font, String string, int width, int lines)
	{
		String[] toreturn = wrapText(font, string, width, lines);

		if (toreturn.length == lines)
		{
			String temp = toreturn[lines - 1];
			if (font.getAdvance(temp) > width)
			{
				int i;
				for (i = temp.length(); font.getAdvance(temp.substring(0, i) + "...") > width; i--);
				temp = temp.substring(0, i) + "...";
			}

			toreturn[lines - 1] = temp;
		}

		return toreturn;
	}
	
	public static String[] breakupTextToWidth(Font font, String string, int width)
	{
		Vector strings = new Vector();
		String[] toreturn;
		string = replace(string, "\n", " \n ");
		String[] tokens = split(string, " ");// honour '\n' chars

		int lines = 100;
		
		int i = 0;
		StringBuffer builder = new StringBuffer();
		loop_while : while (strings.size() < lines && i < tokens.length)
		{
			if (strings.size() == lines - 1)
			{
				// add rest
				for (; i < tokens.length; i++)
				{
					if (tokens[i].equals("\n"))
					{
						i++;
						builder.append("");
						break;
					}

					builder.append(tokens[i]);
					builder.append(" ");
				}
			}
			else
			{
				// add 1 line
				for (; i < tokens.length; i++)
				{
					if (tokens[i].equals("\n"))
					{
						i++;
						builder.append("");
						break;
					}

					if (font.getAdvance(builder.toString() + tokens[i] + " ") <= width)
					{
						builder.append(tokens[i]);
						builder.append(" ");
					}
					else
					{
						int index = tokens[i].indexOf('-');
						if (index != -1)
						{
							index++;
							if (font.getAdvance(builder.toString() + tokens[i].substring(0, index) + " ") <= width)
							{
								builder.append(tokens[i].substring(0, index));
								builder.append(" ");
								strings.addElement(builder.toString().trim());
								builder = new StringBuffer();
								builder.append(tokens[i].substring(index));
								continue loop_while;
							}
						}

						break;
					}
				}
			}

			strings.addElement(builder.toString().trim());
			builder = new StringBuffer();
		}

		toreturn = new String[strings.size()];
		for (i = 0; i < strings.size(); i++)
		{
			toreturn[i] = (String) strings.elementAt(i);
		}

		if (toreturn.length == lines)
		{
			String temp = toreturn[lines - 1];
			if (font.getAdvance(temp) > width)
			{
				for (i = temp.length(); font.getAdvance(temp.substring(0, i) + "...") > width; i--);
				temp = temp.substring(0, i) + "...";
			}

			toreturn[lines - 1] = temp;
		}

		return toreturn;
	}
}
