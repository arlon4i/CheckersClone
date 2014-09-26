package fi.bb.checkers.datatypes;

public class UserData
{
	private String titleId;
	private String username;
	private String pin;
	private String firstname;
	private String surname;
	private String cellphone;
	private String email;
	private String birthdate;
	private String provinceId;
	private MerchantData preferredStore;
	private String wicode;

	public UserData()
	{
		setTitleId("");
		setUsername("");
		setPin("");
		setFirstname("");
		setSurname("");
		setCellphone("");
		setEmail("");
		setBirthdate("");
		preferredStore = null;
	}

	public String getTitleId()
	{
		return titleId;
	}

	public void setTitleId(String titleId)
	{
		this.titleId = titleId;
	}
	
	public String getFirstname()
	{
		return firstname;
	}

	public void setFirstname(String firstname)
	{
		this.firstname = firstname;
	}

	public String getSurname()
	{
		return surname;
	}

	public void setSurname(String surname)
	{
		this.surname = surname;
	}

	public String getCellphone()
	{
		return cellphone;
	}

	public void setCellphone(String cellphone)
	{
		this.cellphone = cellphone;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getBirthdate()
	{
		return birthdate;
	}

	public void setBirthdate(String birthdate)
	{
		this.birthdate = birthdate;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPin()
	{
		return pin;
	}

	public void setPin(String pin)
	{
		this.pin = pin;
	}

	public String getProvinceLocationData()
	{
		return provinceId;
	}

	public void setProvinceLocationData(String provinceLocationData)
	{
		this.provinceId = provinceLocationData;
	}

	public String getWicode()
	{
		return wicode;
	}

	public void setWicode(String wicode)
	{
		this.wicode = wicode;
	}

	public MerchantData getPreferredStore()
	{
		return preferredStore;
	}

	public void setPreferredStore(MerchantData preferredStore)
	{
		this.preferredStore = preferredStore;
	}

}
