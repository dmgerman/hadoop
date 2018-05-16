begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client.api
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|client
operator|.
name|api
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Public
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceStability
operator|.
name|Unstable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|service
operator|.
name|CompositeService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|ReflectionUtils
import|;
end_import

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
name|exceptions
operator|.
name|YarnException
import|;
end_import

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
name|exceptions
operator|.
name|YarnRuntimeException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Client for managing applications.  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Unstable
DECL|class|AppAdminClient
specifier|public
specifier|abstract
class|class
name|AppAdminClient
extends|extends
name|CompositeService
block|{
DECL|field|YARN_APP_ADMIN_CLIENT_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|YARN_APP_ADMIN_CLIENT_PREFIX
init|=
literal|"yarn"
operator|+
literal|".application.admin.client.class."
decl_stmt|;
DECL|field|DEFAULT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_TYPE
init|=
literal|"yarn-service"
decl_stmt|;
DECL|field|DEFAULT_CLASS_NAME
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_CLASS_NAME
init|=
literal|"org.apache.hadoop.yarn"
operator|+
literal|".service.client.ApiServiceClient"
decl_stmt|;
DECL|field|UNIT_TEST_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|UNIT_TEST_TYPE
init|=
literal|"unit-test"
decl_stmt|;
DECL|field|UNIT_TEST_CLASS_NAME
specifier|public
specifier|static
specifier|final
name|String
name|UNIT_TEST_CLASS_NAME
init|=
literal|"org.apache.hadoop.yarn"
operator|+
literal|".service.client.ServiceClient"
decl_stmt|;
annotation|@
name|Private
DECL|method|AppAdminClient ()
specifier|protected
name|AppAdminClient
parameter_list|()
block|{
name|super
argument_list|(
name|AppAdminClient
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    *<p>    * Create a new instance of AppAdminClient.    *</p>    *    * @param appType application type    * @param conf configuration    * @return app admin client    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|createAppAdminClient (String appType, Configuration conf)
specifier|public
specifier|static
name|AppAdminClient
name|createAppAdminClient
parameter_list|(
name|String
name|appType
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|clientClassMap
init|=
name|conf
operator|.
name|getPropsWithPrefix
argument_list|(
name|YARN_APP_ADMIN_CLIENT_PREFIX
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|clientClassMap
operator|.
name|containsKey
argument_list|(
name|DEFAULT_TYPE
argument_list|)
condition|)
block|{
name|clientClassMap
operator|.
name|put
argument_list|(
name|DEFAULT_TYPE
argument_list|,
name|DEFAULT_CLASS_NAME
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|clientClassMap
operator|.
name|containsKey
argument_list|(
name|UNIT_TEST_TYPE
argument_list|)
condition|)
block|{
name|clientClassMap
operator|.
name|put
argument_list|(
name|UNIT_TEST_TYPE
argument_list|,
name|UNIT_TEST_CLASS_NAME
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|clientClassMap
operator|.
name|containsKey
argument_list|(
name|appType
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"App admin client class name not "
operator|+
literal|"specified for type "
operator|+
name|appType
argument_list|)
throw|;
block|}
name|String
name|clientClassName
init|=
name|clientClassMap
operator|.
name|get
argument_list|(
name|appType
argument_list|)
decl_stmt|;
name|Class
argument_list|<
name|?
extends|extends
name|AppAdminClient
argument_list|>
name|clientClass
decl_stmt|;
try|try
block|{
name|clientClass
operator|=
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|AppAdminClient
argument_list|>
operator|)
name|Class
operator|.
name|forName
argument_list|(
name|clientClassName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Invalid app admin client class"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|AppAdminClient
name|appAdminClient
init|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|clientClass
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|appAdminClient
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|appAdminClient
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|appAdminClient
return|;
block|}
comment|/**    *<p>    * Launch a new YARN application.    *</p>    *    * @param fileName specification of application    * @param appName name of the application    * @param lifetime lifetime of the application    * @param queue queue of the application    * @return exit code    * @throws IOException IOException    * @throws YarnException exception in client or server    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|actionLaunch (String fileName, String appName, Long lifetime, String queue)
specifier|public
specifier|abstract
name|int
name|actionLaunch
parameter_list|(
name|String
name|fileName
parameter_list|,
name|String
name|appName
parameter_list|,
name|Long
name|lifetime
parameter_list|,
name|String
name|queue
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
comment|/**    *<p>    * Stop a YARN application (attempt to stop gracefully before killing the    * application). In the case of a long-running service, the service may be    * restarted later.    *</p>    *    * @param appName the name of the application    * @return exit code    * @throws IOException IOException    * @throws YarnException exception in client or server    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|actionStop (String appName)
specifier|public
specifier|abstract
name|int
name|actionStop
parameter_list|(
name|String
name|appName
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
comment|/**    *<p>    * Start a YARN application from a previously saved specification. In the    * case of a long-running service, the service must have been previously    * launched/started and then stopped, or previously saved but not started.    *</p>    *    * @param appName the name of the application    * @return exit code    * @throws IOException IOException    * @throws YarnException exception in client or server    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|actionStart (String appName)
specifier|public
specifier|abstract
name|int
name|actionStart
parameter_list|(
name|String
name|appName
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
comment|/**    *<p>    * Save the specification for a YARN application / long-running service.    * The application may be started later.    *</p>    *    * @param fileName specification of application to save    * @param appName name of the application    * @param lifetime lifetime of the application    * @param queue queue of the application    * @return exit code    * @throws IOException IOException    * @throws YarnException exception in client or server    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|actionSave (String fileName, String appName, Long lifetime, String queue)
specifier|public
specifier|abstract
name|int
name|actionSave
parameter_list|(
name|String
name|fileName
parameter_list|,
name|String
name|appName
parameter_list|,
name|Long
name|lifetime
parameter_list|,
name|String
name|queue
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
comment|/**    *<p>    * Remove the specification and all application data for a YARN application.    * The application cannot be running.    *</p>    *    * @param appName the name of the application    * @return exit code    * @throws IOException IOException    * @throws YarnException exception in client or server    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|actionDestroy (String appName)
specifier|public
specifier|abstract
name|int
name|actionDestroy
parameter_list|(
name|String
name|appName
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
comment|/**    *<p>    * Change the number of running containers for a component of a YARN    * application / long-running service.    *</p>    *    * @param appName the name of the application    * @param componentCounts map of component name to new component count or    *                        amount to change existing component count (e.g.    *                        5, +5, -5)    * @return exit code    * @throws IOException IOException    * @throws YarnException exception in client or server    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|actionFlex (String appName, Map<String, String> componentCounts)
specifier|public
specifier|abstract
name|int
name|actionFlex
parameter_list|(
name|String
name|appName
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|componentCounts
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
comment|/**    *<p>    * Upload AM dependencies to HDFS. This makes future application launches    * faster since the dependencies do not have to be uploaded on each launch.    *</p>    *    * @param destinationFolder    *          an optional HDFS folder where dependency tarball will be uploaded    * @return exit code    * @throws IOException    *           IOException    * @throws YarnException    *           exception in client or server    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|enableFastLaunch (String destinationFolder)
specifier|public
specifier|abstract
name|int
name|enableFastLaunch
parameter_list|(
name|String
name|destinationFolder
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
comment|/**    *<p>    * Get detailed app specific status string for a YARN application.    *</p>    *    * @param appIdOrName appId or appName    * @return status string    * @throws IOException IOException    * @throws YarnException exception in client or server    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getStatusString (String appIdOrName)
specifier|public
specifier|abstract
name|String
name|getStatusString
parameter_list|(
name|String
name|appIdOrName
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
comment|/**    * Initiate upgrade of a long running service.    *    * @param appName      the name of the application.    * @param fileName     specification of application upgrade to save.    * @param autoFinalize when true, finalization of upgrade will be done    *                     automatically.    * @return exit code    * @throws IOException   IOException    * @throws YarnException exception in client or server    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|initiateUpgrade (String appName, String fileName, boolean autoFinalize)
specifier|public
specifier|abstract
name|int
name|initiateUpgrade
parameter_list|(
name|String
name|appName
parameter_list|,
name|String
name|fileName
parameter_list|,
name|boolean
name|autoFinalize
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
comment|/**    * Upgrade component instances of a long running service.    *    * @param appName            the name of the application.    * @param componentInstances the name of the component instances.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|actionUpgradeInstances (String appName, List<String> componentInstances)
specifier|public
specifier|abstract
name|int
name|actionUpgradeInstances
parameter_list|(
name|String
name|appName
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|componentInstances
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
comment|/**    * Upgrade components of a long running service.    *    * @param appName    the name of the application.    * @param components the name of the components.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|actionUpgradeComponents (String appName, List<String> components)
specifier|public
specifier|abstract
name|int
name|actionUpgradeComponents
parameter_list|(
name|String
name|appName
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|components
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
block|}
end_class

end_unit

