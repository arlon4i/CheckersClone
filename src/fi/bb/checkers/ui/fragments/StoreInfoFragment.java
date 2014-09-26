package fi.bb.checkers.ui.fragments;

import java.io.IOException;
import java.util.Hashtable;

import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.PhoneArguments;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import fi.bb.checkers.datatypes.MerchantData;
import fi.bb.checkers.datatypes.UserData;
import fi.bb.checkers.helpers.FlurryHelper;
import fi.bb.checkers.helpers.PersistentStoreHelper;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.helpers.RuntimeStoreHelper;
import fi.bb.checkers.helpers.ServerHelper;
import fi.bb.checkers.interfaces.InterfaceStoreChanged;
import fi.bb.checkers.logger.RemoteLogger;
import fi.bb.checkers.prompts.CustomDialog;
import fi.bb.checkers.prompts.InfoDialog;
import fi.bb.checkers.prompts.LoadingDialog;
import fi.bb.checkers.ui.components.LabelField;
import fi.bb.checkers.ui.components.TextImageButton;
import fi.bb.checkers.ui.screens.ViewPagerScreen;
import fi.bb.checkers.utils.AsyncTask;
import fi.bb.checkers.utils.StringUtil;

public class StoreInfoFragment extends Fragment implements FieldChangeListener
{
	public static final int FRAGMENT_ID = getUUID();

	public static final String BUTTON_PREFFERED_TEXT_REMOVE = "Remove as Preferred";
	public static final String BUTTON_PREFFERED_TEXT_ADD = "Add as Preferred";

	TextImageButton button_call;
	TextImageButton button_preferred;
	String telnumber = null;
	MerchantData merchant;

	private boolean from_profile_view;
	InterfaceStoreChanged storeChangedInterface;

	public StoreInfoFragment(MerchantData merchant)
	{
		this(merchant, false, null);
	}

	public StoreInfoFragment(MerchantData merchant, boolean from_profile_view, InterfaceStoreChanged storeChangedInterface)
	{
		super(NO_HORIZONTAL_SCROLL | VERTICAL_SCROLL);

		int padding = ResourceHelper.convert(12);
		setPadding(padding, padding, 0, padding);

		downloadTask.execute(new Object[]{merchant});
		this.merchant = merchant;
		this.from_profile_view = from_profile_view;
		this.storeChangedInterface = storeChangedInterface;
	}

	private void build(MerchantData merchant)
	{
		deleteAll();

		RemoteLogger.log("MERCHANT_DEBUG", "merchant"+merchant);
		RemoteLogger.log("MERCHANT_DEBUG", "merchant.getName()"+merchant.getName());
		RemoteLogger.log("MERCHANT_DEBUG", "merchant.getPhysicalAddress()"+merchant.getPhysicalAddress());
		RemoteLogger.log("MERCHANT_DEBUG", "merchant.getContactDetails()"+merchant.getContactDetails());
		RemoteLogger.log("MERCHANT_DEBUG", "merchant.getTradingHours()"+merchant.getTradingHours());

		Font font = ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(16), Ui.UNITS_px);
		LabelField label;

		if (merchant.getName()!=null)
		{
			label = new LabelField(merchant.getName(), ResourceHelper.color_black, 0);
			label.setFont(font);
			add(label);
		}

		if (merchant.getPhysicalAddress()!=null)
		{
			label = new LabelField(merchant.getPhysicalAddress(), ResourceHelper.color_grey, 0);
			label.setFont(font);
			add(label);
		}

		String[] tokens;
		int widest;

