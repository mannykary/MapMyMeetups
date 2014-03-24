# Map My Meetups

The Map My Meetups app uses the Google Maps and Meetup.com APIs to display all
open events occurring within the next 24 hours in a 10 mile radius of a 
user-specified location on a map.

The apk file is available in the bin folder, and can be manually installed onto
an Android device that supports Google Play Services.

Upon lauching the app, the map will zoom to your current location. You can then
long-tap anywhere on the map to view all events (up to 20) that are occurring within
24 hours and in a 10 mile radius from the tapped location. Blue markers will appear 
on the map (you may need to zoom out in order to see all the markers). Tapping a marker
will display an Info Window showing the date/time of the event, and a short 
description of the event. Tapping on the Info Window will launch the application
of your choice to view detailed information about the event (either the Meetup 
app if installed on your device, or a browser to launch the event page on the
Meetup.com website.

If you wish to fork/clone the source, add the following file to your res/values/ 
folder (name it apikeys.xml). Replace YOUR_API_KEY with your actual API keys.

```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    
    <string name="GoogleMapsKey">YOUR_API_KEY</string>
    <string name="MeetupKey">YOUR_API_KEY</string>
    
</resources>
```
