# Introductory
The mod skeleton generator is an app made in Kotlin that can generate a mod skeleton 
(for [tModLoader mods](https://tmodloader.net/)) in a given path.

# Java is required
Kotlin compiles to java bytecode. You will need [Java](https://www.java.com/en/download/) installed to run the application.

The application looks something like this:

![](https://i.imgur.com/WdTZ8V5.png)

Fill in all the fields, then press the "Generate mod" button to generate the skeleton.
The rules are that you should not use any special characters for the mod name. 
The build ignore is optional.

The application will remember the path you set (using the [Java Preferences API](https://docs.oracle.com/javase/8/docs/technotes/guides/preferences/index.html)), 
for easier future use to generate new skeletons. The 'Reset cache' button will make the application forget this location.

# Technologies used
[Kotlin](https://kotlinlang.org/) (The programming language used),
 
[RxJava](https://github.com/ReactiveX/RxJava) (Reactive streams), 

[Gradle](https://gradle.org/) (Dependency management), 

[Spring Boot](https://spring.io/) (Micro Service framework for easy MVC flow)

# License
The license for this application is the GNU GPLv3 license.
You are free to make derivative works, but you'll have to disclose the source, use the same license and provide a copyright notice.

# Credits
Thanks to Trivaxy for the initial work on the application.

© tModLoader Team - 2019