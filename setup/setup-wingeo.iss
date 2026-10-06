;------------------------------------------------------------------------------
;   Определяем некоторые константы
;------------------------------------------------------------------------------
#define   Name       "SA-Okna"
#define   URL        "http://sa-okna.ru"
#define   Version    "2.0.1"
#define   AppName    "getdown.jar"

[Setup]
CreateAppDir=yes
WizardImageFile=production.bmp
AppId={{08B2D66F-7919-499C-AE99-3BDB64414857}
AppName={#Name}
AppVersion={#Version}
DefaultDirName={autopf}\{#Name}
DisableDirPage=no
OutputDir=C:\okna\wingeo\setup
OutputBaseFileName=install-wingeo
SetupIconFile=C:\Okna\wingeo\setup\wingeo.ico
Compression=lzma
SolidCompression=yes

[Languages]
Name: default; MessagesFile: compiler:Languages\Russian.isl

[Tasks]
;Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: checkedonce 

[Files]
Source: .\wingeo.ico; DestDir: {app};
Source: .\{#AppName}; DestDir: {app}; Flags: ignoreversion overwritereadonly
Source: .\getdown.txt; DestDir: {app}; Flags: ignoreversion overwritereadonly

[Icons]
;Name: "{commondesktop}\{#Name}"; Filename: "{app}\{#AppName}";
Name: "{group}\{#Name}"; Filename: "{app}\\{#AppName}"
Name: "{autodesktop}\{#Name}"; Filename: "{app}\{#AppName}"; IconFilename: "{app}\wingeo.ico"

