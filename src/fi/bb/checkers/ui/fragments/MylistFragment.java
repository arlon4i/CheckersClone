package fi.bb.checkers.ui.fragments;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.global.Formatter;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import fi.bb.checkers.datatypes.CampaignData;
import fi.bb.checkers.helpers.FlurryHelper;
import fi.bb.checkers.helpers.PersistentStoreHelper;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.helpers.StringHelper;
import fi.bb.checkers.prompts.CustomDialog;
import fi.bb.checkers.ui.components.LabelField;
import fi.bb.checkers.ui.components.MylistField;
import fi.bb.checkers.ui.components.TextImageButton;
import fi.bb.checkers.ui.screens.TutorialScreen;
import fi.bb.checkers.ui.screens.ViewPagerScreen;

public class MylistFragment extends Fragment implements FieldChangeListener
{
	public static final int FRAGMENT_ID = getUUID();

	TextImageButton button_redeem;
	LabelField label_savings;
	LabelField label;
	HorizontalFieldManager banner_manager;
	VerticalFieldManager content;

	public MylistFragment()
	{
		super(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL);
		setBackground(BackgroundFactory.createSolidBackground(ResourceHelper.color_background_app));

		banner_manager = new HorizontalFieldManager()
		{
			public int getPreferredHeight()
			{
				return ResourceHelper.convert(30);
			}

			public int getPreferredWidth()
			{
				return Display.getWidth();
			}

			protected void sublayout(int width, int height)
			{
				width = getPreferredWidth();
				height = getPreferredHeight();
				super.sublayout(width, height);
				setExtent(width, height);
			}

			protected void paint(Graphics graphics)
			{
				graphics.setColor(0xe2e2e2);
				graphics.fillRect(0, 0, getWidth(), getHeight());

				super.paint(graphics);
			}
		};

		label = new LabelField("Total EeziCoupon Savings:", ResourceHelper.color_black, 0);
		label.setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(14), Ui.UNITS_px));
		label.setMargin((banner_manager.getPreferredHeight() - label.getPreferredHeight()) / 2, 0, 0, ResourceHelper.convert(5));

		label_savings = new LabelField(StringHelper.currency_symbol+"0.00", ResourceHelper.color_black, 0);
		label_savings.setFont(ResourceHelper.helveticaMed().getFont(Font.PLAIN, ResourceHelper.convert(14), Ui.UNITS_px));
		label_savings.setMargin((banner_manager.getPreferredHeight() - label_savings.getPreferredHeight()) / 2, ResourceHelper.convert(5), 0, ResourceHelper.convert(5));

		button_redeem = new TextImageButton("Redeem", "btn_sml_default", "btn_sml_hover");
		button_redeem.setTextColor(ResourceHelper.color_white);
		button_redeem.setTextColorHover(ResourceHelper.color_primary);
		button_redeem.setTextColorPressed(ResourceHelper.color_primary);

		button_redeem.setMargin((banner_manager.getPreferredHeight() - button_redeem.getPreferredHeight()) / 2, 0, 0,
				Display.getWidth() - label.getPreferredWidth() - label_savings.getPreferredWidth() - button_redeem.getPreferredWidth() - ResourceHelper.convert(10));

		button_redeem.setChangeListener(this);

		banner_manager.add(label);
		banner_manager.add(label_savings);
		banner_manager.add(button_redeem);
		add(banner_manager);

		content = new VerticalFieldManager(VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL)
		{
			protected void paint(Graphics graphics)
			{
				super.paint(graphics);

				//graphics.drawBitmap(0, content.getVerticalScroll(), shadow.getWidth(), shadow.getHeight(), shadow, 0, 0);
			}
		};
		content.setScrollListener(this);
		add(content);
	}

	protected void onVisibilityChange(boolean visible)
	{
		if (visible)
		{
			try
			{
				refresh();
			} catch (NullPointerException e)
			{
				// happens when trying to logout while on the mylist screen, and stops the logout process
			}

			if (PersistentStoreHelper.isShowTutorial5() == true)
			{
				TutorialScreen.push(TutorialScreen.LIST);
				PersistentStoreHelper.setShowTutorial5(false);
			}
		}
		super.onVisibilityChange(visible);
	}

	public void onClose() {
		FlurryHelper.endTimedEvent(FlurryHelper.EVENT_MY_LIST);
		super.onClose();
	}

	public void makeMenu(Menu menu)
	{
		if (content.getFieldCount() == 0) return;

		MenuItem item = new MenuItem("Remove All", 0x00070000, 1)
		{
			public void run()
			{
				int choice = CustomDialog.doModal("Are you sure you want to remove these items from your list?", new String[]{"Cancel", "Okay"}, new int[]{Dialog.CANCEL, Dialog.YES});
				if (choice == Dialog.YES)
				{
					PersistentStoreHelper.mylistClear();
					refresh();
				}
			}
		};
		menu.add(item);

		boolean selected = false;
		int index = content.getFieldWithFocusIndex();
		if (index != -1)
		{
			final MylistField field = (MylistField) content.getField(index);
			item = new MenuItem("View Info", 0x00060000, 0)
			{
				public void run()
				{
					((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(DetailedCouponFragment.FRAGMENT_ID, new Object[]{field.getCoupon()});
				}
			};
			menu.add(item);

			item = new MenuItem("Remove from List", 0x00060000, 1)
			{
				public void run()
				{
					Vector tempCouponList = new Vector();
					for (int i=0; i < content.getFieldCount(); i++)
					{
						if (((MylistField) content.getField(i)).isChecked()==true)
						{
							tempCouponList.addElement(content.getField(i));
						}
					}

					if (tempCouponList.size() > 0)
					{
						String warning = "Are you sure you want to remove this item from your list?";
						if (tempCouponList.size()>1)
						{
							warning = "Are you sure you want to remove these items from your list?";
						}

						int choice = CustomDialog.doModal(warning, new String[]{"Cancel", "Okay"}, new int[]{Dialog.CANCEL, Dialog.YES});
						if (choice == Dialog.YES)
						{
							if (tempCouponList.size()>1)
							{
								for (int i=0; i < tempCouponList.size(); i++)
								{
									PersistentStoreHelper.mylistDelete(((MylistField)tempCouponList.elementAt(i)).getCoupon());
									content.delete((MylistField)tempCouponList.elementAt(i));
								}
							}
							else
							{
								PersistentStoreHelper.mylistDelete(((MylistField)tempCouponList.elementAt(0)).getCoupon());
								content.delete((MylistField)tempCouponList.elementAt(0));
							}

							Vector mylist = PersistentStoreHelper.getMylist();
							double total = 0;
							for (int i = 0; i < mylist.size(); i++)
							{
								CampaignData coupon = (CampaignData) mylist.elementAt(i);
								total += Double.parseDouble(coupon.getValue()) / 100;
							}

							label_savings.setText(StringHelper.currency_symbol + new Formatter().formatNumber(total, 2));
							button_redeem.setMargin((banner_manager.getPreferredHeight() - button_redeem.getPreferredHeight()) / 2, 0, 0,
									Display.getWidth() - label.getPreferredWidth() - label_savings.getPreferredWidth() - button_redeem.getPreferredWidth() - ResourceHelper.convert(20));	
						}
					}
				}
			};
			menu.add(item);

			selected = field.isChecked();
			if (selected)
			{
				item = new MenuItem("Unselect", 0x00060000, 2)
				{
					public void run()
					{
						field.setChecked(false);
					}
				};
				menu.add(item);
			}
			else
			{
				item = new MenuItem("Select", 0x00060000, 2)
				{
					public void run()
					{
						field.setChecked(true);
					}
				};
				menu.add(item);
			}

			//Check all items
			selected = false;
			MylistField tempField;
			for (int i = 0; i < content.getFieldCount(); i++)
			{
				tempField = (MylistField) content.getField(i);
				if (tempField.isChecked() == true)
				{
					selected = true;
					break;
				}
			}
			
			/*Vector mylist = PersistentStoreHelper.getMylist();
			for (int i = 0; i < mylist.size(); i++)
			{
				if (((CampaignData) mylist.elementAt(i)).isChecked())//.isStrikethrough())
				{
					selected = true;
					break;
				}
			}*/

			if (selected == true)//item(s) are selected
			{
				item = new MenuItem("Unselect All", 0x00060000, 2)
				{
					public void run()
					{
						for (int i = 0; i < content.getFieldCount(); i++)
						{
							MylistField field = (MylistField) content.getField(i);
							field.setChecked(false);//setStrikethrough(false);
						}
					}
				};
				menu.add(item);
			}
			else
			{
				item = new MenuItem("Select All", 0x00060000, 2)
				{
					public void run()
					{
						for (int i = 0; i < content.getFieldCount(); i++)
						{
							MylistField field = (MylistField) content.getField(i);
							field.setChecked(true);//setStrikethrough(false);
						}
					}
				};
				menu.add(item);
			}
		}
	}
	
	private void refresh()
	{
		content.deleteAll();

		Vector mylist = PersistentStoreHelper.getMylist();

		double total = 0;
		for (int i = 0; i < mylist.size(); i++)
		{
			CampaignData coupon = (CampaignData) mylist.elementAt(i);			
			content.add(new MylistField(coupon));

			total += Double.parseDouble(coupon.getValue()) / 100;
		}

		label_savings.setText(StringHelper.currency_symbol + new Formatter().formatNumber(total, 2));
		button_redeem.setMargin((banner_manager.getPreferredHeight() - button_redeem.getPreferredHeight()) / 2, 0, 0,
				Display.getWidth() - label.getPreferredWidth() - label_savings.getPreferredWidth() - button_redeem.getPreferredWidth() - ResourceHelper.convert(20));
	}

	public void fieldChanged(Field field, int context)
	{
		if (field == button_redeem)
		{
			logRedeem();
			((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).profileClick();
		}
	}

	private void logRedeem()
	{
		Calendar dateNow = Calendar.getInstance();
		Hashtable eventParams = new Hashtable();

		eventParams.put(FlurryHelper.PARAM_TAPPED, "1");
		eventParams.put(FlurryHelper.PARAM_TOTAL_EEZI_COUPON_SAVINGS, label_savings.getText());
		eventParams.put(FlurryHelper.PARAM_ITEMS, ""+content.getFieldCount());
		eventParams.put(FlurryHelper.PARAM_NON_COUPON_ITEMS, "0");
		eventParams.put(FlurryHelper.PARAM_TIME, FlurryHelper.getFlurryFormatDate(dateNow));
		eventParams.put(FlurryHelper.PARAM_NUMBER_OF_EEZI_COUPONS, ""+content.getFieldCount());
		FlurryHelper.addFirstLaunchParam(eventParams);

		FlurryHelper.logEvent(FlurryHelper.EVENT_REDEEM, eventParams, false);
	}

	/*public void onCouponsFinishedLoading(boolean success) {
		if (success == true)
		{
			refresh();
		}
		else
		{
			int choice = CustomDialog.doModal("There was a problem trying to download the coupons...", new String[]{"Cancel", "Retry"}, new int[]{0,1}, 1, false);

			if (choice == 1)
			{
				refresh();
			}
			else 
			{
				close();
			}
		}
	}*/

}
