package fi.bb.checkers.utils;

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.file.FileSystemRegistry;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.SimpleSortingVector;

public class ApplicationInterface
{
	private static final int TIMEOUT = 1500;

	public static boolean isSDCardIn()
	{
		boolean response = false;
		String root = null;
		Enumeration e = FileSystemRegistry.listRoots();
		while (e.hasMoreElements())
		{
			root = (String) e.nextElement();
			if (root.equalsIgnoreCase("sdcard/"))
			{
				response = true;
			}
		}
		return response;
	}

	public static String getApplicationName()
	{
		String application_name = ApplicationDescriptor.currentApplicationDescriptor().getName();

		return application_name;
	}

	public static String getApplicationName(int handle)
	{
		final ApplicationDescriptor app = CodeModuleManager.getApplicationDescriptors(handle)[0];
		String application_name = app.getName();

		return application_name;
	}

	public static int getModuleHandle()
	{
		int module_handle = ApplicationDescriptor.currentApplicationDescriptor().getModuleHandle();

		return module_handle;
	}

	public static Vector getAllApplications()
	{
		SimpleSortingVector list = new SimpleSortingVector();
		list.setSort(true);
		list.setSortComparator(new AppComparator());

		int[] handles = CodeModuleManager.getModuleHandles();

		for (int i = 0; i < handles.length; i++)
		{
			if (!CodeModuleManager.isLibrary(handles[i]))
			{
				ApplicationDescriptor[] descriptor = CodeModuleManager
						.getApplicationDescriptors(handles[i]);
				// null - not an application
				// 2 - system app
				// 3 - auto-run system app
				// facebook and twitter are system apps
				if (descriptor != null)
				{
					if (descriptor[0].getModuleHandle() == getModuleHandle()) continue;

					if (descriptor[0].getFlags() != 2 && descriptor[0].getFlags() != 3)
					{
						list.addElement(descriptor[0]);
					}
					else if (descriptor[0].getName().equalsIgnoreCase("facebook")
							|| descriptor[0].getName().equalsIgnoreCase("twitter"))
					{
						list.addElement(descriptor[0]);
					}
				}
			}
		}
		return list;
	}

	public static boolean launchApplication(int handle) throws Exception
	{
		try
		{
			final ApplicationDescriptor app = CodeModuleManager.getApplicationDescriptors(handle)[0];
			int process = ApplicationManager.getApplicationManager().getProcessId(app);
			if (process == -1)
			{
				process = ApplicationManager.getApplicationManager().runApplication(app);
			}
			ApplicationManager.getApplicationManager().requestForeground(process);

			Thread.sleep(TIMEOUT);
			if (UiApplication.getUiApplication().isForeground())
			{
				return false;
			}

		} catch (Exception e)
		{
		}

		return true;
	}
}

class AppComparator implements Comparator
{
	public int compare(Object o1, Object o2)
	{
		ApplicationDescriptor a1 = (ApplicationDescriptor) o1;
		ApplicationDescriptor a2 = (ApplicationDescriptor) o2;

		String name1 = a1.getName();
		String name2 = a2.getName();

		name1 = name1.toLowerCase();
		name2 = name2.toLowerCase();

		if (name1.startsWith("net_rim"))
		{
			int prefix_length = "net_rim_".length();
			name1 = name1.substring(prefix_length);
			name1 = name1.replace('_', ' ');
		}
		if (name2.startsWith("net_rim"))
		{
			int prefix_length = "net_rim_".length();
			name2 = name2.substring(prefix_length);
			name2 = name2.replace('_', ' ');
		}

		return name1.compareTo(name2);
	}

}
