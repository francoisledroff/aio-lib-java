# `aio-lib-java-core`

`aio-lib-java-core` is the Adobe I/O  - Java SDK - Core Library. 
This Java library holds various core utilities used across the other modules.
It also holds the core `Adobe Developer Console` Workspace Builder.

## Workspace

This library holds a [`Workspace`](./src/main/java/com/adobe/aio/workspace/Workspace.java) POJO modeling
your [Adobe Developer Console Project Workspace](https://www.adobe.io/apis/experienceplatform/console/docs.html#!AdobeDocs/adobeio-console/master/projects.md),

To get you started quickly use a `.properties` file,
* see our [sample config file](./src/test/resources/workspace.properties) in our jUnit Test.
* download your `project` configurations file from your Adobe Developer Console Project overview page
* map your `project` configurations with this properties

For now, you do have a bit of copy and paste to do, but we have a plan to streamline the process:
* https://github.com/adobe/aio-lib-java/issues/5

The `Workspace` POJO holds your Adobe Developer Console Project configurations
  (download your `project` configurations file from your Adobe Developer Console Project overview page):
* `aio_project_id`  your Adobe Developer Console project id (`project.id`)
* `aio_consumer_org_id`  your Adobe Developer Console consumer orgnaization id (`project.org.id`)
* `aio_ims_org_id` your Adobe Developer Console IMS Organization ID (`project.org.ims_org_id`)
* `aio_workspace_id` your Adobe Developer Console workspace Id (`project.workspace.id`)
* `aio_credential_id` your Adobe Developer Console jwt credential id (`project.workspace.details.credentials[i].id`)
* `aio_client_secret` your Adobe Developer Console jwt credential client secret (`project.workspace.details.credentials[i].jwt.client_secret`)
* `aio_api_key` your Adobe Developer Console jwt credential API Key (or Client ID) (`project.workspace.details.credentials[i].jwt.client_id`)
* `aio_meta_scopes` a comma separated list of metascopes associated with your API, see your Adobe Developer Console jwt credential metascopes (`project.workspace.details.credentials[i].jwt.meta_scopes`)
* `aio_technical_account_id` your Adobe Developer Console jwt credential technical account id (`project.workspace.details.credentials[i].jwt.technical_account_id`)

### Private Key

On top of these, the [`Workspace`](./src/main/java/com/adobe/aio/workspace/Workspace.java) POJO 
can also hold your private Key 
(associated with the public key you uploaded in your Adobe Developer Console Workspace)
this will help to power Adobe JWT authentication flow and transparently add the proper `Bearer`
authentication token to all your request, 
Note the easiest way is to stuff your Private Key as a `pkcs8` base64 encoded String 
using the `aio_encoded_pkcs8` property key.
confer  [aio-lib-java-ims](../ims/README.md#Create-and-configure-your-public-and-private-key) documentation for more details.

## Builds

This Library is build with [maven](https://maven.apache.org/) (it also runs the unit tests):

### Contributing

Contributions are welcomed! Read the [Contributing Guide](../.github/CONTRIBUTING.md) for more information.

### Licensing

This project is licensed under the Apache V2 License. See [LICENSE](../LICENSE.md) for more information.
  

  
