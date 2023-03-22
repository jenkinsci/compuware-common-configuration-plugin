# <img src="images/bmc_brandmark.png" width="45" height="45">BMC AMI Common Configuration

## Overview

The BMC AMI Common Configuration provides plugin configuration settings that are shared by other BMC Jenkins plugins.

## Prerequisites

The following are required to use this plugin:
- Jenkins
- Jenkins Credentials Plugin

## Configuring for Workbench for Eclipse CLI and Host Connections

To download Endevor, PDS, and Code Pipeline members you will need to point to an installed BMC AMI DevX Workbench for Eclipse Command Line Interface (CLI) that has host connections configured.

Navigate to Manage Jenkins / Configure System and go to the Common Configurations section. Perform the following steps:

- **Windows Workbench for Eclipse CLI home** : Point to the Windows installation location of the CLI. If necessary, change the default values to match the correct installation location.

- **Linux Workbench for Eclipse CLI home** : Point to the Linux installation location of the CLI. If necessary, change the default values to match the correct installation location.

![](docs/images/info.svg) You must install the Workbench for Eclipse CLI on the machine that is configured to run the job.

**Host connections**

![](docs/images/info.svg) If no host connection appears in the Host Connections section, click Add Host Connection, and specify the required values in the following fields:

- **Description** : Enter a description for the connection.

- **Host:Port** : Enter the z/OS host and port to connect to.

- **Encryption protocol** : Select the desired encryption protocol to be used for this connection. The encryption protocol is used to encrypt data sent to and from the host. The default value is \'None\'.

- **Code page** : Select the desired code page to be used for this connection. This is used to translate data sent to and from the host. The default value is 1047.

- **Read/write timeout (minutes)** : Enter the time in minutes for which the plugin must wait to get a response from the host before timing out.

- **CES URL** : Optionally, enter a URL for BMC AMI Common Enterprise Services (CES). This URL is used by other BMC plugins that leverage REST APIs hosted by CES. For example BMC AMI DevX Code Pipeline.

- **Connection ID** : An internal, unique ID by which the connections are identified from jobs and other configuration. If the supplied identifier is null or empty, then a unique identifier is generated. This field is useful when using connections from pipeline scripts.

## Product Assistance

BMC provides assistance to customers with its documentation, the BMC Support web-site, and via telephone calls with the customer support team.

### BMC Support Central

You can access information about the BMC products via our Support site, [https://support.bmc.com](https://support.bmc.com/). Support Central provides access to critical information about your BMC products. You can review frequently asked questions, read or download documentation, access product fixes, or e-mail your questions or comments. The first time you access Support Central, you must register and obtain a password. The registration is free.

### Contacting Customer Support

At BMC, we strive to make our products and documentation the best in the industry. Feedback from our customers helps us maintain our quality standards. If you need support services, please obtain the following information before calling BMC\'s 24-hour telephone support:

- The Jenkins job output that contains any error messages or pertinent information.

- The name, release number, and build number of your product. This information is displayed in the installed extensions page. Apply the filter, BMC, to display all the installed BMC extensions.

- Environment information, such as the operating system and release on which the Workbench for Eclipse CLI is installed.

You can contact BMC in one of the following ways:

#### Web:

You can report issues via the BMC Support website: [https://support.bmc.com](https://support.bmc.com/).

Note: Please report all high-priority issues by phone.

### Corporate Web Site:

To access the BMC website, go to [https://www.bmc.com/](https://www.bmc.com/). The BMC site provides a variety of product and support information.
