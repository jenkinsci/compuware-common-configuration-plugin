Change Log
=========

1.0.10
------

- Version history moved out of Wiki content and linked to CHANGELOG.MD; subsequent changes will not be tracked in this file, but in [GitHub releases](https://github.com/jenkinsci/compuware-common-configuration-plugin/releases) instead.

1.0.9
------

- Corrected issue where the link in Manage connections is not generated correctly

1.0.8
------

- Corrected CLI version checking that was causing conflict with older versions of dependent plugins.

1.0.7
------

- Added encryption protocol selection to a host connection.

1.0.6
------

- Fixed help for for the CES URL field.

1.0.3
------

- Added the CES URL field to specify a URL for Compuware Enterprise Services (CES). This URL is used by other Compuware plugins that leverage REST APIs hosted by CES, for example Compuware‘s ISPW.

1.0.0
------

- Moved host connection information to this common plugin to be used by all Compuware plugins. 