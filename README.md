# JMIX Plugin Addon

Das JMIX Plugin Addon erlaubt es eigene Plugins für JMIX zu schreiben die nicht im Source Code verankert sind, sondern
über PF4J injected werden.

The addon allows you to load/unload and deactivate plugins.

## Installation:

1. Add Maven Dependencies to your `build.gradle`.
    ```groovy
    implementation 'de.bytestore:plugin'
    implementation 'de.bytestore:plugin-starter' 
    ```
2. Add `xmlns:app="http://byte-store.de/schema/app-ui-components"` to your View Descriptor.

Shield: [![CC BY-NC 4.0][cc-by-nc-shield]][cc-by-nc]

This work is licensed under a
[Creative Commons Attribution-NonCommercial 4.0 International License][cc-by-nc].

[![CC BY-NC 4.0][cc-by-nc-image]][cc-by-nc]

[cc-by-nc]: https://creativecommons.org/licenses/by-nc/4.0/

[cc-by-nc-image]: https://licensebuttons.net/l/by-nc/4.0/88x31.png

[cc-by-nc-shield]: https://img.shields.io/badge/License-CC%20BY--NC%204.0-lightgrey.svg