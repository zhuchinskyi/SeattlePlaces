# Seattle Places

Sample project which allows you to search for places in the Seattle area using [Forsquare Places API](https://developer.foursquare.com/places-api). Search results are represented in List and [Map](https://developers.google.com/maps/documentation/android-sdk/intro) formats. Each item is clickable and navigates you to the detailed page with additional information inside (description, phone, working hours, etc). Page with details also contains static map representation using [Google Static Map API](https://developers.google.com/maps/documentation/maps-static/intro) are used with 2 markers which indicate the city center and current item location.

### Installing
Steps to run the project using the command line:
1. Get the project locally:
    ```
    git clone https://github.com/zhuchinskyi/SeattlePlaces.git
    ```
2. Navigate to the `/app` folder and execute `assemblDebug` command from [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html):
    ```
    ./gradlew assembleDebug
    ```
    After the build, `app-debug.apk` can be found inside your project dir using this path `app/build/outputs/apk/debug/`

4. Using [adb](https://developer.android.com/studio/command-line/adb) install project directly to a device or emulator using the command below:
    ```
    adb install app/build/outputs/apk/debug/app-debug.apk
    ```

You can also use Android Studio for that purpose either:
```
VSC -> Git -> Clone
```
Insert URL `https://github.com/zhuchinskyi/SeattlePlaces.git` and press `Clone` button. Android Studio will clone and build the project after you are good to run the App pressing `Run` button at the top with the default configuration.

### Project tech stack

* Kotlin
* [Koin](https://insert-koin.io/) (dependency injection)
* Architecture components (Room, LiveData, ViewModel)
* Android DataBinding
* Gson
* Glide


### TODO
Cover `ViewModels` and `Repositories` with unit tests.