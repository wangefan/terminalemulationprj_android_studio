package com.te.UI;

import android.app.Activity;
import android.os.Bundle;
import com.cipherlab.barcode.*;
import com.cipherlab.barcode.decoder.*;
import com.example.terminalemulation.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class barcodetest extends Activity {

	// Create an IntentFilter to get intents which we want
	private IntentFilter filter;
	
	// ReaderManager is using to communicate with Barcode Reader Service
	private com.cipherlab.barcode.ReaderManager mReaderManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//e1 = (EditText)findViewById(R.id.editText1);
		
		 
		
		// ***************************************************//
		// Need to get ReaderManager instance first¡Aor you can't call any APIs of ReaderManager 
		// ***************************************************//
		mReaderManager = ReaderManager.InitInstance(this);
		
		// ***************************************************//
		// Register an IntentFilter
		// Add com.cipherlab.barcode.GeneralString.Intent_SOFTTRIGGER_DATA for software trigger
		// Add com.cipherlab.barcode.GeneralString.Intent_PASS_TO_APP for getting decoded data after disabling Keyboard Emulation
		// Add com.cipherlab.barcode.GeneralString.Intent_READERSERVICE_CONNECTED for knowing apk is connected with Barcode Reader Service
		// ***************************************************//
		filter = new IntentFilter(); 
		filter.addAction(com.cipherlab.barcode.GeneralString.Intent_SOFTTRIGGER_DATA);
		filter.addAction(com.cipherlab.barcode.GeneralString.Intent_PASS_TO_APP);
		filter.addAction(com.cipherlab.barcode.GeneralString.Intent_READERSERVICE_CONNECTED);
		registerReceiver(myDataReceiver, filter);
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		
		// ***************************************************//
		// Unregister Broadcast Receiver before app close
		// ***************************************************//
		unregisterReceiver(myDataReceiver);
		
		// ***************************************************//
		// release(unbind) before app close
		// ***************************************************//
		if (mReaderManager != null)
		{
			mReaderManager.Release();
		}
	}
	
	/// Here is the all API examples
	private void ExeSampleCode()
	{
		
		// ***************************************************//
		// 1. Get barcode scanner type
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
				com.cipherlab.barcode.decoder.BcReaderType myReaderType =  mReaderManager.GetReaderType();	
				e1.setText(myReaderType.toString());
			}
			*/
		}
		
		// ***************************************************//
		// 2. Enable/Disable barcode reader service
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
				com.cipherlab.barcode.decoder.ClResult clRet = mReaderManager.SetActive(false);
				boolean bRet = mReaderManager.GetActive();
				clRet = mReaderManager.SetActive(false);
				bRet = mReaderManager.GetActive();
			}
			*/
		}
		
		// ***************************************************//
		// 3. Get/Set output data format (keystroke)
		//    Decoded data Post-production, add decoded type¡Bdecode data¡Blength prefix/postfix string or return char...etc
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.ReaderOutputConfiguration settings = new ReaderOutputConfiguration();
				
				// step2: this action does mean set output format to default
				mReaderManager.Set_ReaderOutputConfiguration(settings);
				
				// step3: or user can skip step2 to get settings by using Get_ReaderOutputConfiguration
				mReaderManager.Get_ReaderOutputConfiguration(settings);
				
				// step4: or user can skip step2 and step3 to set more detail items directly.
				settings.autoEnterWay = OutputEnterWay.SuffixData;
				settings.autoEnterChar = OutputEnterChar.Return;
				if (settings.enableKeyboardEmulation == Enable_State.FALSE)
					settings.enableKeyboardEmulation = Enable_State.TRUE;
				settings.showCodeLen = Enable_State.TRUE;
				settings.showCodeType= Enable_State.TRUE;
				settings.szPrefixCode=" Data->";
				settings.szSuffixCode="! ";
				settings.useDelim = ':';
				
				// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
				// if user get Err_InvalidParameter, it means user put wrong value into items
				// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
				// if user get S_OK, it means set settings is successful.
				ClResult clRet = mReaderManager.Set_ReaderOutputConfiguration(settings);
				if (ClResult.S_ERR == clRet)
					Toast.makeText(this, "Set_ReaderOutputConfiguration was failed", Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_InvalidParameter == clRet)
					Toast.makeText(this, "Set_ReaderOutputConfiguration was InvalidParameter", Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_NotSupport == clRet)
					Toast.makeText(this, "Set_ReaderOutputConfiguration was NotSupport", Toast.LENGTH_SHORT).show();
				else if (ClResult.S_OK == clRet)
					Toast.makeText(this, "Set_ReaderOutputConfiguration was Ok", Toast.LENGTH_SHORT).show();
				}
			*/
		}
		
		// ***************************************************//
		// 4. Get/Set notification 
		//    Flash LED¡BVibrate¡BBeep sound when decode barcode data
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.NotificationParams settings = new NotificationParams();
				
				// step2: this action does mean get current settings of notificaion
				mReaderManager.Get_NotificationParams(settings);
				
				// step3: or skip step2 to set detail items 
				settings.ReaderBeep = BeepType.Hwandsw;
				settings.enableVibrator = Enable_State.FALSE;
				settings.ledDuration = 500; //ms
				settings.vibrationCounter = 1; //500ms * count
				
				// step4
				// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
				// if user get Err_InvalidParameter, it means user put wrong value into items
				// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
				// if user get S_OK, it means set settings is successful.		
				ClResult clRet = mReaderManager.Set_NotificationParams(settings);
				
				if (ClResult.S_ERR == clRet)
					Toast.makeText(this, "Set_NotificationParams was failed", Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_InvalidParameter == clRet)
					Toast.makeText(this, "Set_NotificationParams was InvalidParameter", Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_NotSupport == clRet)
					Toast.makeText(this, "Set_NotificationParams was NotSupport", Toast.LENGTH_SHORT).show();
				else if (ClResult.S_OK == clRet)
					Toast.makeText(this, "Set_NotificationParams was Ok", Toast.LENGTH_SHORT).show();
			}
			*/
		}
		
		// ***************************************************//
		// 5. get/set UserPreference 
		//    For example, get/set scan duration time¡Bsecurity level¡Bredundancy level¡Ketc
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
				com.cipherlab.barcode.decoder.BcReaderType myReaderType =  mReaderManager.GetReaderType();
				
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.UserPreference settings = new UserPreference();
				
				// step2: this action does mean get current settings of UserPreference
				mReaderManager.Get_UserPreferences(settings);
				
				// step3: items are not supported exactly, so user can check...
				if (Enable_State.NotSupport == settings.displayMode)
				{
					//1D does not support
				}
				settings.addonSecurityLevel = 7;
				settings.laserOnTime = 3000;
				settings.negativeBarcodes = InverseType.AutoDetect;
				settings.scanAngle = ScanAngleType.Wide;
				settings.securityLevel=SecurityLevel.Two;
				settings.pickListMode=Enable_State.TRUE;
				settings.timeoutBetweenSameSymbol = 1000;
				settings.displayMode = Enable_State.TRUE;
				settings.redundancyLevel = RedundancyLevel.Four;
				settings.transmitCodeIdChar = TransmitCodeIDType.AimCodeId;
				settings.triggerMode = TriggerType.ContinuousMode;
				settings.interCharGapSize = InterCharacterGapSize.Normal;
				settings.decodingAimingPattern = Enable_State.TRUE;
				settings.decodingIllumination  = Enable_State.FALSE;

				
				// step4
				// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
				// if user get Err_InvalidParameter, it means user put wrong value into items
				// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
				// if user get S_OK, it means set settings is successful.		
				ClResult clRet = mReaderManager.Set_UserPreferences(settings);
				if (ClResult.S_ERR == clRet)
					Toast.makeText(this, "Get_UserPreferences was failed", Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_InvalidParameter == clRet)
					Toast.makeText(this, "Get_UserPreferences was InvalidParameter",	Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_NotSupport == clRet)
					Toast.makeText(this, "Get_UserPreferences was NotSupport", Toast.LENGTH_SHORT).show();
				else if (ClResult.S_OK == clRet)
					Toast.makeText(this, "Get_UserPreferences was successful", Toast.LENGTH_SHORT).show();
				}
			*/
		}
		
		
		// ***************************************************//
		// 6. get/set status(enable or disable) of all symbologies
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoder.Decoders settings = new Decoders();
	
				// step2: get status of all symbologies
				mReaderManager.Get_Decoders_Status(settings);
	
				// step3: Not all items are supported exactly, so user can check first...
				if (Enable_State.NotSupport == settings.enableAustrailianPostal) {
					// 1D does not support
				}
				
				// example: disable all
				settings.enableAustrailianPostal = Enable_State.FALSE;
				settings.enableAztec = Enable_State.FALSE;
				settings.enableChinese2Of5 = Enable_State.FALSE;
				settings.enableCodabar = Enable_State.FALSE;
				settings.enableCode11 = Enable_State.FALSE;
				settings.enableCode128 = Enable_State.FALSE;
				settings.enableCode39 = Enable_State.FALSE;
				settings.enableCode93 = Enable_State.FALSE;
				settings.enableCompositeCC_AB = Enable_State.FALSE;
				settings.enableCompositeCC_C = Enable_State.FALSE;
				settings.enableCompositeTlc39 = Enable_State.FALSE;
				settings.enableDataMatrix = Enable_State.FALSE;
				settings.enableDutchPostal = Enable_State.FALSE;
				settings.enableEanJan13 = Enable_State.FALSE;
				settings.enableEanJan8 = Enable_State.FALSE;
				settings.enableGs1128 = Enable_State.FALSE;
				settings.enableGs1DataBar14 = Enable_State.FALSE;
				settings.enableGs1DataBarExpanded = Enable_State.FALSE;
				settings.enableGs1DataBarLimited = Enable_State.FALSE;
				settings.enableGs1DatabarToUpcEan = Enable_State.FALSE;
				
				settings.enableIndustrial2Of5 = Enable_State.FALSE;
				settings.enableInterleaved2Of5 = Enable_State.FALSE;
				settings.enableIsbt128 = Enable_State.FALSE;
				settings.enableJapanPostal = Enable_State.FALSE;
				settings.enableKorean3Of5 = Enable_State.FALSE;
				settings.enableMatrix2Of5 = Enable_State.FALSE;
				settings.enableMaxiCode = Enable_State.FALSE;
				settings.enableMicroPDF417 = Enable_State.FALSE;
				settings.enableMicroQR = Enable_State.FALSE;
				settings.enableMsi = Enable_State.FALSE;
				settings.enablePDF417 = Enable_State.FALSE;
				settings.enableQRcode = Enable_State.FALSE;
				settings.enableTriopticCode39 = Enable_State.FALSE;
				
				settings.enableUccCoupon = Enable_State.FALSE;
				settings.enableUKPostal = Enable_State.FALSE;
				settings.enableUpcA = Enable_State.FALSE;
				settings.enableUpcE = Enable_State.FALSE;
				settings.enableUpcE1 = Enable_State.FALSE;
				settings.enableUPUFICSPostal = Enable_State.FALSE;
				settings.enableUSPlanet = Enable_State.FALSE;
				settings.enableUSPostnet = Enable_State.FALSE;
				settings.enableUSPSPostal = Enable_State.FALSE;
	
				// step4
				// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
				// if user get Err_InvalidParameter, it means user put wrong value into items
				// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
				// if user get S_OK, it means set settings is successful.			
				ClResult clRet = mReaderManager.Set_Decoders_Status(settings);
				if (ClResult.S_ERR == clRet)
					Toast.makeText(this, "Set_Decoders_Status was failed", Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_InvalidParameter == clRet)
					Toast.makeText(this, "Set_Decoders_Status was InvalidParameter",	Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_NotSupport == clRet)
					Toast.makeText(this, "Set_Decoders_Status was NotSupport", Toast.LENGTH_SHORT).show();
				else if (ClResult.S_OK == clRet)
					Toast.makeText(this, "Set_Decoders_Status was successful", Toast.LENGTH_SHORT).show();
				
				// you can set to default after doing steps above 
				//mReaderManager.Set_Decoders_Status(new Decoders());
			}
			*/
		}
			
		
		// ***************************************************//
		// 7-0. get/set CodaBar
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{ 
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.Codabar settings = new Codabar();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
	
				// step3: if barcode scanner support this symbology¡Athen user can change attribute
				settings.clsiEditing = Enable_State.FALSE;
				settings.enable = Enable_State.TRUE;
				settings.length1 = 4;
				settings.length2 = 55;
				settings.notisEditing = Enable_State.FALSE;
	
				// step4
				// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
				// if user get Err_InvalidParameter, it means user put wrong value into items
				// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
				// if user get S_OK, it means set settings is successful.			
				ClResult clRet = mReaderManager.Set_Symbology(settings);
				if (ClResult.S_ERR == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_InvalidParameter == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_NotSupport == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
				else if (ClResult.S_OK == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
			}
			*/
		}
		
		// ***************************************************//
		// 7-1. get/set Industrial 2 Of 5 (same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.Industrial2Of5 settings = new Industrial2Of5();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
	
				// step3: if barcode scanner support this symbology¡Athen user can change attribute
				settings.enable = Enable_State.TRUE;
				settings.length1 = 5;
				settings.length2 = 30;
	
				// step4
				// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
				// if user get Err_InvalidParameter, it means user put wrong value into items
				// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
				// if user get S_OK, it means set settings is successful.			
				ClResult clRet = mReaderManager.Set_Symbology(settings);
				if (ClResult.S_ERR == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_InvalidParameter == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_NotSupport == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
				else if (ClResult.S_OK == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
			}
			*/
			
		}
		
		// ***************************************************//
		// 7-2. get/set Interleaved 2 Of 5(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{		
				com.cipherlab.barcode.decoder.BcReaderType myReaderType =  mReaderManager.GetReaderType();
				
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.Interleaved2Of5 settings = new Interleaved2Of5();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
	
				// step3: if barcode scanner support this symbology¡Athen user can change attribute
				settings.enable = Enable_State.TRUE;
				settings.length1 = 5;
				settings.length2 = 30;
				settings.convertToEan13 = Enable_State.FALSE;
				settings.transmitCheckDigit =Enable_State.FALSE;
				settings.checkDigitVerification = I20f5CheckDigitVerification.OPCC;
				
				
				// step4
				// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
				// if user get Err_InvalidParameter, it means user put wrong value into items
				// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
				// if user get S_OK, it means set settings is successful.
				ClResult clRet = mReaderManager.Set_Symbology(settings);
				if (ClResult.S_ERR == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_InvalidParameter == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_NotSupport == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
				else if (ClResult.S_OK == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
					
			}
			*/
			
		}
		
		// ***************************************************//
		// 7-3. set/get Composite Symbology(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.Composite settings = new Composite();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
	
				// step3: ­×§ï²Ó¶µ
				settings.enableCc_AB = Enable_State.FALSE;
				settings.enableCc_C = Enable_State.FALSE;
				settings.enableEmulationMode = Enable_State.FALSE;
				settings.enableTlc39 = Enable_State.TRUE;
				settings.enableUpcMode = UpcMode.NeverLinksUPC;
				
				
				// step4
				// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
				// if user get Err_InvalidParameter, it means user put wrong value into items
				// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
				// if user get S_OK, it means set settings is successful.
				ClResult clRet = mReaderManager.Set_Symbology(settings);
				if (ClResult.S_ERR == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_InvalidParameter == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_NotSupport == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
				else if (ClResult.S_OK == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
				
			}
			*/
			
		}
		
		// ***************************************************//
		//  7-4. get/set Chinese 2 of 5(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{	
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.Chinese2Of5 settings = new Chinese2Of5();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
	
				// step3: if barcode scanner support this symbology¡Athen user can change attribute
				settings.enable = Enable_State.TRUE;
				
				
				// step4
				// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
				// if user get Err_InvalidParameter, it means user put wrong value into items
				// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
				// if user get S_OK, it means set settings is successful.
				ClResult clRet = mReaderManager.Set_Symbology(settings);
				if (ClResult.S_ERR == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_InvalidParameter == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_NotSupport == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
				else if (ClResult.S_OK == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
				
			}
			*/
			
		}		
		
		// ***************************************************//
		// 7-5. get/set Matrix 2 of 5(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{	
				
				// step1: new a class, the object is set to default value 
				com.cipherlab.barcode.decoderparams.Matrix2Of5 settings = new Matrix2Of5();
				 
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
				  
				// step3: if barcode scanner support this symbology¡Athen user can change attribute 
				settings.enable =Enable_State.TRUE; 
				settings.checkDigitVerification = Enable_State.FALSE; 
				settings.transmitCheckDigit = Enable_State.FALSE; 
				settings.redundancy = Enable_State.TRUE;
				settings.length1 = 5;
				  
				// step4
				// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
				// if user get Err_InvalidParameter, it means user put wrong value into items
				// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
				// if user get S_OK, it means set settings is successful.
				ClResult clRet = mReaderManager.Set_Symbology(settings);
				if (ClResult.S_ERR == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_InvalidParameter == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_NotSupport == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
				else if (ClResult.S_OK == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
			}
			*/
			
		}

		// ***************************************************//
		//  7-6. get/set Code 39(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{	
				
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.Code39 settings = new Code39();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
	
				// step3: if barcode scanner support this symbology¡Athen user can change attribute
				settings.enable = Enable_State.TRUE;
				settings.fullASCII = Enable_State.TRUE;
				settings.checkDigitVerification = Enable_State.FALSE;
				settings.transmitCheckDigit = Enable_State.FALSE;
				settings.convertToCode32 = Enable_State.FALSE;
				settings.convertToCode32Prefix = Enable_State.FALSE;
				
				// step4
				// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
				// if user get Err_InvalidParameter, it means user put wrong value into items
				// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
				// if user get S_OK, it means set settings is successful.
				ClResult clRet = mReaderManager.Set_Symbology(settings);
				if (ClResult.S_ERR == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_InvalidParameter == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_NotSupport == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
				else if (ClResult.S_OK == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
			}	
			*/
			
		}	

		// ***************************************************//
		//  7-7. get/set TriopticCode 39(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{	
					
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.TriopticCode39 settings = new TriopticCode39();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
	
				// step3: if barcode scanner support this symbology¡Athen user can change attribute
				settings.enable = Enable_State.FALSE;
	
				
				// step4
				// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
				// if user get Err_InvalidParameter, it means user put wrong value into items
				// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
				// if user get S_OK, it means set settings is successful.
				ClResult clRet = mReaderManager.Set_Symbology(settings);
				if (ClResult.S_ERR == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_InvalidParameter == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_NotSupport == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
				else if (ClResult.S_OK == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
			
			}	
			*/
			
		}	
	
		// ***************************************************//
		//  7-8. get/set Code 93(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{	
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.Code93 settings = new Code93();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
	
				// step3: if barcode scanner support this symbology¡Athen user can change attribute
				settings.enable = Enable_State.TRUE;
				settings.length1 = 10;
				settings.length2 = 20;
	
				
				// step4
				// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
				// if user get Err_InvalidParameter, it means user put wrong value into items
				// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
				// if user get S_OK, it means set settings is successful.
				ClResult clRet = mReaderManager.Set_Symbology(settings);
				if (ClResult.S_ERR == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_InvalidParameter == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_NotSupport == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
				else if (ClResult.S_OK == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
			}
			*/
			
		}	

		// ***************************************************//
		// 7-9. get/set ISBT 128(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{	
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.ISBT128 settings = new ISBT128();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
	
				// step3: if barcode scanner support this symbology¡Athen user can change attribute
				settings.enable = Enable_State.TRUE;
				settings.concatenation = ISBTConcatenationType.Enable;
				settings.concatenationRedundancy = 5;
	
				
				// step4
				// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
				// if user get Err_InvalidParameter, it means user put wrong value into items
				// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
				// if user get S_OK, it means set settings is successful.
				ClResult clRet = mReaderManager.Set_Symbology(settings);
				if (ClResult.S_ERR == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_InvalidParameter == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_NotSupport == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
				else if (ClResult.S_OK == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
			}	
			*/
			
		}

		// ***************************************************//
		//  7-10. get/set Code 128(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{	
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.Code128 settings = new Code128();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
	
				
				// step3: if barcode scanner support this symbology¡Athen user can change attribute
				settings.enable = Enable_State.TRUE;
	
				
				// step4
				// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
				// if user get Err_InvalidParameter, it means user put wrong value into items
				// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
				// if user get S_OK, it means set settings is successful.
				ClResult clRet = mReaderManager.Set_Symbology(settings);
				if (ClResult.S_ERR == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_InvalidParameter == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_NotSupport == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
				else if (ClResult.S_OK == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
				
			}	
			*/
			
		}

		// ***************************************************//
		//  7-11. get/set GS1 128(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{	
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.GS1128 settings = new GS1128();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
	
				// step3: if barcode scanner support this symbology¡Athen user can change attribute
				settings.enable = Enable_State.TRUE;
				settings.fieldSeparator = '%';
				
				// step4
				// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
				// if user get Err_InvalidParameter, it means user put wrong value into items
				// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
				// if user get S_OK, it means set settings is successful.
				ClResult clRet = mReaderManager.Set_Symbology(settings);
				if (ClResult.S_ERR == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_InvalidParameter == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_NotSupport == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
				else if (ClResult.S_OK == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
			}	
			*/
			
		}

		// ***************************************************//
		//  7-12. get/set Msi(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{	
				
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.Msi settings = new Msi();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
	
				// step3: if barcode scanner support this symbology¡Athen user can change attribute
				settings.enable = Enable_State.TRUE;
				settings.length1 = 4;
				settings.length2 = 55;
				settings.checkDigitAlgorithm = DigitAlgorithm.Modulo_10_11;
				settings.transmitCheckDigit = Enable_State.TRUE;
				settings.checkDigitOption = MsiDigitOption.OneDigit;
				
				
				// step4
				// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
				// if user get Err_InvalidParameter, it means user put wrong value into items
				// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
				// if user get S_OK, it means set settings is successful.
				ClResult clRet = mReaderManager.Set_Symbology(settings);
				if (ClResult.S_ERR == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_InvalidParameter == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_NotSupport == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
				else if (ClResult.S_OK == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
				
			}
			*/
			
		}

		// ***************************************************//
		//  7-13. get/set Ean8(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.Ean8 settings = new Ean8();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
	
				// step3: if barcode scanner support this symbology¡Athen user can change attribute
				settings.enable = Enable_State.TRUE;
				settings.addon2 = AddonsType.IgnoresAddons;
				settings.addon5 = AddonsType.AutoDiscriminate;
				settings.convertToEan13 = Enable_State.FALSE;
				settings.transmitCheckDigit = Enable_State.TRUE;
				
				
				// step4
				// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
				// if user get Err_InvalidParameter, it means user put wrong value into items
				// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
				// if user get S_OK, it means set settings is successful.
				ClResult clRet = mReaderManager.Set_Symbology(settings);
				if (ClResult.S_ERR == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_InvalidParameter == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_NotSupport == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
				else if (ClResult.S_OK == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
			}	
			*/
			
			
		}

		// ***************************************************//
		//  7-14. get/set Ean13(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.Ean13 settings = new Ean13();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
	
				// step3: if barcode scanner support this symbology¡Athen user can change attribute
				settings.enable = Enable_State.TRUE;
				settings.addon2 = AddonsType.AutoDiscriminate;
				settings.addon5 = AddonsType.IgnoresAddons;
				settings.convertToISBN = Enable_State.TRUE;
				settings.transmitCheckDigit = Enable_State.TRUE;
				settings.booklandISBNFormat=ISBNFormat.ISBN_10;
				
				
				// step4
				// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
				// if user get Err_InvalidParameter, it means user put wrong value into items
				// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
				// if user get S_OK, it means set settings is successful.
				ClResult clRet = mReaderManager.Set_Symbology(settings);
				if (ClResult.S_ERR == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_InvalidParameter == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_NotSupport == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
				else if (ClResult.S_OK == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
			}
			*/
			
			
		}
		
		// ***************************************************//
		//  7-15. get/set GS1 DataBar 14(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.GS1DataBar14 settings = new GS1DataBar14();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
	
				// step3: if barcode scanner support this symbology¡Athen user can change attribute
				settings.enable = Enable_State.TRUE;
				settings.convertToUpcEan = Enable_State.FALSE;
				
				// step4
				// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
				// if user get Err_InvalidParameter, it means user put wrong value into items
				// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
				// if user get S_OK, it means set settings is successful.
				ClResult clRet = mReaderManager.Set_Symbology(settings);
				if (ClResult.S_ERR == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_InvalidParameter == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_NotSupport == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
				else if (ClResult.S_OK == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
			}
			*/
			
			
		}

		// ***************************************************//
		//  7-16. get/set GS1 DataBar Expanded(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
				
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.GS1DataBarExpanded settings = new GS1DataBarExpanded();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
	
				// step3: if barcode scanner support this symbology¡Athen user can change attribute
				settings.enable = Enable_State.TRUE;
				settings.fieldSeparator = '"';
				
				// step4
				// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
				// if user get Err_InvalidParameter, it means user put wrong value into items
				// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
				// if user get S_OK, it means set settings is successful.
				ClResult clRet = mReaderManager.Set_Symbology(settings);
				if (ClResult.S_ERR == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_InvalidParameter == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_NotSupport == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
				else if (ClResult.S_OK == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
				
			}
			*/
			
			
		}	
		
		// ***************************************************//
		//  7-17. get/set GS1 DataBar Limited(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
				
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.GS1DataBarLimited settings = new GS1DataBarLimited();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
	
				// step3: if barcode scanner support this symbology¡Athen user can change attribute
				settings.enable = Enable_State.TRUE;
				settings.convertToUpcEan = Enable_State.FALSE;
				
				// step4
				// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
				// if user get Err_InvalidParameter, it means user put wrong value into items
				// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
				// if user get S_OK, it means set settings is successful.
				ClResult clRet = mReaderManager.Set_Symbology(settings);
				if (ClResult.S_ERR == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_InvalidParameter == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_NotSupport == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
				else if (ClResult.S_OK == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
			}
			*/
			
			
		}			

		// ***************************************************//
		//  7-18. get/set UccCoupon(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
					
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.UccCoupon settings = new UccCoupon();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
	
				// step3: if barcode scanner support this symbology¡Athen user can change attribute
				settings.enable = Enable_State.TRUE;
				
				// step4
				// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
				// if user get Err_InvalidParameter, it means user put wrong value into items
				// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
				// if user get S_OK, it means set settings is successful.
				ClResult clRet = mReaderManager.Set_Symbology(settings);
				if (ClResult.S_ERR == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_InvalidParameter == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_NotSupport == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
				else if (ClResult.S_OK == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
			}	
			*/
			
		}

		// ***************************************************//
		//  7-19. get/set UpcA(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
					
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.UpcA settings = new UpcA();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
	
				// step3: if barcode scanner support this symbology¡Athen user can change attribute
				settings.enable = Enable_State.TRUE;
				settings.addon2 = AddonsType.AutoDiscriminate;
				settings.addon5 = AddonsType.AutoDiscriminate;
				settings.convertToEan13 = Enable_State.FALSE;
				settings.transmitCheckDigit = Enable_State.TRUE;
				settings.transmitSystemNumber = Preamble.SysNumAndCtyCode;
				
				// step4
				// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
				// if user get Err_InvalidParameter, it means user put wrong value into items
				// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
				// if user get S_OK, it means set settings is successful.
				ClResult clRet = mReaderManager.Set_Symbology(settings);
				if (ClResult.S_ERR == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_InvalidParameter == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_NotSupport == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
				else if (ClResult.S_OK == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
			}
			*/
			
			
		}	

		// ***************************************************//
		//  7-20. get/set UpcE(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
				
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.UpcE settings = new UpcE();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
	
				// step3: if barcode scanner support this symbology¡Athen user can change attribute
				settings.enable = Enable_State.TRUE;
				settings.addon2 = AddonsType.AutoDiscriminate;
				settings.addon5 = AddonsType.AutoDiscriminate;
				settings.convertToUpcA = Enable_State.FALSE;
				settings.transmitCheckDigit = Enable_State.TRUE;
				settings.transmitSystemNumber = Preamble.SysNumAndCtyCode;
				
				// step4
				// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
				// if user get Err_InvalidParameter, it means user put wrong value into items
				// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
				// if user get S_OK, it means set settings is successful.
				ClResult clRet = mReaderManager.Set_Symbology(settings);
				if (ClResult.S_ERR == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_InvalidParameter == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_NotSupport == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
				else if (ClResult.S_OK == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
			}
			*/
			
			
		}

		// ***************************************************//
		//  7-21. get/set UpcE1(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
					
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.UpcE1 settings = new UpcE1();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
	
				// step3: if barcode scanner support this symbology¡Athen user can change attribute
				settings.enable = Enable_State.TRUE;
				settings.addon2 = AddonsType.AutoDiscriminate;
				settings.addon5 = AddonsType.AutoDiscriminate;
				settings.convertToUpcA = Enable_State.FALSE;
				settings.transmitCheckDigit = Enable_State.TRUE;
				settings.transmitSystemNumber = Preamble.None;
				
				// step4
				// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
				// if user get Err_InvalidParameter, it means user put wrong value into items
				// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
				// if user get S_OK, it means set settings is successful.
				ClResult clRet = mReaderManager.Set_Symbology(settings);
				if (ClResult.S_ERR == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_InvalidParameter == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_NotSupport == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
				else if (ClResult.S_OK == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
			}
			*/
			
			
		}

		// ***************************************************//
		//  7-22. get/set Code11(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
					
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.Code11 settings = new Code11();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
				
				// step3: if barcode scanner support this symbology¡Athen user can change attribute
				settings.enable = Enable_State.TRUE;
				settings.transmitCheckDigit = Enable_State.TRUE;
				settings.numberOfCheckDigits = NumberOfCheck.Two;
				settings.length1 = 4;
				settings.length2 = 55;
				
				// step4
				// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
				// if user get Err_InvalidParameter, it means user put wrong value into items
				// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
				// if user get S_OK, it means set settings is successful.
				ClResult clRet = mReaderManager.Set_Symbology(settings);
				if (ClResult.S_ERR == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_InvalidParameter == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
				else if (ClResult.Err_NotSupport == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
				else if (ClResult.S_OK == clRet)
					Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
				
			}
			*/
			
			
		}
		
		// ***************************************************//
		// 7-23. get/set USPostal(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
					
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.USPostal settings = new USPostal();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
				else if (ClResult.S_OK == mReaderManager.Get_Symbology(settings)) {
					
					// step3: if barcode scanner support this symbology¡Athen user can change attribute
					settings.enablePlanet = Enable_State.TRUE;
					settings.enablePostnet = Enable_State.TRUE;
					settings.transmitCheckDigit = Enable_State.TRUE;
	
					// step4
					// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
					// if user get Err_InvalidParameter, it means user put wrong value into items
					// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
					// if user get S_OK, it means set settings is successful.
					ClResult clRet = mReaderManager.Set_Symbology(settings);
					if (ClResult.S_ERR == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
					else if (ClResult.Err_InvalidParameter == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
					else if (ClResult.Err_NotSupport == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
					else if (ClResult.S_OK == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
	
				}
			}
			*/
		}
		
		// ***************************************************//
		// 7-24. get/set UKPostal(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
					
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.UKPostal settings = new UKPostal();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
				else if (ClResult.S_OK == mReaderManager.Get_Symbology(settings)) {
					
					// step3: if barcode scanner support this symbology¡Athen user can change attribute
					settings.enable = Enable_State.FALSE;
					settings.transmitCheckDigit = Enable_State.TRUE;
	
					// step4
					// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
					// if user get Err_InvalidParameter, it means user put wrong value into items
					// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
					// if user get S_OK, it means set settings is successful.
					ClResult clRet = mReaderManager.Set_Symbology(settings);
					if (ClResult.S_ERR == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
					else if (ClResult.Err_InvalidParameter == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
					else if (ClResult.Err_NotSupport == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
					else if (ClResult.S_OK == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
	
				}
			}
			*/
		}
		
		// ***************************************************//
		// 7-25. get/set JapanPostal(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
					
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.JapanPostal settings = new JapanPostal();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
				else if (ClResult.S_OK == mReaderManager.Get_Symbology(settings)) {
					
					// step3: if barcode scanner support this symbology¡Athen user can change attribute
					settings.enable = Enable_State.FALSE;
	
					// step4
					// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
					// if user get Err_InvalidParameter, it means user put wrong value into items
					// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
					// if user get S_OK, it means set settings is successful.
					ClResult clRet = mReaderManager.Set_Symbology(settings);
					if (ClResult.S_ERR == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
					else if (ClResult.Err_InvalidParameter == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
					else if (ClResult.Err_NotSupport == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
					else if (ClResult.S_OK == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
	
				}
			}
			*/
		}
	
		// ***************************************************//
		// 7-26. get/set AustralianPostal(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
				
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.AustralianPostal settings = new AustralianPostal();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
				else if (ClResult.S_OK == mReaderManager.Get_Symbology(settings)) {
					
					// step3: if barcode scanner support this symbology¡Athen user can change attribute
					settings.enable = Enable_State.TRUE;
	
					// step4
					// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
					// if user get Err_InvalidParameter, it means user put wrong value into items
					// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
					// if user get S_OK, it means set settings is successful.
					ClResult clRet = mReaderManager.Set_Symbology(settings);
					if (ClResult.S_ERR == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
					else if (ClResult.Err_InvalidParameter == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
					else if (ClResult.Err_NotSupport == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
					else if (ClResult.S_OK == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
	
				}
			}
			*/
		}
		
		// ***************************************************//
		// 7-27. get/set DutchPostal(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
				
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.DutchPostal settings = new DutchPostal();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
				else if (ClResult.S_OK == mReaderManager.Get_Symbology(settings)) {
					
					// step3: if barcode scanner support this symbology¡Athen user can change attribute
					settings.enable = Enable_State.TRUE;
	
					// step4
					// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
					// if user get Err_InvalidParameter, it means user put wrong value into items
					// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
					// if user get S_OK, it means set settings is successful.
					ClResult clRet = mReaderManager.Set_Symbology(settings);
					if (ClResult.S_ERR == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
					else if (ClResult.Err_InvalidParameter == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
					else if (ClResult.Err_NotSupport == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
					else if (ClResult.S_OK == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
	
				}
			}
			*/
		}
		
		// ***************************************************//
		// 7-28. get/set USPSPostal(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
					
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.USPSPostal settings = new USPSPostal();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
				else if (ClResult.S_OK == mReaderManager.Get_Symbology(settings)) {
					
					// step3: if barcode scanner support this symbology¡Athen user can change attribute
					settings.enable = Enable_State.TRUE;
	
					// step4
					// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
					// if user get Err_InvalidParameter, it means user put wrong value into items
					// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
					// if user get S_OK, it means set settings is successful.
					ClResult clRet = mReaderManager.Set_Symbology(settings);
					if (ClResult.S_ERR == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
					else if (ClResult.Err_InvalidParameter == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
					else if (ClResult.Err_NotSupport == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
					else if (ClResult.S_OK == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
	
				}
			}
			*/
		}
	
		// ***************************************************//
		// 7-29. get/set UPUFICSPostal(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
					
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.UPUFICSPostal settings = new UPUFICSPostal();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
				else if (ClResult.S_OK == mReaderManager.Get_Symbology(settings)) {
					
					// step3: if barcode scanner support this symbology¡Athen user can change attribute
					settings.enable = Enable_State.TRUE;
	
					// step4
					// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
					// if user get Err_InvalidParameter, it means user put wrong value into items
					// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
					// if user get S_OK, it means set settings is successful.
					ClResult clRet = mReaderManager.Set_Symbology(settings);
					if (ClResult.S_ERR == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
					else if (ClResult.Err_InvalidParameter == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
					else if (ClResult.Err_NotSupport == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
					else if (ClResult.S_OK == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
	
				}
			}
			*/
		}
	
		
		// ***************************************************//
		// 7-30. get/set PDF417(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
					
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.PDF417 settings = new PDF417();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
				else if (ClResult.S_OK == mReaderManager.Get_Symbology(settings)) {
					
					// step3: if barcode scanner support this symbology¡Athen user can change attribute
					settings.enable = Enable_State.TRUE;
					settings.escapeCharacter = Enable_State.FALSE;
					settings.transmitControlHeader = Enable_State.FALSE;
					settings.transmitMode = TransmitMode.TransmitAnySymbolInSet;
					
					// step4
					// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
					// if user get Err_InvalidParameter, it means user put wrong value into items
					// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
					// if user get S_OK, it means set settings is successful.
					ClResult clRet = mReaderManager.Set_Symbology(settings);
					if (ClResult.S_ERR == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
					else if (ClResult.Err_InvalidParameter == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
					else if (ClResult.Err_NotSupport == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
					else if (ClResult.S_OK == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
	
				}
			}
			*/
		}
		
		// ***************************************************//
		// 7-31. get/set MicroPDF417(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
				
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.MicroPDF417 settings = new MicroPDF417();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
				else if (ClResult.S_OK == mReaderManager.Get_Symbology(settings)) {
					
					// step3: if barcode scanner support this symbology¡Athen user can change attribute
					settings.enable = Enable_State.TRUE;
					settings.code128Emulation = Enable_State.FALSE;
					
					// step4
					// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
					// if user get Err_InvalidParameter, it means user put wrong value into items
					// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
					// if user get S_OK, it means set settings is successful.
					ClResult clRet = mReaderManager.Set_Symbology(settings);
					if (ClResult.S_ERR == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
					else if (ClResult.Err_InvalidParameter == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
					else if (ClResult.Err_NotSupport == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
					else if (ClResult.S_OK == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
	
				}
			}
			*/
		}

		// ***************************************************//
		// 7-32. get/set DataMatrix(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
					
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.DataMatrix settings = new DataMatrix();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
				else if (ClResult.S_OK == mReaderManager.Get_Symbology(settings)) {
					
					// step3: if barcode scanner support this symbology¡Athen user can change attribute
					settings.enable = Enable_State.TRUE;
					settings.mirrorImage = MatrixMirrorImage.Auto;
					settings.fieldSeparator = 'A';
					
					// step4
					// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
					// if user get Err_InvalidParameter, it means user put wrong value into items
					// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
					// if user get S_OK, it means set settings is successful.
					ClResult clRet = mReaderManager.Set_Symbology(settings);
					if (ClResult.S_ERR == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
					else if (ClResult.Err_InvalidParameter == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
					else if (ClResult.Err_NotSupport == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
					else if (ClResult.S_OK == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
	
				}
			}
			*/
		}
		
		// ***************************************************//
		// 7-33. get/set MaxiCode(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
					
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.MaxiCode settings = new MaxiCode();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
				else if (ClResult.S_OK == mReaderManager.Get_Symbology(settings)) {
					
					// step3: if barcode scanner support this symbology¡Athen user can change attribute
					settings.enable = Enable_State.TRUE;
					
					// step4
					// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
					// if user get Err_InvalidParameter, it means user put wrong value into items
					// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
					// if user get S_OK, it means set settings is successful.
					ClResult clRet = mReaderManager.Set_Symbology(settings);
					if (ClResult.S_ERR == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
					else if (ClResult.Err_InvalidParameter == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
					else if (ClResult.Err_NotSupport == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
					else if (ClResult.S_OK == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
	
				}
			}
			*/
		}
	
		// ***************************************************//
		// 7-34. get/set QRCode(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
					
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.QRCode settings = new QRCode();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
				else if (ClResult.S_OK == mReaderManager.Get_Symbology(settings)) {
					
					// step3: if barcode scanner support this symbology¡Athen user can change attribute
					settings.enable = Enable_State.TRUE;
					
					// step4
					// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
					// if user get Err_InvalidParameter, it means user put wrong value into items
					// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
					// if user get S_OK, it means set settings is successful.
					ClResult clRet = mReaderManager.Set_Symbology(settings);
					if (ClResult.S_ERR == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
					else if (ClResult.Err_InvalidParameter == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
					else if (ClResult.Err_NotSupport == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
					else if (ClResult.S_OK == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
	
				}
			}
			*/
		}
		
		// ***************************************************//
		// 7-35. get/set MicroQR(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
					
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.MicroQR settings = new MicroQR();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
				else if (ClResult.S_OK == mReaderManager.Get_Symbology(settings)) {
					
					// step3: if barcode scanner support this symbology¡Athen user can change attribute
					settings.enable = Enable_State.TRUE;
					
					// step4
					// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
					// if user get Err_InvalidParameter, it means user put wrong value into items
					// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
					// if user get S_OK, it means set settings is successful.
					ClResult clRet = mReaderManager.Set_Symbology(settings);
					if (ClResult.S_ERR == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
					else if (ClResult.Err_InvalidParameter == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
					else if (ClResult.Err_NotSupport == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
					else if (ClResult.S_OK == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
	
				}
			}
			*/
		}
		
		// ***************************************************//
		// 7-36. get/set Aztec(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
					
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.Aztec settings = new Aztec();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
				else if (ClResult.S_OK == mReaderManager.Get_Symbology(settings)) {
					
					// step3: if barcode scanner support this symbology¡Athen user can change attribute
					settings.enable = Enable_State.TRUE;
					
					// step4
					// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
					// if user get Err_InvalidParameter, it means user put wrong value into items
					// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
					// if user get S_OK, it means set settings is successful.
					ClResult clRet = mReaderManager.Set_Symbology(settings);
					if (ClResult.S_ERR == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
					else if (ClResult.Err_InvalidParameter == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
					else if (ClResult.Err_NotSupport == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
					else if (ClResult.S_OK == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
	
				}
			}
			*/
		}
	
		// ***************************************************//
		// 7-37. get/set Korean3Of5(same usage as above)
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
							
				// step1: new a class, the object is set to default value
				com.cipherlab.barcode.decoderparams.Korean3Of5 settings = new Korean3Of5();
	
				// step2: to check does barcode scanner support this symbology
				if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
				{
					// barcode scanner of device does not support this kind of symbology
					return;
				}
				else if (ClResult.S_OK == mReaderManager.Get_Symbology(settings)) {
					
					// step3: if barcode scanner support this symbology¡Athen user can change attribute
					settings.enable = Enable_State.TRUE;
					
					// step4
					// Set settings and check retrun value, if user get ClResult.S_ERR, it means failed, 
					// if user get Err_InvalidParameter, it means user put wrong value into items
					// if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
					// if user get S_OK, it means set settings is successful.
					ClResult clRet = mReaderManager.Set_Symbology(settings);
					if (ClResult.S_ERR == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
					else if (ClResult.Err_InvalidParameter == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
					else if (ClResult.Err_NotSupport == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
					else if (ClResult.S_OK == clRet)
						Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
	
				}
			}
			*/
		}
		
		// ***************************************************//
		//  8. software trigger
		// ***************************************************//
		{
			/*
			// software way to scan barcode (ex, create a button (call this API inside onClick)) 
			// if decode barcode successfully, app will get an intent "Intent_SOFTTRIGGER_DATA"
			// get decoded data from intent.getStringExtra() (it does need to implement a BroadcastReceiver (see below))
			if (mReaderManager != null)
			{
				mReaderManager.SoftScanTrigger();
			}
			*/
		}

		// ***************************************************//
		//  9. reset to default(include all symbologies and Notification¡BUserPreferences¡BReaderOutputConfiguration
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
				if (ClResult.S_ERR == mReaderManager.ResetReaderToDefault())
				{
					Toast.makeText(this, "ResetReaderToDefault was failed",
							Toast.LENGTH_SHORT).show();
				}
				else {
					Toast.makeText(this, "ResetReaderToDefault was done!",
							Toast.LENGTH_SHORT).show();
				}
			}
			*/
		}

		// ***************************************************//
		//  10. get version of barcode reader service
		// ***************************************************//
		{
			/*
			if (mReaderManager != null)
			{
				String ver = mReaderManager.Get_BarcodeServiceVer();
			}
			*/
		}		
		
	}
	
	/// create a BroadcastReceiver for receiving intents from barcode reader service
	private final BroadcastReceiver myDataReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Software trigger must receive this intent message
			if (intent.getAction().equals(GeneralString.Intent_SOFTTRIGGER_DATA)) {
				
				// extra string from intent
				String data = intent.getStringExtra(GeneralString.BcReaderData);
				
				// show decoded data
				//e1.setText(data);
			}else if (intent.getAction().equals(GeneralString.Intent_PASS_TO_APP)){
				// If user disable KeyboardEmulation, barcode reader service will broadcast Intent_PASS_TO_APP
				
				// extra string from intent
				String data = intent.getStringExtra(GeneralString.BcReaderData);
				
				// show decoded data
				//e1.setText(data);
				
			}else if(intent.getAction().equals(GeneralString.Intent_READERSERVICE_CONNECTED)){
				// Make sure this app bind to barcode reader sevice , then user can use APIs to get/set settings from barcode reader service 
				
				BcReaderType myReaderType =  mReaderManager.GetReaderType();	
				//e1.setText(myReaderType.toString());
				
				/*NotificationParams settings = new NotificationParams();
				mReaderManager.Get_NotificationParams(settings);
				
				ReaderOutputConfiguration settings2 = new ReaderOutputConfiguration();
				mReaderManager.Get_ReaderOutputConfiguration(settings2);
				*/
			}

		}
	};

}
