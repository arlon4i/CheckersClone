package fi.bb.checkers.ui.fragments;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.global.Formatter;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import fi.bb.checkers.datatypes.CampaignData;
import fi.bb.checkers.datatypes.CouponCategory;
import fi.bb.checkers.helpers.FlurryHelper;
import fi.bb.checkers.helpers.PersistentStoreHelper;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.helpers.RuntimeStoreHelper;
import fi.bb.checkers.helpers.SharingHelper;
import fi.bb.checkers.helpers.StringHelper;
import fi.bb.checkers.imageloader.ImageLoader;
import fi.bb.checkers.imageloader.ImageLoaderInterface;
import fi.bb.checkers.interfaces.InterfaceCouponsFinishedLoading;
import fi.bb.checkers.logger.RemoteLogger;
import fi.bb.checkers.prompts.CustomDialog;
import fi.bb.checkers.ui.components.LabelField;
import fi.bb.checkers.ui.components.RelatedItem;
import fi.bb.checkers.ui.components.TextImageButton;
import fi.bb.checkers.ui.screens.ViewPagerScreen;
import fi.bb.checkers.utils.BitmapTools;
import fi.bb.checkers.utils.StringUtil;
import fi.bb.checkers.utils.VectorUtil;

public class DetailedCouponFragment extends Fragment implements FieldChangeListener, InterfaceCouponsFinishedLoading
{
	public static final int FRAGMENT_ID = getUUID();
	private CampaignData coupon;
	private HorizontalFieldManager related_manager;
	TextImageButton button_add;
	TextImageButton button_share;

	InterfaceCouponsFinishedLoading interfaceListener;

	public DetailedCouponFragment(CampaignData coupon)
	{
		super(NO_HORIZONTAL_SCROLL | VERTICAL_SCROLL);
		this.coupon = coupon;

		this.interfaceListener = this;

		try
		{
			build();
		} catch (Throwable e)
		{
			RemoteLogger.log("ERROR", e.toString());
		}
	}

	private void build()
	{
		deleteAll();
		related_manager = null;

		final CouponInfo coupon_item = new CouponInfo(coupon);
		add(coupon_item);

		Font font = ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(13), Ui.UNITS_px);
		final String[] lines = StringUtil.ellipsize(font, coupon.getTerms(), Display.getWidth() - ResourceHelper.convert(10), Integer.MAX_VALUE);
		String copytext = "";

		if (lines.length != 0)
		{
			copytext = lines[0];
			for (int i = 1; i < lines.length; i++)
			{
				copytext += "\n" + lines[i];
			}
		}

		LabelField terms = new LabelField(copytext, ResourceHelper.color_dark_grey, 0)
		{
			public int getPreferredHeight()
			{
				return super.getPreferredHeight() * lines.length;
			}
		};
		int termsMarginTop = ResourceHelper.convert(5);
		int termsMarginBottom = ResourceHelper.convert(10);
		terms.setFont(font);
		terms.setMargin(termsMarginTop, ResourceHelper.convert(5), termsMarginBottom, ResourceHelper.convert(5));
		add(terms);

		button_add = new TextImageButton(coupon.isChecked() ? "Remove" : "Add To My List", "btn_default", "btn_hover");
		button_add.setTextColor(ResourceHelper.color_white);
		button_add.setTextColorHover(ResourceHelper.color_primary);
		button_add.setTextColorPressed(ResourceHelper.color_primary);
		button_add.setChangeListener(this);

		button_share =  new TextImageButton("Share", "btn_grey_default", "btn_hover");
		button_share.setTextColor(ResourceHelper.color_primary);
		button_share.setTextColorHover(ResourceHelper.color_primary);
		button_share.setTextColorPressed(ResourceHelper.color_primary);
		button_share.setChangeListener(this);

		button_add.setMargin(0, ResourceHelper.convert(5), ResourceHelper.convert(10), ResourceHelper.convert(5));
		button_share.setMargin(0, ResourceHelper.convert(5), ResourceHelper.convert(10), ResourceHelper.convert(5));

		VerticalFieldManager manager = new VerticalFieldManager();
		manager.add(button_add);
		manager.add(button_share);
		add(manager);

