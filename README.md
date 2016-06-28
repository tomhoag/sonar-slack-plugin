# sonar-slack-plugin
A SonarQube plugin to send notifications to a Slack channel.

## Howto ##
To build the plugin call **mvn clean package** (or download the current release). The jar file must be copied to the *SONAR_HOME/extensions/plugins* folder and SonarQube must be restarted.  (SONAR_HOME on OSX and Linux is likely /opt/sonar)

Tested for sonarqube 5.6

The plugin requires Java 1.8

## Plugin Configuration ##

After you have restarted SonarQube, you can configure the Slack plugin settings either globally or for a project.  If you set a global configuration, the settings will become the defaults for all projects.  You can override the global settings in each project.

The Slack plugin settings can be configured on the Administration page. 

**Enabled** - When set to false, Slack notifications will *not* be sent.

**Slack Channel** - The #channel or @user to which the Slack notification will be sent.   Channel names must be prefixed with # and user names must be prefixed with @.

**Slack Handle** - The handle from which the Slack notifications will be sent.  Defaults to SonarQube.  But get creative :-) 

**Slack Hook** - The Slack webhook used to send notifications.  For more info, please take a look @ [https://my.slack.com/services/new/incoming-webhook]()

**Notification Message** - The template for the Slack notification. You may substitue the following variables into your message:

| Variable  |  Description |
|---|---|
| $name |  Project Name |
| $date |  Analysis Date |
| $new  |  Number of new issues |
| $resolved | Number of resolved issues|
| $total | Total number of issues |
| $newBlockers | Number of new Blockers|
| $blockers | Total number of Blockers|
| $newCritical | Number of new Critical issues|
| $critical | Total number of Critical issues|
| $newMajor | Number of new Major issues|
| $major |  Total number of Major issues|
| $newMinor | Number of new Minor issues|
| $minor |  Total number of Minor issues|
| $newInfo | Number of new Info issues|
| $info |  Total number of Info issues|
| $newCodeSmells | Number of new Code Smells |
| $codeSmells | Total number of Code Smells |
| $nl | newline |

## TODO ##

While the plugin currently works just fine, it is built using classes and methods that have been deprecated in the 5.6 version of the API.  This will need updating . . . 