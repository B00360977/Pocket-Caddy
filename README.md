# Pocket Caddy

This is the Repository for my final Hons Project.

Pocket Caddy is a mobile app for Android Devices that can record:
  * Shot distances
  * Clubs used
  * Number of shots per hole
  
It also gives users the ability to view previously recorded data so they can see how they are progressing

The main feature of the app is the reccommendation feature. 
Utilising Azure machine learning studio the app can use the distance the user is trying to hit and recommend which club they should use based on their previous shot history.

This works by creating models that are trained of the users specific data so the reccomendation is unique to them.

A demo of the app can be found [here](https://studentmailuwsac-my.sharepoint.com/:v:/g/personal/b00360997_studentmail_uws_ac_uk/EQ78vlaAd-9OtZyXHsLfA-8BG1qDpahbebRlf7zl6oMkyA?e=2vRvY3)

A dependencies may need to be manuallay installed. It is jtds-1.3.1  
The jar file can be found here - https://sourceforge.net/projects/jtds/files/jtds/1.3.1/  
Place the file in local machine and add to build.gradle file as shown below.  

dependencies {
 implementation files('{PATH_TO_FILE}\\jtds-1.3.1.jar')
}


_Owner - Kyle Kennedy_
