# Overview #
This site hosts an [Eclipse](http://www.eclipse.org/) plugin for [Near Field Communication](http://en.wikipedia.org/wiki/Near_field_communication) development.

# Background #
Developers wanting to explore NFC tech should not waste time on low-level (read/write) details, but rather concentrate on accessing higher-level functionality - creating world class applications.

# Features #
The most important features are
  * Eclipse [NDEF](http://developer.android.com/guide/topics/nfc/nfc.html) editor
  * Android app integration for reading/writing NFC tags
    * Automagical QR-code based transfers from Eclipse
  * NFC reader integration - [ACR122](http://www.acs.com.hk/index.php?pid=product&id=ACR122U) and others
    * Type 2 tags support
# Screenshots #
Eclipse [Juno](http://wiki.nfc-eclipse-plugin.googlecode.com/git/images/juno.png), [Indigo](http://wiki.nfc-eclipse-plugin.googlecode.com/git/images/indigo.png) and [Kepler](http://wiki.nfc-eclipse-plugin.googlecode.com/git/images/kepler.png) releases. More in the [tutorial](Tutorial.md), also see [this 30 sec Youtube video](http://www.youtube.com/watch?v=I0P1Y4jPHtI&lc).
# Eclipse update site <a href='http://marketplace.eclipse.org/marketplace-client-intro?mpc_install=288653' title='Drag and drop into a running Eclipse Indigo workspace to install NFC Eclipse plugin'><img src='http://marketplace.eclipse.org/misc/installbutton.png' /></a> #
From Eclipse <b>3.7.2 or later</b>, select Help -> Install new Software and add
```
http://wiki.nfc-eclipse-plugin.googlecode.com/git/updatesite 
```
update site. See the [installation](Installation.md) page for more details.
# Getting started #
Try the walk-through [tutorial](Tutorial.md), it is **short and has a lot of images** ;-)

# Tags #
Order yourself some [NFC tags starter kit](http://rapidnfc.com/r/1372) to take full advantage of this project (also to have the most fun ;-)). See the [tags](http://code.google.com/p/nfc-eclipse-plugin/wiki/Tags) wiki page for more.

# Android client #
Download the free <b>'NFC Developer'</b> app from [Android market](http://play.google.com/store/apps/details?id=com.antares.nfc&referrer=eclipse) for **a painless workflow**: NDEF messages are automagically represented as custom QR codes in the plugin editor, then promptly scanned and written to NFC tags using the app - simple, fast and convenient :-)

Also supports load/save NDEF message files via device filesystem.

## NDEF Tools for Android ##
Look into the [NDEF Tools for Android](http://code.google.com/p/ndef-tools-for-android/) project for a [boilerplate project](http://code.google.com/p/ndef-tools-for-android/downloads/list) to [get started reading and writing NFC tags](http://code.google.com/p/nfc-eclipse-plugin/wiki/GettingStartedAndroid) on Android. You might also be interested in [a tutorial-like workshop](https://github.com/greenbird/workshops/tree/master/mobile/Android/Near%20Field%20Communications) (solution included) or an [online](http://ndefeditor.grundid.de/) NDEF editor.

A working demo is available in [Google Play](https://play.google.com/store/apps/details?id=org.ndeftools.boilerplate), search for keywords 'ndef tools demo'.
# Forum #
Please post comments and questions at the [NFC developers](http://groups.google.com/group/nfc-developers/topics) Google forum group.

# Acknowledgements #
This project uses code from the [ZXing](http://code.google.com/p/zxing/) and [NFC Tools for Java](https://github.com/grundid/nfctools) and was inspired by [my](http://code.google.com/p/nfc-eclipse-plugin/wiki/Author) [former](http://www.antares.no) and [current](http://www.greenbird.com) coworkers.

# News #
October 2nd 2014: The [NFC Developer](http://play.google.com/store/apps/details?id=com.antares.nfc) app has reached 35k installs!<br>
September 25th 2014: Project now builds with <a href='http://www.eclipse.org/tycho/'>Tycho</a>.<br>
March 25th 2013: Experimental support for Signature Records.<br>

<h1>History</h1>
Nov 12th 2014: Version 1.3.7 released with tycho build and new update site url.<br>
March 25th 2013: Version 1.3.6 released with fix for <a href='https://code.google.com/p/nfc-eclipse-plugin/issues/detail?id=2'>issue 2</a>.<br>
January 22th 2013: Version 1.3.5 released.<br>
November 10th 2012: Version 1.3.4 released.<br>
October 29th 2012: Version 1.3.3 released.<br>
September 23th 2012: Version 1.3.2 released.<br>
August 26th 2012: Version 1.3.1 released.<br>
August 13th 2012: Version 1.3.0 released.<br>

<h1>Need help?</h1>
If you need professional help with an NFC project, get in touch.<br>
<br>
<h1>Donate</h1>
Chip in to help me buy some more NFC tags and readers and divert time from paid work. Any amount is helpful and encouraging :-)<br>
<br>
<a href='https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=ANEJESHR6E7VC'><img src='https://www.paypal.com/en_US/i/btn/btn_donateCC_LG.gif' /></a>

