package fi.bb.checkers.imageloader;

import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.UiApplication;
import fi.bb.checkers.network.HttpInterface;
import fi.bb.checkers.utils.BitmapTools;
import fi.bb.checkers.utils.Queue;

public class ImageLoader
{
	private static final Queue download_queue = new Queue();
	private static DownloadThread[] thread_pool = new DownloadThread[5];

	public static void loadImage(String url, ImageLoaderInterface callback)
	{

		if (url == null || url.equals("") || callback == null) return;
		Bitmap bitmap = ImageCache.getImage(getFileName(url));

		if (bitmap != null)
		{
			callback.setImage(bitmap);
			return;
		}
		synchronized (download_queue)
		{
			download_queue.enqueue(new DownloadQueueItem(url, callback));
		}

		int threads_needed = download_queue.size();
		for (int i = 0; i < thread_pool.length; i++)
		{
			if (threads_needed == 0) break;

			if (thread_pool[i] == null || !thread_pool[i].isAlive())
			{
				thread_pool[i] = new DownloadThread();
				thread_pool[i].start();
				threads_needed--;
			}
		}

	}

	public static void emptyQueue()
	{
		synchronized (download_queue)
		{
			download_queue.removeAllElements();
		}
	}

	private static Bitmap retrieveBitmapFromUrl(final String url)
	{
		Bitmap bitmap = null;
		InputStream inputStream = null;
		HttpConnection httpconnection = null;

		// Request queued earlier may have retrieved the same image url. So check cache.
		bitmap = ImageCache.getImage(getFileName(url));
		if (bitmap != null)
		{
			return bitmap;
		}

		try
		{
			try
			{
				bitmap = BitmapTools.retrieveBitmapFromDisk(url);
				if (bitmap != null)
				{
					ImageCache.cacheImage(getFileName(url), bitmap);
					return bitmap;
				}
			} catch (Exception e)
			{// Don't want the object to be missing if there is an error
			}

			byte[] responseData = new byte[1000];
			String params;
			params = HttpInterface.getConnectionString();

			httpconnection = (HttpConnection) Connector.open(url + params, Connector.READ, true);
			inputStream = httpconnection.openInputStream();
			responseData = IOUtilities.streamToBytes(inputStream);

			int responseCode = httpconnection.getResponseCode();

			if (responseCode != HttpConnection.HTTP_OK)
			{
				return bitmap;
			}

			bitmap = Bitmap.createBitmapFromBytes(responseData, 0, responseData.length, 1);

			try
			{
				ImageCache.cacheImage(getFileName(url), bitmap);
				BitmapTools.saveBitmapToDisk(url, bitmap);
			} catch (Exception e)
			{// Don't want the object to be missing if there is an error
			}
		} catch (Throwable ex)
		{
		} finally
		{
			try
			{
				if (inputStream != null)
				{
					inputStream.close();
					inputStream = null;
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			try
			{
				if (httpconnection!=null)
				{
					httpconnection.close();
					httpconnection = null;
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		return bitmap;
	}

	private static String getFileName(String url)
	{
		return url.substring(url.lastIndexOf('/') + 1);
	}

	private static class DownloadThread extends Thread
	{
		public DownloadThread()
		{
			setPriority(Thread.NORM_PRIORITY);//WAS MIN_PRIORITY
		}

		public void run()
		{
			while (!download_queue.isEmpty())
			{
				try
				{
					final DownloadQueueItem queued_item;
					synchronized (download_queue)
					{
						queued_item = (DownloadQueueItem) download_queue.dequeue();
					}

					final Bitmap image = retrieveBitmapFromUrl(queued_item.url);
					UiApplication.getUiApplication().invokeLater(new Runnable()
					{
						public void run()
						{
							try
							{
								(queued_item.callback).setImage(image);
							} catch (Exception e)
							{
							};
						}
					});

				} catch (Exception e)
				{
				};
			}
		}
	}

	private static class DownloadQueueItem
	{
		String url;
		ImageLoaderInterface callback;

		public DownloadQueueItem(String url, ImageLoaderInterface callback)
		{
			this.url = url;
			this.callback = callback;
		}

		public boolean equals(Object obj)
		{
			return callback == obj;
		}
	}
}
