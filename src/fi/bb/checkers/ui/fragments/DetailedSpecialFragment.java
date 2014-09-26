package fi.bb.checkers.ui.fragments;

import java.util.Vector;

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
import fi.bb.checkers.datatypes.CampaignData;
import fi.bb.checkers.helpers.PersistentStoreHelper;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.helpers.SharingHelper;
import fi.bb.checkers.logger.RemoteLogger;
import fi.bb.checkers.ui.components.LabelField;
import fi.bb.checkers.ui.components.RelatedItem;
import fi.bb.checkers.ui.components.SpecialField;
import fi.bb.checkers.ui.components.TextImageButton;
import fi.bb.checkers.ui.screens.ViewPagerScreen;
import fi.bb.checkers.utils.StringUtil;
import fi.bb.checkers.utils.VectorUtil;

public class DetailedSpecialFragment extends Fragment implements FieldChangeListener
{
	public static final int FRAGMENT_ID = getUUID();
	private Vector all_specials;
	private CampaignData special;
	private HorizontalFieldManager related_manager;
	TextImageButton button_share;

	private boolean showRelatedItems;

	public DetailedSpecialFragment(CampaignData special, Vector all_specials)
	{
		this(special, all_specials, true);
	}

	public DetailedSpecialFragment(CampaignData special, Vector all_specials, boolean showRelatedItems)
	{
		super(NO_HORIZONTAL_SCROLL | VERTICAL_SCROLL);
		this.special = special;
		this.all_specials = all_specials; // pass this on because the list is downloaded when the screen is opened
		this.showRelatedItems = showRelatedItems;

		build();
	}

	private void build()
	{
		deleteAll();
		related_manager = null;

		final SpecialField special_item = new SpecialField(special, FIELD_LEFT | Field.NON_FOCUSABLE);
		add(special_item);

		final Font fontLabel = ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(13), Ui.UNITS_px);
		final String[] lines = StringUtil.ellipsize(fontLabel, getSpecialTerms()/*special.getTerms()*/, Display.getWidth(), 2);
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
				return /*fontLabel.getHeight()*/ super.getPreferredHeight() * lines.length;
			}
		};
		int termsMarginTop = ResourceHelper.convert(5);
		int termsMarginBottom = ResourceHelper.convert(10);
		terms.setFont(fontLabel);
		terms.setMargin(termsMarginTop, 0, termsMarginBottom, ResourceHelper.convert(5));
		add(terms);

		button_share = new TextImageButton("Share", "btn_grey_default", "btn_hover");
		button_share.setTextColor(ResourceHelper.color_primary);
		button_share.setTextColorHover(ResourceHelper.color_primary);
		button_share.setTextColorPressed(ResourceHelper.color_primary);
		button_share.setChangeListener(this);

		button_share.setMargin(0, ResourceHelper.convert(5), ResourceHelper.convert(10), ResourceHelper.convert(5));

		HorizontalFieldManager manager = new HorizontalFieldManager();
		manager.add(button_share);
		add(manager);

		if (showRelatedItems == true)
		{
			Vector related_specials = getRelatedSpecials();
			if (!related_specials.isEmpty())
			{
				Field heading = new Field()
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
						graphics.setColor(ResourceHelper.color_lighter_grey);
						graphics.fillRect(0, 0, getWidth(), getHeight());

						int y = (getHeight() - getFont().getHeight()) / 2;
						graphics.setColor(ResourceHelper.color_dark_grey);
						graphics.drawText("OTHER SPECIALS", ResourceHelper.convert(5), y);
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

				/*for (int i = 0; i < related_specials.size(); i++)
				{
					related_manager.add(new RelatedItem((CampaignData) related_specials.elementAt(i))
					{
						public void clickButton()
						{
							((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(DetailedSpecialFragment.FRAGMENT_ID, new Object[]{getCoupon(), all_specials});
						}
					});
				}*/

				/*int margin = getPreferredHeight()
						- (special_item.getPreferredHeight() + terms.getPreferredHeight() + manager.getPreferredHeight() + heading.getPreferredHeight() + related_manager.getPreferredHeight() + ResourceHelper
								.convert(20));
				heading.setMargin(Math.max(0, margin), 0, 0, 0);*/
				
				int headingMargin = 0;
				int spaceLeft = getPreferredHeight() - (special_item.getPreferredHeight() + (terms.getPreferredHeight() + termsMarginTop + termsMarginBottom) + manager.getPreferredHeight() + heading.getPreferredHeight() + related_manager.getPreferredHeight());

				if (spaceLeft > 0)
				{
					headingMargin = spaceLeft;
				}
				else
				{
					headingMargin = ResourceHelper.convert(5);
				}

				//int margin = getPreferredHeight() - (special_item.getPreferredHeight() + (terms.getPreferredHeight() + termsMarginTop + termsMarginBottom) + manager.getPreferredHeight() + heading.getPreferredHeight() + related_manager.getPreferredHeight() + headingMargin);

				heading.setMargin(Math.max(0, headingMargin), 0, 0, 0);

				(new LoadThread()).start();
			}
		}
	}

	public void makeMenu(Menu menu)
	{
		MenuItem item;

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

	private Vector getRelatedSpecials()
	{
		Vector related = new Vector();
		for (int i = 0; i < all_specials.size(); i++)
		{
			CampaignData special = (CampaignData) all_specials.elementAt(i);
			if (!special.equals(this.special) && VectorUtil.hasCommonElement(special.getCategoryList(), this.special.getCategoryList()))
			{
				related.addElement(special);
				if (related.size() == 10) break;
			}
		}

		return related;
	}

	private class LoadThread extends Thread
	{
		public LoadThread()
		{
			setPriority(Thread.NORM_PRIORITY);
		}

		public void run()
		{			
			Vector specials = all_specials;
			if (specials == null) return;//dloading in background

			for (int i = 0; i < specials.size(); i++)
			{
				if (related_manager.getFieldCount() == 10)
				{
					break;
				}
				else
				{
					CampaignData specialTemp = (CampaignData) specials.elementAt(i);
					final RelatedItem item = new RelatedItem(specialTemp)
					{
						public void clickButton()
						{
							((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(DetailedSpecialFragment.FRAGMENT_ID, new Object[]{getCoupon(), all_specials});
						}
					};

					if (!specialTemp.equals(special))
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

	public void fieldChanged(Field arg0, int arg1)
	{
		if (arg0 == button_share)
		{
			SharingHelper.shareSpecial(special);
		}
	}

	private String getSpecialTerms()//Since the server returns an "a" for the specials as terms... should be fixed server side :\
	{
		return "Cannot be exchanged for cash. Offer valid while stocks last.";
	}
}
