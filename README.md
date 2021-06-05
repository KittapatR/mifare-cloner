# mifare-cloner
Application for MiFare card header information extraction

For persons who interested in this application, it can install by download .APK file and press allow "Unknown Sources" in your Android settings -> Security -> Device Administration.

## Requirements
1. You must have an Android device that running Jelly Bean 4.2+.
2. Your device must support NFC technologies and MiFare card. You can check it easily by downloading this application, available on Google Play: https://play.google.com/store/apps/details?id=net.tpky.tools.nfccheck&hl=en&gl=US
3. After install "NFC Check by Tapkey", you will see that how your device supports NFC technology, only focus in 2 main keys: NFC is activated, MiFare Classic and Mifare Ultralight cards supported (this usually means, that the device uses an NFC Chip from NXP.)

## What application do
1. The application is going to extract MiFare card header data that contains UID, bytes, number of sectors and blocks.
2. The application can display multiple header card information, but not persistent data which store to the devices.
3. The application can copy a console out to your text files by only a copy button.
4. The application can clear a console by a clear button.

## Further applications
1. For MiFare classic, when a card has personal data and has been sniffed with NFC reader, a card will be extracted a personal data immediately due to a vulnerable encryption scheme that use in MiFare classic.
2. The UID field can be used for card emulation by NFC spoofing. For most Android devices, it requires to do superuser authorization. Except new Samsung devices since Samsung Galaxy S6, or devices that has NFC chip model as S3NRN74, S3NRN81, or S3NRN82: a driver cannot be easily edited _even superuser authorized_  due to a driver is not in control of Android as peripheral device, communicate via character special file "sec-nfc" in /dev/sec-nfc. Also, NFC chip has a security protection according to the study: https://i.blackhat.com/USA-20/Thursday/us-20-Wade-Beyond-Root-Custom-Firmware-For-Embedded-Mobile-Chipsets.pdf
3. For NFC emulation with fixed UID, you can investigate the method at: https://smartlockpicking.com/slides/Confidence_A_2018_Practical_Guide_To_Hacking_RFID_NFC.pdf (page 38-39). (Superuser authorization required).
