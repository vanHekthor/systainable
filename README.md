<p align="center">
    <a href="https://systainable.herokuapp.com/"><img src="https://user-images.githubusercontent.com/35042166/222794394-a3adc7f5-b7c4-4b39-a1d9-1a9eeb7bf9f5.png" alt="systainable logo"></a><br/>
    Green Configuration Dashboard <br/>
    Check it out <a href="https://systainable.herokuapp.com/">here.</a>
</p>

## Overview

Systainable is a web dashboard for comparing software configurations in mutltiple metrics (performance, energy consumption and memory usage, etc.). It is especially intended for finding greener more environmentally friendly configurations.

![image](https://user-images.githubusercontent.com/35042166/222795608-f3be00c0-5b3c-4174-a627-59a82e10ff3b.png)

## Features

### Define configurations

Add, remove or modify options. Add, import or export configurations.

![image](https://user-images.githubusercontent.com/35042166/222795904-254d35d2-2f6e-44d6-87cf-31a8b2038baf.png)

### Compare properties

Get a quick overview of each configuration's effect on the software properties.

![image](https://user-images.githubusercontent.com/35042166/222796567-977a8764-d058-447f-87b9-c217332b2370.png)

### Analyze in-depth

Find out which configuration options or interactions have the biggest effect on the overall performance of the software.

![image](https://user-images.githubusercontent.com/35042166/222796760-289f1854-cfa7-404f-bef8-e082b6f80ace.png)

### Optimize configurations

Let systainable automatically refine a configuration and check out the improvements in the optimized config preview.

![image](https://user-images.githubusercontent.com/35042166/222797198-e5878677-8f93-4942-8141-5692677393b6.png)

## Development

This application was generated using JHipster 6.10.3, you can find documentation and help at [https://www.jhipster.tech/documentation-archive/v6.10.3](https://www.jhipster.tech/documentation-archive/v6.10.3).

Before you can build this project, you must install and configure the following dependencies on your machine:

1. [Node.js][]: We use Node to run a development web server and build the project.
   Depending on your system, you can install Node either from source or as a pre-packaged bundle.

After installing Node, you should be able to run the following command to install development tools.
You will only need to run this command when dependencies change in [package.json](package.json).

    npm install

We use npm scripts and [Webpack][] as our build system.

Run the following commands in two separate terminals to create a blissful development experience where your browser
auto-refreshes when files change on your hard drive.

    ./gradlew
    npm start

Npm is also used to manage CSS and JavaScript dependencies used in this application. You can upgrade dependencies by
specifying a newer version in [package.json](package.json). You can also run `npm update` and `npm install` to manage dependencies.
Add the `help` flag on any command to see how you can use it. For example, `npm help update`.

The `npm run` command will list all of the scripts available to run for this project.

### Service workers

Service workers are commented by default, to enable them please uncomment the following code.

- The service worker registering script in index.html

```html
<script>
  if ('serviceWorker' in navigator) {
    navigator.serviceWorker.register('./service-worker.js').then(function () {
      console.log('Service Worker Registered');
    });
  }
</script>
```

Note: workbox creates the respective service worker and dynamically generate the `service-worker.js`

### Managing dependencies

For example, to add [Leaflet][] library as a runtime dependency of your application, you would run following command:

    npm install --save --save-exact leaflet

To benefit from TypeScript type definitions from [DefinitelyTyped][] repository in development, you would run following command:

    npm install --save-dev --save-exact @types/leaflet

Then you would import the JS and CSS files specified in library's installation instructions so that [Webpack][] knows about them:
Edit [src/main/webapp/app/main.ts](src/main/webapp/app/main.ts) file:

```
import 'leaflet/dist/leaflet.js';
```

Edit [src/main/webapp/content/scss/vendor.scss](src/main/webapp/content/scss/vendor.scss) file:

```
@import '~leaflet/dist/leaflet.scss';
```

Note: there are still few other things remaining to do for Leaflet that we won't detail here.

For further instructions on how to develop with JHipster, have a look at [Using JHipster in development][].

### Using vue-cli

You can also use [Vue CLI][] to display the project using vue UI.

For example, the following command:

    vue ui

will generate open Vue Project Manager. From there, you'll be able to manage your project as any other Vue.js projects.

## Building for production

### Packaging as jar

To build the final jar and optimize the test application for production, run:

    ./gradlew -Pprod clean bootJar

This will concatenate and minify the client CSS and JavaScript files. It will also modify `index.html` so it references these new files.
To ensure everything worked, run:

    java -jar build/libs/*.jar

Then navigate to [http://localhost:8080](http://localhost:8080) in your browser.

Refer to [Using JHipster in production][] for more details.

### Packaging as war

To package your application as a war in order to deploy it to an application server, run:

    ./gradlew -Pprod -Pwar clean bootWar

## Testing

To launch your application's tests, run:
./gradlew test integrationTest

### Client tests

Unit tests are run by [Jest][] and written with [Jasmine][]. They're located in [src/test/javascript/](src/test/javascript/) and can be run with:
npm test

For more information, refer to the [Running tests page][].

# Deploy

Systainable is deployed under [https://systainable.herokuapp.com/](https://systainable.herokuapp.com/).
