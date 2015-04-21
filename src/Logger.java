import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * The logger, when turned on, enables debugging statements to be printed to
 * an output along with STDOUT.
 */
public class Logger {

	static boolean on = false;
	static String output = "";

	public static void activate()
	{
		on = true;
	}

	public static void deactivate()
	{
		on = false;
	}

	/* Log a string. */
	public static void log(String message)
	{
		if (on)
		{
			try {
				Calendar calendar = Calendar.getInstance();
				Date date = calendar.getTime();
				String newOutput = new Timestamp(date.getTime()).toString()+": "+message+"\n";
				output = output + (newOutput);
				System.out.println(newOutput);
			}
			catch (Exception e)
			{
			}
		}
	}

	/* Print string to file */
	public static void saveToFile(String message, String fileName)
	{
		FileOutputStream fop = null;
		try {
			File file = new File(fileName);

			fop = new FileOutputStream(file,false);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				System.out.println("File should be created!");
				file.createNewFile();
			}
			else
			{
				System.out.println("File is already created!");
			}

			// get the content in bytes
			byte[] contentInBytes = message.getBytes();

			fop.write(contentInBytes);
			fop.flush();
			fop.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String display(int size)
	{
		if(size > output.length())
		{
			size = output.length();
		}
		String display = "";
		if(size == 0)
		{
			display = output;
		}
		else
		{
			display = output.substring(0, size);
		}
		if(display == null)
		{
			return "";
		}
		return display;
	}

}
