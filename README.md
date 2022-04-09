## Build tools & versions used
- Android Gradle Plugin v7.1.2
- Gradle Plugin 7.2

## Steps to run the app

- Change directory to project directory
- Run `gradlew :app:assembleDebug` in the command line
- Ensure you have the Android Platform Tools installed (https://developer.android.com/studio/releases/platform-tools)
- With an Android device connected, run `adb install ./app/build/outputs/apk/debug/app-debug.apk`

## What areas of the app did you focus on?
Main focus was on architecture, with a partial focus on performance.

## What was the reason for your focus? What problems were you trying to solve?
Focusing on architecture allowed me to quickly try out new libraries and frameworks. I wanted to ensure changes I made were concise and minimal, while having the greatest impact.

## How long did you spend on this project?
~ 6 hours, spread across 3 days.

## Did you make any trade-offs for this project? What would you have done differently with more time?
I chose to write a custom image caching layer instead of going with a library like Glide or Picasso, mostly because I was unfamiliar with these libraries, and had to deal with adapting the calls to work with Kotlin Coroutines. With more time, I would have looked for an efficient way wrap or adapt the calls, to ensure no more threads than needed were created.

## What do you think is the weakest part of your project?
The image caching layer and the UI.

## Did you copy any code or dependencies? Please make sure to attribute them here!
Some of the logic for the image caching came from https://developer.android.com/topic/performance/graphics/cache-bitmap#memory-cache

## Is there any other information you’d like us to know?
All images will be stored in the internal app cache. Clearing the app cache would force a redownload of all the images.
