# JMIX Plugin Addon

The JMIX Plugin Addon allows you to write your own plugins for [JMIX](https://jmix.io/) that are not anchored in the source code, but are
injected via [PF4J](https://pf4j.org/).

The addon allows you to load/unload and deactivate plugins.

## Installation

1. Add Maven Dependencies to your `build.gradle`.
    ```groovy
    implementation 'de.bytestore:plugin'
    implementation 'de.bytestore:plugin-starter' 
    ```
2. Add `xmlns:app="http://byte-store.de/schema/app-ui-components"` to your View Descriptor.

## Features

- Plugin Management (Disable/Enable/Start/Stop/Delete/Download)
- Version Management (Semver)
- Plugin [Development Mode](https://pf4j.org/doc/development-mode.html) (Load Modules from Sourcecode and not JAR Archive)

### Plugin Auto-loading

To disable Plugin Auto-loading, you need to add this line to your `application.properties`.

You than can control the loading via the `PluginService`.

```properties
plugins.autoload=false
```

### Change Plugin Directory

To change Plugin Directory, you need to add this line to your `application.properties`.

```properties
plugins.home=./test/plugins
```

### Semantic Version Check

To disable globally semantic version checking, you need to add this line to your `application.properties`.

This will hide the required Field in the List UI and disables Version checking via PF4J.

```properties
plugins.version.check=false
```

### Exact Version Check

To enable exact version checks, you need to add this line to your `application.properties`.

```properties
plugins.version.exact=true
```

## Screenshots

![List View](assets/img/list-view.png)

![Detail View](assets/img/detail-view.png)

Shield: [![CC BY-NC 4.0][cc-by-nc-shield]][cc-by-nc]

This work is licensed under a
[Creative Commons Attribution-NonCommercial 4.0 International License][cc-by-nc].

[![CC BY-NC 4.0][cc-by-nc-image]][cc-by-nc]

[cc-by-nc]: https://creativecommons.org/licenses/by-nc/4.0/

[cc-by-nc-image]: https://licensebuttons.net/l/by-nc/4.0/88x31.png

[cc-by-nc-shield]: https://img.shields.io/badge/License-CC%20BY--NC%204.0-lightgrey.svg