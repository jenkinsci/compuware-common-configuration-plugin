# Compuware Common Configuration Plugin

[![Jenkins Plugin](https://img.shields.io/jenkins/plugin/v/compuware-common-configuration.svg)](https://plugins.jenkins.io/compuware-common-configuration) [![GitHub release](https://img.shields.io/github/v/release/jenkinsci/compuware-common-configuration-plugin.svg?label=release)](https://github.com/jenkinsci/compuware-common-configuration-plugin/releases/latest) [![Jenkins Plugin Installs](https://img.shields.io/jenkins/plugin/i/compuware-common-configuration.svg?color=blue)](https://plugins.jenkins.io/compuware-common-configuration)

## Overview

The Compuware Common Configuration provides configuration settings that are shared by other Compuware Jenkins plugins.

## Prerequisites

The following are required to use this plugin:
- Jenkins
- Jenkins Credentials Plugin

## Configuring for Topaz Workbench CLI & Host Connections

In order to download Endevor, PDS, and ISPW members you will need to point to an installed Topaz Workbench Command Line Interface (CLI). The Topaz Workbench CLI will work with host connection(s) you also need to configure to download Endevor, PDS, and ISPW members.

Navigate to Manage Jenkins / Configure System and go to Compuware Configurations section.

- **Windows Topaz Workbench CLI home** : Point to the Windows installation location of the CLI. If necessary, change the default values given to match the correct installation location.

- **Linux Topaz Workbench CLI home** : Point to the Linux installation location of the CLI. If necessary, change the default values given to match the correct installation location.

![](docs/images/info.svg) The Topaz Workbench CLI must be installed on the machine that is configured to run the job.

**Host connections**

![](docs/images/info.svg) If no host connection appears in the Host Connections section, click Add Host Connection.

- **Description** : Enter a description of the connection.

- **Host:port** : Enter the z/OS host and port to connect to.

- **Encryption protocol** : Select the desired encryption protocol to be used for this connection. The encryption protocol is used to encrypt data sent to and from the host. The default is \'None\'.

- **Code page** : Select the desired code page to be used for this connection. The code page is used to translate data sent to and from the host. The default is 1047.

- **Read/write timeout (minutes)** : Enter the number of minutes for the plugin to wait for a response from the host before timing out.

- **CES URL** : Optionally, enter a URL for Compuware Enterprise Services (CES). This URL is used by other Compuware plugins that leverage REST APIs hosted by CES, for example Compuware's ISPW.

- **Connection ID** : An internal unique ID by which these connections are identified from jobs and other configuration. If the supplied identifier is null or empty, then a unique identifier is generated. Useful to specify explicitly when using connections from pipeline scripts.

## Product Assistance

Compuware provides assistance for customers with its documentation, the Compuware Support Center web site, and telephone customer support.

### Compuware Support Center

You can access online information for Compuware products via our Support Center site at [https://support.compuware.com](https://support.compuware.com/). Support Center provides access to critical information about your Compuware products. You can review frequently asked questions, read or download documentation, access product fixes, or e-mail your questions or comments. The first time you access Support Center, you must register and obtain a password. Registration is free.

### Contacting Customer Support

At Compuware, we strive to make our products and documentation the best in the industry. Feedback from our customers helps us maintain our quality standards. If you need support services, please obtain the following information before calling Compuware\'s 24-hour telephone support:

- The Jenkins job console output that contains any error messages or pertinent information.

- The name, release number, and build number of your product. This information is displayed in the Jenkins / Plugin Manager and go to the Installed tab. Apply filter: Compuware in order to display all of the installed Compuware plugins.

- Job information, whether the job uses Pipeline script or Freestyle project.

- Environment information, such as the operating system and release on which the Topaz CLI is installed.

You can contact Compuware in one of the following ways:

#### Phone

- USA and Canada: 1-800-538-7822 or 1-313-227-5444.

- All other countries: Contact your local Compuware office. Contact information is available at [https://support.compuware.com](https://support.compuware.com/).

#### Web

You can report issues via Compuware Support Center: [https://support.compuware.com](https://support.compuware.com/).

Note: Please report all high-priority issues by phone.

### Corporate Web Site

To access Compuware\'s site on the Web, go to [https://www.compuware.com](https://www.compuware.com/). The Compuware site provides a variety of product and support information.

## Change Log

See [Change Log](https://github.com/jenkinsci/compuware-common-configuration-plugin/blob/master/CHANGELOG.md)
