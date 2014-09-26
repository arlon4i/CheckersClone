package fi.bb.checkers.imageloader;

import java.lang.ref.WeakReference;
import java.util.Hashtable;

import net.rim.device.api.system.Bitmap;

public class ImageCache
{
	// use weakreference to allow garbage collection to use the memory
	private static Hashtable cache = new Hashtable();

	public static Bitmap getImage(String key)
	{
		WeakReference reference = (WeakReference) cache.get(key);
		if (reference == null) return null;

		return (Bitmap) reference.get();
	}

	public static void cacheImage(String key, Bitmap value)
	{
		if (key == null || value == null) return;

		cache.put(key, new WeakReference(value));
	}
}
