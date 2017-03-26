begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.common
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|common
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|ApplicationConstants
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Keys and various constants for Slider  */
end_comment

begin_interface
DECL|interface|SliderKeys
specifier|public
interface|interface
name|SliderKeys
extends|extends
name|SliderXmlConfKeys
block|{
comment|/**    * This is the name of the slider appmaster in configurations :{@value}    */
DECL|field|COMPONENT_AM
name|String
name|COMPONENT_AM
init|=
literal|"slider-appmaster"
decl_stmt|;
comment|/**    * Slider role is "special":{@value}    */
DECL|field|ROLE_AM_PRIORITY_INDEX
name|int
name|ROLE_AM_PRIORITY_INDEX
init|=
literal|0
decl_stmt|;
comment|/**    * The path under which cluster and temp data are stored    * {@value}    */
DECL|field|SLIDER_BASE_DIRECTORY
name|String
name|SLIDER_BASE_DIRECTORY
init|=
literal|".slider"
decl_stmt|;
comment|/**    * The paths under which Slider AM dependency libraries are stored    */
DECL|field|SLIDER_DEPENDENCY_LOCALIZED_DIR_LINK
name|String
name|SLIDER_DEPENDENCY_LOCALIZED_DIR_LINK
init|=
literal|"slider_dep"
decl_stmt|;
DECL|field|SLIDER_DEPENDENCY_HDP_PARENT_DIR
name|String
name|SLIDER_DEPENDENCY_HDP_PARENT_DIR
init|=
literal|"/hdp"
decl_stmt|;
DECL|field|SLIDER_DEPENDENCY_DIR
name|String
name|SLIDER_DEPENDENCY_DIR
init|=
literal|"/apps/%s/slider"
decl_stmt|;
DECL|field|SLIDER_DEPENDENCY_TAR_GZ_FILE_NAME
name|String
name|SLIDER_DEPENDENCY_TAR_GZ_FILE_NAME
init|=
literal|"slider-dep"
decl_stmt|;
DECL|field|SLIDER_DEPENDENCY_TAR_GZ_FILE_EXT
name|String
name|SLIDER_DEPENDENCY_TAR_GZ_FILE_EXT
init|=
literal|".tar.gz"
decl_stmt|;
DECL|field|SLIDER_DEPENDENCY_DIR_PERMISSIONS
name|String
name|SLIDER_DEPENDENCY_DIR_PERMISSIONS
init|=
literal|"755"
decl_stmt|;
comment|/**    *     */
DECL|field|HDP_VERSION_PROP_NAME
name|String
name|HDP_VERSION_PROP_NAME
init|=
literal|"HDP_VERSION"
decl_stmt|;
comment|/**    *  name of the relative path to expaned an image into:  {@value}.    *  The title of this path is to help people understand it when    *  they see it in their error messages    */
DECL|field|LOCAL_TARBALL_INSTALL_SUBDIR
name|String
name|LOCAL_TARBALL_INSTALL_SUBDIR
init|=
literal|"expandedarchive"
decl_stmt|;
comment|/**    * Application type for YARN  {@value}    */
DECL|field|APP_TYPE
name|String
name|APP_TYPE
init|=
literal|"org-apache-slider"
decl_stmt|;
comment|/**    * Key for component type. This MUST NOT be set in app_config/global {@value}    */
DECL|field|COMPONENT_TYPE_KEY
name|String
name|COMPONENT_TYPE_KEY
init|=
literal|"site.global.component_type"
decl_stmt|;
comment|/**    * A component type for an external app that has been predefined using the    * slider build command    */
DECL|field|COMPONENT_TYPE_EXTERNAL_APP
name|String
name|COMPONENT_TYPE_EXTERNAL_APP
init|=
literal|"external_app"
decl_stmt|;
DECL|field|COMPONENT_SEPARATOR
name|String
name|COMPONENT_SEPARATOR
init|=
literal|"-"
decl_stmt|;
DECL|field|COMPONENT_KEYS_TO_SKIP
name|List
argument_list|<
name|String
argument_list|>
name|COMPONENT_KEYS_TO_SKIP
init|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"zookeeper."
argument_list|,
literal|"env.MALLOC_ARENA_MAX"
argument_list|,
literal|"site.fs."
argument_list|,
literal|"site.dfs."
argument_list|)
argument_list|)
decl_stmt|;
comment|/**    * A component type for a client component    */
DECL|field|COMPONENT_TYPE_CLIENT
name|String
name|COMPONENT_TYPE_CLIENT
init|=
literal|"client"
decl_stmt|;
comment|/**    * Key for application version. This must be set in app_config/global {@value}    */
DECL|field|APP_VERSION
name|String
name|APP_VERSION
init|=
literal|"site.global.app_version"
decl_stmt|;
DECL|field|APP_VERSION_UNKNOWN
name|String
name|APP_VERSION_UNKNOWN
init|=
literal|"awaiting heartbeat..."
decl_stmt|;
comment|/**    * Keys for application container specific properties, like release timeout    */
DECL|field|APP_CONTAINER_RELEASE_TIMEOUT
name|String
name|APP_CONTAINER_RELEASE_TIMEOUT
init|=
literal|"site.global.app_container.release_timeout_secs"
decl_stmt|;
DECL|field|APP_CONTAINER_HEARTBEAT_INTERVAL_SEC
name|int
name|APP_CONTAINER_HEARTBEAT_INTERVAL_SEC
init|=
literal|10
decl_stmt|;
comment|// look for HEARTBEAT_IDDLE_INTERVAL_SEC
comment|/**    * JVM arg to force IPv4  {@value}    */
DECL|field|JVM_ENABLE_ASSERTIONS
name|String
name|JVM_ENABLE_ASSERTIONS
init|=
literal|"-ea"
decl_stmt|;
comment|/**    * JVM arg enable JVM system/runtime {@value}    */
DECL|field|JVM_ENABLE_SYSTEM_ASSERTIONS
name|String
name|JVM_ENABLE_SYSTEM_ASSERTIONS
init|=
literal|"-esa"
decl_stmt|;
comment|/**    * JVM arg to force IPv4  {@value}    */
DECL|field|JVM_FORCE_IPV4
name|String
name|JVM_FORCE_IPV4
init|=
literal|"-Djava.net.preferIPv4Stack=true"
decl_stmt|;
comment|/**    * JVM arg to go headless  {@value}    */
DECL|field|JVM_JAVA_HEADLESS
name|String
name|JVM_JAVA_HEADLESS
init|=
literal|"-Djava.awt.headless=true"
decl_stmt|;
comment|/**    * This is the name of the dir/subdir containing    * the hbase conf that is propagated via YARN    *  {@value}    */
DECL|field|PROPAGATED_CONF_DIR_NAME
name|String
name|PROPAGATED_CONF_DIR_NAME
init|=
literal|"propagatedconf"
decl_stmt|;
DECL|field|INFRA_DIR_NAME
name|String
name|INFRA_DIR_NAME
init|=
literal|"infra"
decl_stmt|;
DECL|field|GENERATED_CONF_DIR_NAME
name|String
name|GENERATED_CONF_DIR_NAME
init|=
literal|"generated"
decl_stmt|;
DECL|field|SNAPSHOT_CONF_DIR_NAME
name|String
name|SNAPSHOT_CONF_DIR_NAME
init|=
literal|"snapshot"
decl_stmt|;
DECL|field|DATA_DIR_NAME
name|String
name|DATA_DIR_NAME
init|=
literal|"database"
decl_stmt|;
DECL|field|HISTORY_DIR_NAME
name|String
name|HISTORY_DIR_NAME
init|=
literal|"history"
decl_stmt|;
DECL|field|HISTORY_FILENAME_SUFFIX
name|String
name|HISTORY_FILENAME_SUFFIX
init|=
literal|"json"
decl_stmt|;
DECL|field|HISTORY_FILENAME_PREFIX
name|String
name|HISTORY_FILENAME_PREFIX
init|=
literal|"rolehistory-"
decl_stmt|;
DECL|field|KEYTAB_DIR
name|String
name|KEYTAB_DIR
init|=
literal|"keytabs"
decl_stmt|;
DECL|field|RESOURCE_DIR
name|String
name|RESOURCE_DIR
init|=
literal|"resources"
decl_stmt|;
comment|/**    * Filename pattern is required to save in strict temporal order.    * Important: older files must sort less-than newer files when using    * case-sensitive name sort.    */
DECL|field|HISTORY_FILENAME_CREATION_PATTERN
name|String
name|HISTORY_FILENAME_CREATION_PATTERN
init|=
name|HISTORY_FILENAME_PREFIX
operator|+
literal|"%016x."
operator|+
name|HISTORY_FILENAME_SUFFIX
decl_stmt|;
comment|/**    * The posix regexp used to locate this     */
DECL|field|HISTORY_FILENAME_MATCH_PATTERN
name|String
name|HISTORY_FILENAME_MATCH_PATTERN
init|=
name|HISTORY_FILENAME_PREFIX
operator|+
literal|"[0-9a-f]+\\."
operator|+
name|HISTORY_FILENAME_SUFFIX
decl_stmt|;
comment|/**    * The posix regexp used to locate this     */
DECL|field|HISTORY_FILENAME_GLOB_PATTERN
name|String
name|HISTORY_FILENAME_GLOB_PATTERN
init|=
name|HISTORY_FILENAME_PREFIX
operator|+
literal|"*."
operator|+
name|HISTORY_FILENAME_SUFFIX
decl_stmt|;
comment|/**    * XML resource listing the standard Slider providers    * {@value}    */
DECL|field|SLIDER_XML
name|String
name|SLIDER_XML
init|=
literal|"org/apache/slider/slider.xml"
decl_stmt|;
DECL|field|CLUSTER_DIRECTORY
name|String
name|CLUSTER_DIRECTORY
init|=
literal|"cluster"
decl_stmt|;
DECL|field|PACKAGE_DIRECTORY
name|String
name|PACKAGE_DIRECTORY
init|=
literal|"package"
decl_stmt|;
comment|/**    * JVM property to define the slider configuration directory;    * this is set by the slider script: {@value}    */
DECL|field|PROPERTY_CONF_DIR
name|String
name|PROPERTY_CONF_DIR
init|=
literal|"slider.confdir"
decl_stmt|;
comment|/**    * JVM property to define the slider lib directory;    * this is set by the slider script: {@value}    */
DECL|field|PROPERTY_LIB_DIR
name|String
name|PROPERTY_LIB_DIR
init|=
literal|"slider.libdir"
decl_stmt|;
comment|/**    * name of generated dir for this conf: {@value}    */
DECL|field|SUBMITTED_CONF_DIR
name|String
name|SUBMITTED_CONF_DIR
init|=
literal|"conf"
decl_stmt|;
comment|/**    * Slider AM log4j file name : {@value}    */
DECL|field|LOG4J_SERVER_PROP_FILENAME
name|String
name|LOG4J_SERVER_PROP_FILENAME
init|=
literal|"slideram-log4j.properties"
decl_stmt|;
comment|/**    * Standard log4j file name  : {@value}    */
DECL|field|LOG4J_PROP_FILENAME
name|String
name|LOG4J_PROP_FILENAME
init|=
literal|"log4j.properties"
decl_stmt|;
comment|/**    * Log4j sysprop to name the resource :{@value}    */
DECL|field|SYSPROP_LOG4J_CONFIGURATION
name|String
name|SYSPROP_LOG4J_CONFIGURATION
init|=
literal|"log4j.configuration"
decl_stmt|;
comment|/**    * sysprop for Slider AM log4j directory :{@value}    */
DECL|field|SYSPROP_LOG_DIR
name|String
name|SYSPROP_LOG_DIR
init|=
literal|"LOG_DIR"
decl_stmt|;
comment|/**    * name of the Slider client resource    * loaded when the service is loaded.    */
DECL|field|SLIDER_CLIENT_XML
name|String
name|SLIDER_CLIENT_XML
init|=
literal|"slider-client.xml"
decl_stmt|;
comment|/**    * The name of the resource to put on the classpath    */
DECL|field|SLIDER_SERVER_XML
name|String
name|SLIDER_SERVER_XML
init|=
literal|"slider-server.xml"
decl_stmt|;
DECL|field|TMP_LOGDIR_PREFIX
name|String
name|TMP_LOGDIR_PREFIX
init|=
literal|"/tmp/slider-"
decl_stmt|;
DECL|field|TMP_DIR_PREFIX
name|String
name|TMP_DIR_PREFIX
init|=
literal|"tmp"
decl_stmt|;
DECL|field|AM_DIR_PREFIX
name|String
name|AM_DIR_PREFIX
init|=
literal|"appmaster"
decl_stmt|;
comment|/**    * Store the default app definition, e.g. metainfo file or content of a folder    */
DECL|field|APP_DEF_DIR
name|String
name|APP_DEF_DIR
init|=
literal|"appdef"
decl_stmt|;
comment|/**    * Store additional app defs - co-processors    */
DECL|field|ADDONS_DIR
name|String
name|ADDONS_DIR
init|=
literal|"addons"
decl_stmt|;
DECL|field|SLIDER_JAR
name|String
name|SLIDER_JAR
init|=
literal|"slider-core.jar"
decl_stmt|;
DECL|field|JCOMMANDER_JAR
name|String
name|JCOMMANDER_JAR
init|=
literal|"jcommander.jar"
decl_stmt|;
DECL|field|GSON_JAR
name|String
name|GSON_JAR
init|=
literal|"gson.jar"
decl_stmt|;
DECL|field|DEFAULT_APP_PKG
name|String
name|DEFAULT_APP_PKG
init|=
literal|"appPkg.zip"
decl_stmt|;
DECL|field|DEFAULT_JVM_HEAP
name|String
name|DEFAULT_JVM_HEAP
init|=
literal|"256M"
decl_stmt|;
DECL|field|DEFAULT_YARN_MEMORY
name|int
name|DEFAULT_YARN_MEMORY
init|=
literal|256
decl_stmt|;
DECL|field|STDOUT_AM
name|String
name|STDOUT_AM
init|=
literal|"slider-out.txt"
decl_stmt|;
DECL|field|STDERR_AM
name|String
name|STDERR_AM
init|=
literal|"slider-err.txt"
decl_stmt|;
DECL|field|DEFAULT_GC_OPTS
name|String
name|DEFAULT_GC_OPTS
init|=
literal|""
decl_stmt|;
DECL|field|HADOOP_USER_NAME
name|String
name|HADOOP_USER_NAME
init|=
name|ApplicationConstants
operator|.
name|Environment
operator|.
name|USER
operator|.
name|toString
argument_list|()
decl_stmt|;
DECL|field|HADOOP_PROXY_USER
name|String
name|HADOOP_PROXY_USER
init|=
literal|"HADOOP_PROXY_USER"
decl_stmt|;
DECL|field|SLIDER_PASSPHRASE
name|String
name|SLIDER_PASSPHRASE
init|=
literal|"SLIDER_PASSPHRASE"
decl_stmt|;
DECL|field|PROPAGATE_RESOURCE_OPTION
name|boolean
name|PROPAGATE_RESOURCE_OPTION
init|=
literal|true
decl_stmt|;
comment|/**    * Security associated keys.    */
DECL|field|SECURITY_DIR
name|String
name|SECURITY_DIR
init|=
literal|"security"
decl_stmt|;
DECL|field|CRT_FILE_NAME
name|String
name|CRT_FILE_NAME
init|=
literal|"ca.crt"
decl_stmt|;
DECL|field|CSR_FILE_NAME
name|String
name|CSR_FILE_NAME
init|=
literal|"ca.csr"
decl_stmt|;
DECL|field|KEY_FILE_NAME
name|String
name|KEY_FILE_NAME
init|=
literal|"ca.key"
decl_stmt|;
DECL|field|KEYSTORE_FILE_NAME
name|String
name|KEYSTORE_FILE_NAME
init|=
literal|"keystore.p12"
decl_stmt|;
DECL|field|CRT_PASS_FILE_NAME
name|String
name|CRT_PASS_FILE_NAME
init|=
literal|"pass.txt"
decl_stmt|;
DECL|field|PASS_LEN
name|String
name|PASS_LEN
init|=
literal|"50"
decl_stmt|;
DECL|field|COMP_STORES_REQUIRED_KEY
name|String
name|COMP_STORES_REQUIRED_KEY
init|=
literal|"slider.component.security.stores.required"
decl_stmt|;
DECL|field|COMP_KEYSTORE_PASSWORD_PROPERTY_KEY
name|String
name|COMP_KEYSTORE_PASSWORD_PROPERTY_KEY
init|=
literal|"slider.component.keystore.password.property"
decl_stmt|;
DECL|field|COMP_KEYSTORE_PASSWORD_ALIAS_KEY
name|String
name|COMP_KEYSTORE_PASSWORD_ALIAS_KEY
init|=
literal|"slider.component.keystore.credential.alias.property"
decl_stmt|;
DECL|field|COMP_KEYSTORE_PASSWORD_ALIAS_DEFAULT
name|String
name|COMP_KEYSTORE_PASSWORD_ALIAS_DEFAULT
init|=
literal|"component.keystore.credential.alias"
decl_stmt|;
DECL|field|COMP_TRUSTSTORE_PASSWORD_PROPERTY_KEY
name|String
name|COMP_TRUSTSTORE_PASSWORD_PROPERTY_KEY
init|=
literal|"slider.component.truststore.password.property"
decl_stmt|;
DECL|field|COMP_TRUSTSTORE_PASSWORD_ALIAS_KEY
name|String
name|COMP_TRUSTSTORE_PASSWORD_ALIAS_KEY
init|=
literal|"slider.component.truststore.credential.alias.property"
decl_stmt|;
DECL|field|COMP_TRUSTSTORE_PASSWORD_ALIAS_DEFAULT
name|String
name|COMP_TRUSTSTORE_PASSWORD_ALIAS_DEFAULT
init|=
literal|"component.truststore.credential.alias"
decl_stmt|;
comment|/**    * Python specific    */
DECL|field|PYTHONPATH
name|String
name|PYTHONPATH
init|=
literal|"PYTHONPATH"
decl_stmt|;
comment|/**    * Name of the AM filter to use: {@value}    */
DECL|field|AM_FILTER_NAME
name|String
name|AM_FILTER_NAME
init|=
literal|"org.apache.hadoop.yarn.server.webproxy.amfilter.AmFilterInitializer"
decl_stmt|;
comment|/**    * Allowed port range. This MUST be set in app_conf/global.    * {@value}    */
DECL|field|KEY_ALLOWED_PORT_RANGE
name|String
name|KEY_ALLOWED_PORT_RANGE
init|=
literal|"site.global.slider.allowed.ports"
decl_stmt|;
comment|/**    * env var for custom JVM options.    */
DECL|field|SLIDER_JVM_OPTS
name|String
name|SLIDER_JVM_OPTS
init|=
literal|"SLIDER_JVM_OPTS"
decl_stmt|;
DECL|field|SLIDER_CLASSPATH_EXTRA
name|String
name|SLIDER_CLASSPATH_EXTRA
init|=
literal|"SLIDER_CLASSPATH_EXTRA"
decl_stmt|;
DECL|field|YARN_CONTAINER_PATH
name|String
name|YARN_CONTAINER_PATH
init|=
literal|"/node/container/"
decl_stmt|;
DECL|field|GLOBAL_CONFIG_TAG
name|String
name|GLOBAL_CONFIG_TAG
init|=
literal|"global"
decl_stmt|;
DECL|field|SYSTEM_CONFIGS
name|String
name|SYSTEM_CONFIGS
init|=
literal|"system_configs"
decl_stmt|;
DECL|field|JAVA_HOME
name|String
name|JAVA_HOME
init|=
literal|"java_home"
decl_stmt|;
DECL|field|TWO_WAY_SSL_ENABLED
name|String
name|TWO_WAY_SSL_ENABLED
init|=
literal|"ssl.server.client.auth"
decl_stmt|;
DECL|field|INFRA_RUN_SECURITY_DIR
name|String
name|INFRA_RUN_SECURITY_DIR
init|=
literal|"infra/run/security/"
decl_stmt|;
DECL|field|CERT_FILE_LOCALIZATION_PATH
name|String
name|CERT_FILE_LOCALIZATION_PATH
init|=
name|INFRA_RUN_SECURITY_DIR
operator|+
literal|"ca.crt"
decl_stmt|;
DECL|field|AM_CONFIG_GENERATION
name|String
name|AM_CONFIG_GENERATION
init|=
literal|"am.config.generation"
decl_stmt|;
DECL|field|APP_CONF_DIR
name|String
name|APP_CONF_DIR
init|=
literal|"app/conf"
decl_stmt|;
DECL|field|APP_RESOURCES
name|String
name|APP_RESOURCES
init|=
literal|"application.resources"
decl_stmt|;
DECL|field|APP_RESOURCES_DIR
name|String
name|APP_RESOURCES_DIR
init|=
literal|"app/resources"
decl_stmt|;
DECL|field|APP_PACKAGES_DIR
name|String
name|APP_PACKAGES_DIR
init|=
literal|"app/packages"
decl_stmt|;
block|}
end_interface

end_unit