		if (merchant.getContactDetails()!=null)
		{
			tokens = StringUtil.split(merchant.getContactDetails(), ",");
			widest = 0;

			if (tokens.length > 0)
			{
				label = new LabelField("Manager", ResourceHelper.color_black, 0);
				label.setMargin(ResourceHelper.convert(24), 0, 0, 0);
				label.setFont(font);
				add(label);

				for (int i = 0; i < tokens.length; i++)
				{
					if (tokens[i].indexOf(':') != -1)
					{
						widest = Math.max(widest, font.getAdvance(StringUtil.split(tokens[i], ":")[0].trim() + ":"));
					}
				}
				for (int i = 0; i < tokens.length; i++)
				{
					int index = tokens[i].indexOf(':');
					if (index == -1)
					{
						label = new LabelField(tokens[i], ResourceHelper.color_grey, 0);
						label.setFont(font);
						add(label);
					}
					else
					{
						if (i == 0)
						{
							// if there wasn't a manager name previously
							label = new LabelField("No contact information.", ResourceHelper.color_grey, 0);
							label.setFont(font);
							add(label);
						}

						String[] contact_detail = StringUtil.split(tokens[i], ":");

						HorizontalFieldManager contact_manager = new HorizontalFieldManager(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL);
						label = new LabelField(contact_detail[0].trim() + ":", ResourceHelper.color_black, 0);
						label.setFont(font);
						label.setMargin(0, widest - font.getAdvance(contact_detail[0].trim() + ":") + ResourceHelper.convert(20), 0, 0);
						contact_manager.add(label);

						String number = contact_detail[1];
						number = StringUtil.removeNonDigit(number);
						label = new LabelField(StringUtil.formatDivisions(3, number), ResourceHelper.color_grey, 0);
						label.setFont(font);
						contact_manager.add(label);

						add(contact_manager);

						if (contact_detail[0].indexOf("Tel") != -1) telnumber = number;
					}
				}

				if (telnumber != null)
				{
					button_call = new TextImageButton("Call", "btn_sml_default", "btn_sml_hover");
					button_call.setChangeListener(this);
					button_call.setTextColor(ResourceHelper.color_white);
					button_call.setTextColorHover(ResourceHelper.color_primary);
					button_call.setTextColorPressed(ResourceHelper.color_primary);
					button_call.setMargin(ResourceHelper.convert(5), 0, 0, 0);
					add(button_call);
				}
			}
		}

