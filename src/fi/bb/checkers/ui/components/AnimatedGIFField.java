package fi.bb.checkers.ui.components;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.GIFEncodedImage;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;

public class AnimatedGIFField extends BitmapField
{
	private GIFEncodedImage image;
	private int currentFrame;
	// private int width;
	// private int height;
	private AnimatorThread animatorThread;

	public AnimatedGIFField(String image)
	{
		this(image, 0);
	}

	public AnimatedGIFField(String image, long style)
	{
		super(EncodedImage.getEncodedImageResource(image).getBitmap(), style);

		this.image = (GIFEncodedImage) EncodedImage.getEncodedImageResource(image);
		// this.width = this.image.getWidth();
		// this.height = this.image.getHeight();

		animatorThread = new AnimatorThread(this);
		animatorThread.start();
	}

	public int getPreferredHeight()
	{
		int height = 0;
		for (int i = 1; i < image.getFrameCount(); i++)
		{
			height = Math.max(height, image.getFrameHeight(i));
		}
		return height;
	}

	public int getPreferredWidth()
	{
		int width = 0;
		for (int i = 1; i < image.getFrameCount(); i++)
		{
			width = Math.max(width, image.getFrameWidth(i));
		}
		return width;
	}

	protected void paint(Graphics graphics)
	{
		super.paint(graphics);

		if (currentFrame != 0)
		{
			graphics.drawImage(image.getFrameLeft(currentFrame), image.getFrameTop(currentFrame), image.getFrameWidth(currentFrame), image.getFrameHeight(currentFrame), image, currentFrame, 0, 0);
		}
	}

	protected void onUndisplay()
	{
		animatorThread.stop();
		super.onUndisplay();
	}

	private class AnimatorThread extends Thread
	{
		private AnimatedGIFField theField;
		private boolean keepGoing = true;
		private int totalFrames;
		private int loopCount;
		private int totalLoops;

		public AnimatorThread(AnimatedGIFField theField)
		{
			this.theField = theField;
			totalFrames = image.getFrameCount();
			totalLoops = image.getIterations();
		}

		public synchronized void stop()
		{
			keepGoing = false;
		}

		public void run()
		{
			while (keepGoing)
			{
				// Invalidate the field so that it is redrawn.
				UiApplication.getUiApplication().invokeAndWait(new Runnable()
				{
					public void run()
					{
						theField.invalidate();
					}
				});

				try
				{
					sleep(image.getFrameDelay(currentFrame) * 10);
				} catch (InterruptedException iex)
				{
				}

				++currentFrame;

				if (currentFrame == totalFrames)
				{
					currentFrame = 0;

					++loopCount;

					if (loopCount == totalLoops)
					{
						keepGoing = false;
					}
				}
			}
		}
	}
}