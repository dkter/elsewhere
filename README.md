# Elsewhere
*The weather app that tells you the weather where you aren't*

<img alt="Screenshot of Elsewhere showing the weather in Montreuil-sous-Pérouse, France" src="assets/screenshots/montreuil-sous-perouse.png" height=500 /><img alt="Screenshot of Elsewhere showing the weather in Brateiu, Austria" src="assets/screenshots/brateiu.png" height=500 /><img alt="Screenshot of Elsewhere showing the weather in Golenishchevo, Russia" src="assets/screenshots/golenishchevo.png" height=500 />

Every day, Elsewhere gives you the weather in another random location in the world.

## Why?
Well, you already know the weather where you are, don't you? Maybe you don't know the exact number, but I bet you could estimate it based on the temperature sensors in your body. What you *don't* know, however, is the weather in [Água Doce, Brazil](https://en.wikipedia.org/wiki/%C3%81gua_Doce). (Unless you live there, that is.)

Maybe, through using Elsewhere, you'll discover a new place you've never heard of before. Actually, scratch that. I can *guarantee* you will. Almost every day, in fact. The world is huge, and there just isn't enough time to visit every corner of it -- but maybe there's enough time to know the weather in a few places.

## Download
APK build coming soon...

## Build
Get an OpenWeatherMap API key and create a file called `keys.properties` in the root of this repo, then format it like this:

    OWM_KEY="your OWM API key here"

You can then build it normally.

**Tip:** if you don't want to use Android Studio, make sure ADB is in your path and launch the app using `dbg.ps1`. It will build the app, install it on your device, run it, and launch logcat filtered to messages from this app.

## About
Created by David Teresi.  
Weather data is sourced from [OpenWeatherMap](https://openweathermap.org). Place images are from [Wikimedia Commons](https://commons.wikimedia.org) and collected using [Wikidata](https://wikidata.org/).

Licensed under the Mozilla Public License. For more information see [LICENSE.txt](LICENSE.txt).
