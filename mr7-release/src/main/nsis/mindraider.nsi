;NSIS Modern User Interface
;Header Bitmap Example Script
;Written by Joost Verburg

;--------------------------------
;Include Modern UI

  !include "mui-1.8-system.nsh"

;--------------------------------
;General

  ;Var MRHOME

  ;Name and file
  Name "MindRaider"
  OutFile "mindraider-8.0-windows.exe"

  ;Default installation folder
  InstallDir "$PROGRAMFILES\mindraider"
  
  ;Get installation folder from registry if available
  InstallDirRegKey HKCU "Software\mindraider" ""

;--------------------------------
;Variables

  Var MUI_TEMP
  Var STARTMENU_FOLDER
  
;--------------------------------
;Interface Configuration

  !define MUI_HEADERIMAGE
  !define MUI_HEADERIMAGE_BITMAP "C:\.mr\sf\mindraider7\mr7-release\src\main\nsis\images\nsis.bmp"
  !define MUI_ABORTWARNING

  !define MUI_FINISHPAGE_TEXT "$(^NameDA) has been installed on your computer.\r\n\r\nClick Finish to close this wizard."
  !define MUI_FINISHPAGE_LINK " "
  !define MUI_FINISHPAGE_LINK_LOCATION "http://java.sun.com/j2se/1.6.0/download.html"


;--------------------------------
;Pages

  !insertmacro MUI_PAGE_WELCOME
  !insertmacro MUI_PAGE_LICENSE "files/license.txt"
  ;!insertmacro MUI_PAGE_COMPONENTS
  !insertmacro MUI_PAGE_DIRECTORY

  ;Start Menu Folder Page Configuration
  !define MUI_STARTMENUPAGE_REGISTRY_ROOT "HKCU" 
  !define MUI_STARTMENUPAGE_REGISTRY_KEY "Software\mindraider" 
  !define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "Start Menu Folder"  
  !insertmacro MUI_PAGE_STARTMENU Application $STARTMENU_FOLDER

  !insertmacro MUI_PAGE_INSTFILES
  !insertmacro MUI_PAGE_FINISH

  
  !insertmacro MUI_UNPAGE_WELCOME
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES
  !insertmacro MUI_UNPAGE_FINISH
  
;--------------------------------
;Languages
 
  !insertmacro MUI_LANGUAGE "English"

;--------------------------------
;Installer Sections

Section "Dummy Section" SecDummy

  SetOutPath "$INSTDIR"
  
  ;ADD YOUR OWN FILES HERE...
  File /r C:\.mr\sf\mindraider7\mr7-release\target\mindraider\*.*
  File /r C:\.mr\sf\jre-distribution\*.*
  
  ;Store installation folder
  WriteRegStr HKCU "Software\mindraider" "" $INSTDIR
  
  ;Create uninstaller
  WriteUninstaller "$INSTDIR\Uninstall.exe"

  !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
    
    ;Create shortcuts
    CreateDirectory "$SMPROGRAMS\$STARTMENU_FOLDER"
    CreateShortCut "$SMPROGRAMS\$STARTMENU_FOLDER\MindRaider.lnk" "$INSTDIR\mindraider.exe" "" "$INSTDIR\programIcon.ico"	
    CreateShortCut "$SMPROGRAMS\$STARTMENU_FOLDER\Documentation.lnk" "http://mindraider.sourceforge.net" "" "$INSTDIR\programIcon.ico"
    CreateShortCut "$SMPROGRAMS\$STARTMENU_FOLDER\Uninstall.lnk" "$INSTDIR\Uninstall.exe"
  
  !insertmacro MUI_STARTMENU_WRITE_END

SectionEnd

;--------------------------------
;Descriptions

  ;Language strings
  LangString DESC_SecDummy ${LANG_ENGLISH} "A test section."

  ;Assign language strings to sections
  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
    !insertmacro MUI_DESCRIPTION_TEXT ${SecDummy} $(DESC_SecDummy)
  !insertmacro MUI_FUNCTION_DESCRIPTION_END
 
;--------------------------------
;Uninstaller Section

Section "Uninstall"

  ;ADD YOUR OWN FILES HERE...
  ;Delete "$INSTDIR\Uninstall.exe"
  RMDir /r "$INSTDIR"

  !insertmacro MUI_STARTMENU_GETFOLDER Application $MUI_TEMP    
  Delete "$SMPROGRAMS\$MUI_TEMP\MindRaider.lnk"
  Delete "$SMPROGRAMS\$MUI_TEMP\Documentation.lnk"
  Delete "$SMPROGRAMS\$MUI_TEMP\Uninstall.lnk"
  
  ;Delete empty start menu parent diretories
  StrCpy $MUI_TEMP "$SMPROGRAMS\$MUI_TEMP"
 
  startMenuDeleteLoop:
	ClearErrors
    RMDir $MUI_TEMP
    GetFullPathName $MUI_TEMP "$MUI_TEMP\.."
    
    IfErrors startMenuDeleteLoopDone
  
    StrCmp $MUI_TEMP $SMPROGRAMS startMenuDeleteLoopDone startMenuDeleteLoop
  startMenuDeleteLoopDone:

  DeleteRegKey /ifempty HKCU "Software\mindraider"

SectionEnd