		if (merchant.getTradingHours()!=null)
		{
			tokens = StringUtil.split(merchant.getTradingHours(), ",");
			widest = 0;
			if (tokens.length > 0)
			{
				label = new LabelField("Hours", ResourceHelper.color_black, 0);
				label.setMargin(ResourceHelper.convert(24), 0, 0, 0);
				label.setFont(font);
				add(label);

				for (int i = 0; i < tokens.length; i += 3)
				{
					widest = Math.max(widest, font.getAdvance(tokens[i] + ":"));
				}

				for (int i = 0; i < tokens.length; i += 3)
				{
					HorizontalFieldManager time_manager = new HorizontalFieldManager(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL);
					label = new LabelField(tokens[i] + ":", ResourceHelper.color_grey, 0);
					label.setFont(font);
					time_manager.add(label);

					int margin = widest - font.getAdvance(tokens[i] + ":") + ResourceHelper.convert(20);
					label.setMargin(0, margin, 0, 0);

					label = new LabelField(tokens[i + 1] + " - " + tokens[i + 2], ResourceHelper.color_grey, 0);
					label.setFont(font);
					time_manager.add(label);
					add(time_manager);
				}
			}
		}
		button_preferred = new TextImageButton(RuntimeStoreHelper.getSessionID() != null && this.merchant.equals(RuntimeStoreHelper.getUserData().getPreferredStore())
				? BUTTON_PREFFERED_TEXT_REMOVE
						: BUTTON_PREFFERED_TEXT_ADD, "btn_default", "btn_hover");
		button_preferred.setChangeListener(this);
		button_preferred.setTextColor(ResourceHelper.color_white);
		button_preferred.setTextColorHover(ResourceHelper.color_primary);
		button_preferred.setTextColorPressed(ResourceHelper.color_primary);
		button_preferred.setMargin(ResourceHelper.convert(5), 0, ResourceHelper.convert(12), 0);
		add(button_preferred);
	}

	public void onClose() 
	{
		super.onClose();
	}

	public void makeMenu(Menu menu)
	{
		MenuItem item;

		if (RuntimeStoreHelper.getSessionID() != null)
		{
			if (from_profile_view == true)
			{
				if (button_preferred.getText().equals(BUTTON_PREFFERED_TEXT_REMOVE) == true)
				{
					remove();
				}
				else
				{
					add();
				}
			}
			else
			{
				if (RuntimeStoreHelper.getUserData().getPreferredStore() != null && merchant.equals(RuntimeStoreHelper.getUserData().getPreferredStore()))
				{
					item = new MenuItem(BUTTON_PREFFERED_TEXT_REMOVE, 0x00070000, 0)
					{
						public void run()
						{
							remove();
						}
					};
					menu.add(item);
				}
				else
				{
					item = new MenuItem(BUTTON_PREFFERED_TEXT_ADD, 0x00070000, 0)
					{
						public void run()
						{
							if (RuntimeStoreHelper.getSessionID() == null)
								((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).profileClick();
							else
								add();
						}
					};
					menu.add(item);
				}
			}
		}

		if (telnumber != null)
		{
			item = new MenuItem("Call Store", 0x00070000, 1)
			{
				public void run()
				{
					call();
				}
			};
			menu.add(item);
		}

		item = new MenuItem("Call Checkline Consultant", 0x00070000, 2)
		{
			public void run()
			{
				((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(ChecklineFragment.FRAGMENT_ID, null);
			}
		};
		menu.add(item);
	}

	private void call()
	{
		int choice = CustomDialog.doModal(StringUtil.formatDivisions(3, telnumber), new String[]{"Cancel", "Call"}, new int[]{Dialog.CANCEL, Dialog.YES});
		if (choice == Dialog.YES)
		{
			UiApplication.getUiApplication().invokeLater(new Runnable()
			{
				public void run()
				{
					try
					{
						PhoneArguments call = new PhoneArguments(PhoneArguments.ARG_CALL, telnumber);
						Invoke.invokeApplication(Invoke.APP_TYPE_PHONE, call);

					} catch (Exception e)
					{
						RemoteLogger.log("StoreInfoFragment", "Invoke.APP_TYPE_PHONE: " + e.toString());
						InfoDialog.doModal("Error", e.getMessage(), "Okay");
					}
				}
			});
		}
	}

	private void remove()
	{
		if (from_profile_view==false)
		{
			UserData profile = new UserData();
			profile.setTitleId(RuntimeStoreHelper.getUserData().getTitleId());
			profile.setUsername(PersistentStoreHelper.getUsername());
			profile.setFirstname(RuntimeStoreHelper.getUserData().getFirstname());
			profile.setSurname(RuntimeStoreHelper.getUserData().getSurname());
			profile.setCellphone(RuntimeStoreHelper.getUserData().getCellphone());
			profile.setEmail(RuntimeStoreHelper.getUserData().getEmail() == null ? "" : RuntimeStoreHelper.getUserData().getEmail());
			profile.setBirthdate(RuntimeStoreHelper.getUserData().getBirthdate());
			profile.setProvinceLocationData(RuntimeStoreHelper.getUserData().getProvinceLocationData());
			profile.setPreferredStore(null);
			profile.setWicode(RuntimeStoreHelper.getUserData().getWicode());
			new UpdateTask().execute(new Object[]{profile});
		}
		else
		{
			button_preferred.setText(BUTTON_PREFFERED_TEXT_ADD);
			if (storeChangedInterface != null)
			{
				storeChangedInterface.onStoreChanged(null);
			}
		}
	}

	private void add()
	{		
		if (from_profile_view==false)
		{
			UserData profile = new UserData();
			profile.setTitleId(RuntimeStoreHelper.getUserData().getTitleId());
			profile.setUsername(PersistentStoreHelper.getUsername());
			profile.setFirstname(RuntimeStoreHelper.getUserData().getFirstname());
			profile.setSurname(RuntimeStoreHelper.getUserData().getSurname());
			profile.setCellphone(RuntimeStoreHelper.getUserData().getCellphone());
			profile.setEmail(RuntimeStoreHelper.getUserData().getEmail() == null ? "" : RuntimeStoreHelper.getUserData().getEmail());
			profile.setBirthdate(RuntimeStoreHelper.getUserData().getBirthdate());
			profile.setProvinceLocationData(RuntimeStoreHelper.getUserData().getProvinceLocationData());
			profile.setPreferredStore(merchant);
			profile.setWicode(RuntimeStoreHelper.getUserData().getWicode());
			new UpdateTask().execute(new Object[]{profile});
		}
		else
		{
			button_preferred.setText(BUTTON_PREFFERED_TEXT_REMOVE);
			if (storeChangedInterface != null)
			{
				storeChangedInterface.onStoreChanged(merchant);
			}
		}
	}

	private void logAddAsPrefferedStore(MerchantData merchant)
	{
		Hashtable eventParams = new Hashtable();

		eventParams.put(FlurryHelper.PARAM_STORE_ID, merchant.getId());
		try
		{
			eventParams.put(FlurryHelper.PARAM_PROVINCE, merchant.getProvince());
		}
		catch (Exception e)
		{
			//no province data for merchant
		}

		FlurryHelper.logEvent(FlurryHelper.EVENT_PREFFERED_STORE, eventParams, false);
	}

	public void fieldChanged(Field field, int context)
	{
		if (field == button_call)
		{
			call();
		}
		else if (field == button_preferred)
		{
			if (RuntimeStoreHelper.getSessionID() == null)
			{
				((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).profileClick();
				return;
			}

			if (from_profile_view == true)
			{
				if (button_preferred.getText().equals(BUTTON_PREFFERED_TEXT_REMOVE) == true)
				{
					remove();
				}
				else
				{
					add();
				}
			}
			else
			{
				if (merchant.equals(RuntimeStoreHelper.getUserData().getPreferredStore()))
				{
					remove();
				}
				else
				{
					add();
				}
			}
		}
	}

	private AsyncTask downloadTask = new AsyncTask()
	{
		LoadingDialog prompt;

		protected void onPreExecute()
		{
			super.onPreExecute();
			prompt = LoadingDialog.push("Loading");
		}

		protected void onPostExecute(Object result)
		{
			super.onPostExecute(result);
			prompt.close();

			if (result instanceof MerchantData)
			{
				build((MerchantData) result);
			}
			else
			{
				String msg = ((Exception) result).getMessage();
				if (msg.length() == 0) msg = "An unexpected error occured.";
				InfoDialog.doModal("Error", msg, "Okay");

				close();
			}
		}

		public Object doInBackground(Object[] params)
		{
			try
			{
				return ServerHelper.getStoreInfo((MerchantData) params[0]);
			} catch (IOException e)
			{
				return e;
			}
		}
	};

	private class UpdateTask extends AsyncTask
	{
		LoadingDialog dialog;
		protected void onPreExecute()
		{
			super.onPreExecute();
			dialog = LoadingDialog.push("Updating...");
		}

		protected void onPostExecute(Object result)
		{
			super.onPostExecute(result);
			dialog.close();
			
			if (result instanceof UserData)
			{				
				String matchStoreName = "";
				
				if (RuntimeStoreHelper.getUserData().getPreferredStore() != null)
				{
					matchStoreName = RuntimeStoreHelper.getUserData().getPreferredStore().getName();
					logAddAsPrefferedStore(merchant);//log "add_as_preffered_store" here since a null would mean removed store
				}
				
				button_preferred.setText(merchant.getName().equals(matchStoreName) ? BUTTON_PREFFERED_TEXT_REMOVE : BUTTON_PREFFERED_TEXT_ADD);
				invalidate();
			}
			else
				if (result instanceof String)
				{
					InfoDialog.doModal("Error", (String) result, "Okay");
				}
				else if (result instanceof Exception)
				{
					InfoDialog.doModal(((Exception) result).getClass().getName(), ((Exception) result).getMessage(), "Okay");
				}
		}

		public Object doInBackground(Object[] params)
		{
			try
			{
				String response = ServerHelper.updateUserDetails((UserData) params[0]);
				
				if (response.equals("success") == true) return params[0];

				return response;
			} catch (Exception e)
			{
				RemoteLogger.log("StoreInfoFragment", "UpdateTask: " + e.toString());
				return e;
			}
		}
	}
}
