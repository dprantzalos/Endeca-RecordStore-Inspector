# Endeca RecordStore Inspector

The Endeca RecordStore Inspector is a GUI tool for visualizing the contents of Endeca RecordStores. It was created
to aid Endeca developers in debugging issues pertaining to CAS data ingestion.

I've written a blog entry explaining more about the tool here:
**[Debugging CAS with the Endeca RecordStore Inspector](http://www.ateam-oracle.com/debugging-cas-with-the-endeca-recordstore-inspector/)**

## Building the Project

This project currently requires Java 8 and Endeca 11.1 to build successfully. It has not been tested with earlier
versions of Endeca. You will also need Grade and Maven to run the build scripts.

To build the project, you must first install the Endeca dependencies in a local Maven repository. If you have Maven
in your path, you can just run the scripts/mvn_install.bat script to install all the dependent libraries. If your
Endeca installation resides in a directory other than the Endeca default (C:\Endeca), then you will need to
modify the mvn_install script to specify the Endeca Home location accordingly.

Once the dependencies are in place, assuming the Gradle bin directory is in your path, run the following command
to build a self-contained .jar file with all the Endeca dependencies included in the jar file:

```
gradle shadowJar
```

To build a portable version, optimized for Windows, that includes the Java Runtime Environment, use the following:
 
```
gradle build
```

## License

This code is released under the [MIT License](http://www.opensource.org/licenses/MIT).
