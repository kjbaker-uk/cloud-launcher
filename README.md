# Cloud Launcher - A console like interface for Android with a Razer Kishi

![Promo](/images/screenshot.png)

This is Cloud Launcher, One-stop-shop for cloud gaming on a Razer Kishi controller.

## Features

As the apps feature-set continues to be merged into this new fork, I will continue to add to this repository.

- Support for Razer Kishi controller.
- Built from scratch to add a console like feel.
- Clean and simple interface.
- Navigate via touch or Razer Kishi gamepad buttons.
- Hardware support for batter status, charging, Wi-Fi, settings.
- OLED mode for devices that support it.
- Hot link icons for Chrome, Playstore, youTubem Discord.
- Support all the major Cloud gaming platforms.
- Gesture support
- Min Android 9, Max 11.
- No permissons required.

## Compiling

The project is built with gradle, so maintenence and compilation is very straightforward. 

```
$ ./gradlew build
```

## Setting up

The following apps need to be installed from the Google Play Store to take full advantage of this launcher.

    * GamePass : https://play.google.com/store/apps/details?id=com.gamepass&hl=en_GB&gl=US
    * Stadia : https://play.google.com/store/apps/details?id=com.google.stadia.android&hl=en_GB&gl=US
    * GeForce Now : https://play.google.com/store/apps/details?id=com.nvidia.geforcenow&hl=en_GB&gl=US
    * Google Play Games : https://play.google.com/store/apps/details?id=com.google.android.play.games&hl=en_GB&gl=US
    * Razer Kishi App : https://play.google.com/store/apps/details?id=com.razer.mobilegamepad.en&hl=en_GB&gl=US
    * Discord :  https://play.google.com/store/apps/details?id=com.discord&hl=en_GB&gl=US

Known issue: If you do not have some of these apps installed features will fail to work. I will make this better in the future.


## Contributing

Please fork this repository and contribute back using [pull requests](https://github.com/D4rkC00d3r/cloud-launcher/pulls). Features can be requested using [issues](https://github.com/D4rkC00d3r/cloud-launcher/issues). All code, comments, and critiques are greatly appreciated.

## Changelog

The full changelog for the app can be found on my discord [here](https://discord.gg/76QENp79).

## To sync with Cloud Launcher

```
$ git remote add upstream https://github.com/D4rkC00d3r/cloud-launcher
$ git fetch upstream
$ git checkout master
$ git merge upstream/master
```

## Join the Cloud Launcher Community
You are all very welcome to join the Cloud Launcher Discord server where you can hang out and talk features, updates, and all things Cloud Launcher. [Cloud Launcher Discord](https://discord.gg/76QENp79)

---

## License

```
Copyright 2021 Keith Baker

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```