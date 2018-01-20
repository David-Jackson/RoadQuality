# RoadQuality

Tired of the bad roads? This app is a simple way to fix it. RoadQuality uses your phone's accelerometer and GPS to detect the severity and location of poor road conditions. With this crowdsourced data, we can direct transportation agencies to the roads that need repair the most.

## Vision

The vision for this app is to make it as simple for the user as possible to record and upload anonymous data pertaining to the quality of roads that they drive on. To achieve this, the app should start when the user starts driving, automatically. The app should also do as much local processing of the data as possible, and only upload the anonymous data points that cannot be linked to any other data points, or be linked back to the user. The app should also provide the user control of their own data pertaining to the ability upload, share, and stop recording at any point in time. This is what the app should be striving to achieve throughout development.

## Get Started

To start contributing, clone the project with git (see [Cloning a repository](https://help.github.com/articles/cloning-a-repository/)) and import the `app` folder into Android Studio. Then you just need to create a new file called `api_keys.xml` in the project's `res/values/` folder (this is where we will be storing the Google Maps API key). Follow [these steps](https://developers.google.com/maps/documentation/android-api/signup) to get your own API Key, and add it to the `api_keys.xml` file you just created. Here's an example of how your file should look:

```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="google_maps_key" templateMergeStrategy="preserve" translatable="false">API_KEY_HERE</string>
</resources>
```

From there, you should be all set to run the app on your device or emulator. If you have any issues, please [submit an issue](https://github.com/David-Jackson/RoadQuality/issues/new).

## Progress

![](https://raw.githubusercontent.com/David-Jackson/RoadQuality/master/images/device-2018-01-20-122051.gif)

This app is still very much a work in progress, but this is what is implemented so far:
- [X] Detects and records bumps with accelerometer
- [X] Records location with GPS
- [X] Interpolates GPS data to find coordinates of bumps
- [X] Able to record individual trips
- [X] Runs as a foreground service (Independent of the app)
- [X] Implements Material Design and fluid animations
- [X] View past trips on a map

And this is what still needs to be added in the future:
- [ ] Automatically start recording when connected to Bluetooth device or navigating with Google Maps
- [ ] Upload anonymous data to a cloud server
- [ ] Add 'recording deadzones' where the app will not record data within a certain distance from a point

## Methodology

To get a better idea of the math behind the code, check out the [methodology](https://github.com/David-Jackson/RoadQuality/blob/master/METHODOLOGY.md)
