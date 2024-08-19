# AndroidCalendar

## Authors

Dan Moraru, Thomas Roos

## Description

Android calendar app done as a school project. This application acts as a regular calendar, allowing you to create, view, delete and update events while also allowing multiple events on the same day. The data is saved on an database that persists when the application is closed. Through the use of APIs, the application also displays national holidays based from where your location, as well as the weather for the next 7 days based from your location.

## UI Tests DISCLAIMER

> DISCLAIMER : The navigation tests can only be run with pre-seeding the database
> At the current moment we do not have an alternative
> In the `MenuViewModel.kt` file, uncomment the method **setupEventList()** on `line 26`
> WARNING: This will flush out your current data beware
