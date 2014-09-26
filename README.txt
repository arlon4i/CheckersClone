Reflection is needed to prevent the app from being dependent on the device's BBM.
The BBMImplementation project needs to be included in the jad before deploying.

1.	Copy contents of 
	..\workspace\checkers-blackberry\deliverables\Web\5.0.0
	..\workspace\BBMImplementation\deliverables\Web\5.0.0
	to the same dir.

2.	run: ..eclipse\plugins\net.rim.ejde.componentpack<api level>\components\bin\UpdateJad.exe -n Checkers_full.jad BBMImplementation.jad
	Checkers_full.jad should now have a reference to BBMImplementation at the bottom

3.	To install to device 
		local -	open Blackberry Desktop Manager and copy the contents of the folder to the device.
			Then on the device, open the file browser and run Checkers_full.jad.
		remote- download Checkers_full.jad in browser as usual.



NOTE. 	The app will still work when deploying from eclipse, with the lack of BBM obviously.
	Checkers_full.jad can be renamed for deployment.