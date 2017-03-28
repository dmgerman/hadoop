begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.router
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|router
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|hdfs
operator|.
name|DFSConfigKeys
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|resolver
operator|.
name|ActiveNamenodeResolver
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|resolver
operator|.
name|FileSubclusterResolver
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|StateStoreService
import|;
end_import

begin_comment
comment|/**  * Utilities for managing HDFS federation.  */
end_comment

begin_class
DECL|class|FederationUtil
specifier|public
specifier|final
class|class
name|FederationUtil
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|FederationUtil
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|FederationUtil ()
specifier|private
name|FederationUtil
parameter_list|()
block|{
comment|// Utility Class
block|}
comment|/**    * Create an instance of an interface with a constructor using a state store    * constructor.    *    * @param conf Configuration    * @param context Context object to pass to the instance.    * @param contextType Type of the context passed to the constructor.    * @param configurationKeyName Configuration key to retrieve the class to load    * @param defaultClassName Default class to load if the configuration key is    *          not set    * @param clazz Class/interface that must be implemented by the instance.    * @return New instance of the specified class that implements the desired    *         interface and a single parameter constructor containing a    *         StateStore reference.    */
DECL|method|newInstance (final Configuration conf, final R context, final Class<R> contextClass, final String configKeyName, final String defaultClassName, final Class<T> clazz)
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|,
name|R
parameter_list|>
name|T
name|newInstance
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|R
name|context
parameter_list|,
specifier|final
name|Class
argument_list|<
name|R
argument_list|>
name|contextClass
parameter_list|,
specifier|final
name|String
name|configKeyName
parameter_list|,
specifier|final
name|String
name|defaultClassName
parameter_list|,
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
block|{
name|String
name|className
init|=
name|conf
operator|.
name|get
argument_list|(
name|configKeyName
argument_list|,
name|defaultClassName
argument_list|)
decl_stmt|;
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|instance
init|=
name|conf
operator|.
name|getClassByName
argument_list|(
name|className
argument_list|)
decl_stmt|;
if|if
condition|(
name|clazz
operator|.
name|isAssignableFrom
argument_list|(
name|instance
argument_list|)
condition|)
block|{
if|if
condition|(
name|contextClass
operator|==
literal|null
condition|)
block|{
comment|// Default constructor if no context
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Constructor
argument_list|<
name|T
argument_list|>
name|constructor
init|=
operator|(
name|Constructor
argument_list|<
name|T
argument_list|>
operator|)
name|instance
operator|.
name|getConstructor
argument_list|()
decl_stmt|;
return|return
name|constructor
operator|.
name|newInstance
argument_list|()
return|;
block|}
else|else
block|{
comment|// Constructor with context
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Constructor
argument_list|<
name|T
argument_list|>
name|constructor
init|=
operator|(
name|Constructor
argument_list|<
name|T
argument_list|>
operator|)
name|instance
operator|.
name|getConstructor
argument_list|(
name|Configuration
operator|.
name|class
argument_list|,
name|contextClass
argument_list|)
decl_stmt|;
return|return
name|constructor
operator|.
name|newInstance
argument_list|(
name|conf
argument_list|,
name|context
argument_list|)
return|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Class "
operator|+
name|className
operator|+
literal|" not instance of "
operator|+
name|clazz
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|ReflectiveOperationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Could not instantiate: "
operator|+
name|className
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
comment|/**    * Creates an instance of a FileSubclusterResolver from the configuration.    *    * @param conf Configuration that defines the file resolver class.    * @param obj Context object passed to class constructor.    * @return FileSubclusterResolver    */
DECL|method|newFileSubclusterResolver ( Configuration conf, StateStoreService stateStore)
specifier|public
specifier|static
name|FileSubclusterResolver
name|newFileSubclusterResolver
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|StateStoreService
name|stateStore
parameter_list|)
block|{
return|return
name|newInstance
argument_list|(
name|conf
argument_list|,
name|stateStore
argument_list|,
name|StateStoreService
operator|.
name|class
argument_list|,
name|DFSConfigKeys
operator|.
name|FEDERATION_FILE_RESOLVER_CLIENT_CLASS
argument_list|,
name|DFSConfigKeys
operator|.
name|FEDERATION_FILE_RESOLVER_CLIENT_CLASS_DEFAULT
argument_list|,
name|FileSubclusterResolver
operator|.
name|class
argument_list|)
return|;
block|}
comment|/**    * Creates an instance of an ActiveNamenodeResolver from the configuration.    *    * @param conf Configuration that defines the namenode resolver class.    * @param obj Context object passed to class constructor.    * @return ActiveNamenodeResolver    */
DECL|method|newActiveNamenodeResolver ( Configuration conf, StateStoreService stateStore)
specifier|public
specifier|static
name|ActiveNamenodeResolver
name|newActiveNamenodeResolver
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|StateStoreService
name|stateStore
parameter_list|)
block|{
return|return
name|newInstance
argument_list|(
name|conf
argument_list|,
name|stateStore
argument_list|,
name|StateStoreService
operator|.
name|class
argument_list|,
name|DFSConfigKeys
operator|.
name|FEDERATION_NAMENODE_RESOLVER_CLIENT_CLASS
argument_list|,
name|DFSConfigKeys
operator|.
name|FEDERATION_NAMENODE_RESOLVER_CLIENT_CLASS_DEFAULT
argument_list|,
name|ActiveNamenodeResolver
operator|.
name|class
argument_list|)
return|;
block|}
block|}
end_class

end_unit