		/*Vector related_coupons = getRelatedCoupons();
		if (!related_coupons.isEmpty())
		{
		 */Field heading = new Field()
		 {
			 public int getPreferredHeight()
			 {
				 return ResourceHelper.convert(25);
			 }
			 public int getPreferredWidth()
			 {
				 return Display.getWidth();
			 }
			 protected void layout(int width, int height)
			 {
				 setExtent(getPreferredWidth(), getPreferredHeight());
			 }

			 protected void paint(Graphics graphics)
			 {
				 graphics.setColor(ResourceHelper.color_light_grey);
				 graphics.fillRect(0, 0, getWidth(), getHeight());

				 int y = (getHeight() - getFont().getHeight()) / 2;
				 graphics.setColor(ResourceHelper.color_dark_grey);
				 graphics.drawText("OTHER EEZICOUPONS", ResourceHelper.convert(5), y);
			 }
		 };
		 heading.setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(14), Ui.UNITS_px));
		 add(heading);

		 related_manager = new HorizontalFieldManager(NO_VERTICAL_SCROLL | HORIZONTAL_SCROLL)
		 {
			 public int getPreferredHeight()
			 {
				 return RelatedItem.height;
			 }

			 public int getPreferredWidth()
			 {
				 return Display.getWidth();
			 }

			 protected void sublayout(int maxWidth, int maxHeight)
			 {
				 super.sublayout(getPreferredWidth(), getPreferredHeight());
				 setExtent(getPreferredWidth(), getPreferredHeight());
			 }
		 };
		 add(related_manager);

		 /*for (int i = 0; i < related_coupons.size(); i++)
			{
				related_manager.add(new RelatedItem((CampaignData) related_coupons.elementAt(i))
				{
					public void clickButton()
					{
						logSelectsRelatedProduct(getCoupon());
						((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(DetailedCouponFragment.FRAGMENT_ID, new Object[]{getCoupon()});
					}
				});
			}*/		 
		 int headingMargin = 0;
		 int spaceLeft = getPreferredHeight() - (coupon_item.getPreferredHeight() + (terms.getPreferredHeight() + termsMarginTop + termsMarginBottom) + manager.getPreferredHeight() + heading.getPreferredHeight() + related_manager.getPreferredHeight());// + ResourceHelper.convert(5));
		 
		 if (spaceLeft >= ResourceHelper.convert(5))
		 {
			 headingMargin = spaceLeft;
		 }
		 else
		 {
			 headingMargin = ResourceHelper.convert(5);
		 }
		 
		 int margin = getPreferredHeight() - (coupon_item.getPreferredHeight() + terms.getPreferredHeight() + manager.getPreferredHeight() + heading.getPreferredHeight() + related_manager.getPreferredHeight() + headingMargin);
		 
		 heading.setMargin(Math.max(0, margin), 0, 0, 0);

		 (new LoadThread()).start();
		 //}
	}

	public void makeMenu(Menu menu)
	{
		MenuItem item;

		if (coupon.isChecked())
		{
			item = new MenuItem("Remove from List", 0x00070000, 0)
			{
				public void run()
				{
					removeFromList();
				}
			};
		}
		else
		{
			item = new MenuItem("Add To My List", 0x00070000, 0)
			{
				public void run()
				{
					addToList();
				}
			};
		}
		menu.add(item);

		String menuImageText = (PersistentStoreHelper.shouldLoadImages() == true)?"Turn off images":"Turn on images";
		item = new MenuItem(menuImageText, 0x00070000, 1)
		{
			public void run()
			{
				PersistentStoreHelper.setshouldLoadImages(!PersistentStoreHelper.shouldLoadImages());
				build();
			}
		};
		menu.add(item);
	}

	/*private Vector getRelatedCoupons()
	{
		Vector coupons = RuntimeStoreHelper.getCoupons(interfaceListener, true, "Downloading coupons...");
		Vector related = new Vector();
		for (int i = 0; i < coupons.size(); i++)
		{
			CampaignData coupon = (CampaignData) coupons.elementAt(i);
			if (!coupon.equals(this.coupon) && VectorUtil.hasCommonElement(coupon.getCategoryList(), this.coupon.getCategoryList()))
			{
				related.addElement(coupon);
				if (related.size() == 10) break;
			}
		}

		return related;
	}*/

	public void fieldChanged(Field arg0, int arg1)
	{
		if (arg0 == button_add)
		{
			if (coupon.isChecked())
			{
				removeFromList();
			}
			else
			{
				addToList();
			}
		}
		else if (arg0 == button_share)
		{
			SharingHelper.shareCoupon(coupon);
		}
	}

	private void removeFromList()
	{
		if (RuntimeStoreHelper.getSessionID() == null)
		{
			((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).profileClick();
		}
		else
		{
			PersistentStoreHelper.mylistDelete(coupon);

			button_add.setText("Add To My List");
			invalidate();
		}
	}

	private void addToList()
	{
		if (RuntimeStoreHelper.getSessionID() == null)
		{
			((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).profileClick();
		}
		else
		{
			logAddToMyList(coupon);

			PersistentStoreHelper.mylistAdd(coupon);

			button_add.setText("Remove from list");
			invalidate();
		}
	}

	// coupon info block
	private static class CouponInfo extends Field implements ImageLoaderInterface
	{
		private static final Bitmap default_image = ResourceHelper.getImage("eezicoupon_image_error");
		private static final int padding_x = ResourceHelper.convert(12);
		private static final int padding_y = ResourceHelper.convert(14);
		private static final int text_origin = ResourceHelper.convert(120);
		private static final Bitmap tick = ResourceHelper.getImage("eezicoupons_added-to-list_tick");
		private static final Font font_save = ResourceHelper.helveticaMed().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px);
		private static final Font font_rand = ResourceHelper.helveticaMed().getFont(Font.PLAIN, ResourceHelper.convert(52), Ui.UNITS_px);
		private static final Font font_cent = font_rand.derive(Font.PLAIN, font_rand.getHeight() / 2, Ui.UNITS_px);
		private static final Font font_name = ResourceHelper.helveticaMed().getFont(Font.PLAIN, ResourceHelper.convert(13), Ui.UNITS_px);

		private final CampaignData coupon;
		private final String price;
		private final String name;
		private Bitmap image;

		public CouponInfo(CampaignData coupon)
		{
			super(NON_FOCUSABLE);
			this.coupon = coupon;

			int width = getPreferredWidth() - text_origin - padding_x * 2;
			if (coupon.getName() != null)
			{
				String[] lines = StringUtil.ellipsize(font_name, StringUtil.replace(coupon.getName(), "_", " "), width, 2);
				if (lines.length == 2)
					name = lines[0] + "\n" + lines[1];
				else if (lines.length == 1)
					name = lines[0];
				else
					name = "";
			}
			else
			{
				name = "";
			}

			if (coupon.getValue() != null)
			{
				double value = Double.parseDouble(coupon.getValue()) / 100;
				price = new Formatter().formatNumber(value, 2);
			}
			else
			{
				price = "0.00";
			}

			setImage(default_image);
			
			if (PersistentStoreHelper.shouldLoadImages() == true)
			{
				ImageLoader.loadImage(coupon.getImageURL(), this);
			}
		}
		public int getPreferredHeight()
		{
			return ResourceHelper.convert(115);
		}

		public int getPreferredWidth()
		{
			return Display.getWidth();
		}

		protected void layout(int width, int height)
		{
			setExtent(getPreferredWidth(), getPreferredHeight());
		}

		protected void paint(Graphics graphics)
		{
			graphics.setColor(ResourceHelper.color_white);
			graphics.fillRect(0, 0, text_origin, getHeight());

			int x, y;
			if (image != null)
			{
				x = padding_x + (text_origin - (padding_x * 2) - image.getWidth()) / 2;
				y = padding_y + (getPreferredHeight() - (padding_y * 2) - image.getHeight()) / 2;
				graphics.drawBitmap(x, y, image.getWidth(), image.getHeight(), image, 0, 0);
			}

			x = text_origin + padding_x;
			y = padding_y;
			graphics.setFont(font_save);
			graphics.setColor(ResourceHelper.color_primary);
			graphics.drawText("Save", x, y);

			y += font_save.getBaseline() - font_rand.getLeading() + ResourceHelper.convert(5);
			graphics.setFont(font_cent);
			int x1 = x;
			graphics.drawText(StringHelper.currency_symbol, x1, y + font_rand.getLeading() - font_cent.getLeading());
			x1 += graphics.getFont().getAdvance(StringHelper.currency_symbol);
			graphics.setFont(font_rand);
			String[] pricetokens = StringUtil.split(price, ".");
			graphics.drawText(pricetokens[0], x1, y);
			x1 += font_rand.getAdvance(pricetokens[0]);
			graphics.setFont(font_cent);
			graphics.drawText(pricetokens[1], x1, y + font_rand.getLeading() - font_cent.getLeading());

			y += font_rand.getHeight();
			graphics.setFont(font_name);
			graphics.setColor(ResourceHelper.color_black);

			String[] tokens = StringUtil.split(name, "\n");
			for (int i = 0; i < tokens.length; i++)
			{
				graphics.drawText(tokens[i], x, y);
				y += font_name.getHeight();
			}

			if (coupon.isChecked())
			{
				graphics.setColor(ResourceHelper.color_white);
				graphics.setGlobalAlpha(160);
				graphics.fillRect(0, 0, text_origin, getHeight());
				graphics.setGlobalAlpha(255);

				x = padding_x + (text_origin - (padding_x * 2) - tick.getWidth()) / 2;
				y = padding_y + (getPreferredHeight() - (padding_y * 2) - tick.getHeight()) / 2;
				graphics.drawBitmap(x, y, tick.getWidth(), tick.getHeight(), tick, 0, 0);
			}

			graphics.setColor(ResourceHelper.color_grey);
			graphics.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
		}

		public void setImage(Bitmap image)
		{
			if (image == null) return;
			if (image.getWidth() > text_origin - (padding_x * 2))
			{
				image = BitmapTools.resizeTransparentBitmap(image, text_origin - (padding_x * 2), 0, Bitmap.FILTER_BILINEAR, Bitmap.SCALE_STRETCH);
			}
			if (image.getHeight() > getPreferredHeight() - (padding_y * 2))
			{
				image = BitmapTools.resizeTransparentBitmap(image, 0, getPreferredHeight() - (padding_y * 2), Bitmap.FILTER_BILINEAR, Bitmap.SCALE_STRETCH);
			}

			this.image = image;
			invalidate();
		}
	}

	private void logAddToMyList(CampaignData coupon)
	{
		Hashtable eventParams = new Hashtable();

		eventParams.put(FlurryHelper.PARAM_COUPON_MONETARY_VALUE, coupon.getValue());
		eventParams.put(FlurryHelper.PARAM_CATEGORY, ((CouponCategory)coupon.getCategoryList().elementAt(0)).getId());
		FlurryHelper.addProvinceParam(eventParams);
		eventParams.put(FlurryHelper.PARAM_TIME, FlurryHelper.getFlurryFormatDate(Calendar.getInstance()));
		eventParams.put(FlurryHelper.PARAM_PRODUCT_NAME, coupon.getId());
		FlurryHelper.addFirstLaunchParam(eventParams);

		FlurryHelper.logEvent(FlurryHelper.EVENT_ADD_EEZI_COUPON_TO_LIST, eventParams, false);
	}

	private void logSelectsRelatedProduct(CampaignData coupon)
	{
		Hashtable eventParams = new Hashtable();

		eventParams.put(FlurryHelper.PARAM_CATEGORY, ((CouponCategory)coupon.getCategoryList().elementAt(0)).getId());
		eventParams.put(FlurryHelper.PARAM_COUPON_MONETARY_VALUE, coupon.getValue());
		eventParams.put(FlurryHelper.PARAM_PRODUCT_NAME, coupon.getId());
		FlurryHelper.addLoginParams(eventParams);		

		FlurryHelper.logEvent(FlurryHelper.EVENT_SELECT_RELATED_PRODUCTS, eventParams, false);
	}

	private class LoadThread extends Thread
	{
		public LoadThread()
		{
			setPriority(Thread.NORM_PRIORITY);
		}

		public void run()
		{			
			Vector coupons = RuntimeStoreHelper.getCoupons(interfaceListener, true, "Downloading coupons...", RuntimeStoreHelper.getLastLocationIdUsedForCouponsSearch());
			if (coupons == null) return;//dloading in background

			if (RuntimeStoreHelper.getSessionID() != null)
			{
				PersistentStoreHelper.checkMyListCoupons();
			}
			
			for (int i = 0; i < coupons.size(); i++)
			{
				if (related_manager.getFieldCount() == 10)
				{
					break;
				}
				else
				{
					CampaignData couponTemp = (CampaignData) coupons.elementAt(i);
					final RelatedItem item = new RelatedItem(couponTemp)
					{
						public void clickButton()
						{
							logSelectsRelatedProduct(getCoupon());
							((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(DetailedCouponFragment.FRAGMENT_ID, new Object[]{getCoupon()});
						}
					};

					if (!couponTemp.equals(coupon) && VectorUtil.hasCommonElement(couponTemp.getCategoryList(), coupon.getCategoryList()))
					{
						UiApplication.getUiApplication().invokeLater(new Runnable()
						{
							public void run()
							{
								if (related_manager.getFieldCount() < 10)
								{
									related_manager.add(item);
								}
							}
						});
					}
				}
			}
		};
	}

	public void onCouponsFinishedLoading(boolean success) {
		if (success == true)
		{
			build();
		}
		else
		{
			int choice = CustomDialog.doModal("There was a problem trying to download the coupons...", new String[]{"Cancel", "Retry"}, new int[]{0,1}, 1, false);

			if (choice == 1)
			{
				build();
			}
			else 
			{
				close();
			}
		}
	}
}
