begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.store.driver
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
name|store
operator|.
name|driver
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
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
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|StateStoreService
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
name|StateStoreUnavailableException
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
name|records
operator|.
name|BaseRecord
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
name|Time
import|;
end_import

begin_comment
comment|/**  * Driver class for an implementation of a {@link StateStoreService}  * provider. Driver implementations will extend this class and implement some of  * the default methods.  */
end_comment

begin_class
DECL|class|StateStoreDriver
specifier|public
specifier|abstract
class|class
name|StateStoreDriver
implements|implements
name|StateStoreRecordOperations
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
name|StateStoreDriver
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** State Store configuration. */
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
comment|/** Identifier for the driver. */
DECL|field|identifier
specifier|private
name|String
name|identifier
decl_stmt|;
comment|/**    * Initialize the state store connection.    * @param config Configuration for the driver.    * @param id Identifier for the driver.    * @param records Records that are supported.    * @return If initialized and ready, false if failed to initialize driver.    */
DECL|method|init (final Configuration config, final String id, final List<Class<? extends BaseRecord>> records)
specifier|public
name|boolean
name|init
parameter_list|(
specifier|final
name|Configuration
name|config
parameter_list|,
specifier|final
name|String
name|id
parameter_list|,
specifier|final
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|BaseRecord
argument_list|>
argument_list|>
name|records
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|identifier
operator|=
name|id
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|identifier
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"The identifier for the State Store connection is not set"
argument_list|)
expr_stmt|;
block|}
comment|// TODO stub
return|return
literal|false
return|;
block|}
comment|/**    * Get the State Store configuration.    *    * @return Configuration for the State Store.    */
DECL|method|getConf ()
specifier|protected
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|this
operator|.
name|conf
return|;
block|}
comment|/**    * Gets a unique identifier for the running task/process. Typically the    * router address.    *    * @return Unique identifier for the running task.    */
DECL|method|getIdentifier ()
specifier|public
name|String
name|getIdentifier
parameter_list|()
block|{
return|return
name|this
operator|.
name|identifier
return|;
block|}
comment|/**    * Prepare the driver to access data storage.    *    * @return True if the driver was successfully initialized. If false is    *         returned, the state store will periodically attempt to    *         re-initialize the driver and the router will remain in safe mode    *         until the driver is initialized.    */
DECL|method|initDriver ()
specifier|public
specifier|abstract
name|boolean
name|initDriver
parameter_list|()
function_decl|;
comment|/**    * Initialize storage for a single record class.    *    * @param name String reference of the record class to initialize, used to    *             construct paths and file names for the record. Determined by    *             configuration settings for the specific driver.    * @param clazz Record type corresponding to the provided name.    * @return True if successful, false otherwise.    */
DECL|method|initRecordStorage ( String className, Class<T> clazz)
specifier|public
specifier|abstract
parameter_list|<
name|T
extends|extends
name|BaseRecord
parameter_list|>
name|boolean
name|initRecordStorage
parameter_list|(
name|String
name|className
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
function_decl|;
comment|/**    * Check if the driver is currently running and the data store connection is    * valid.    *    * @return True if the driver is initialized and the data store is ready.    */
DECL|method|isDriverReady ()
specifier|public
specifier|abstract
name|boolean
name|isDriverReady
parameter_list|()
function_decl|;
comment|/**    * Check if the driver is ready to be used and throw an exception otherwise.    *    * @throws StateStoreUnavailableException If the driver is not ready.    */
DECL|method|verifyDriverReady ()
specifier|public
name|void
name|verifyDriverReady
parameter_list|()
throws|throws
name|StateStoreUnavailableException
block|{
if|if
condition|(
operator|!
name|isDriverReady
argument_list|()
condition|)
block|{
name|String
name|driverName
init|=
name|getDriverName
argument_list|()
decl_stmt|;
name|String
name|hostname
init|=
name|getHostname
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|StateStoreUnavailableException
argument_list|(
literal|"State Store driver "
operator|+
name|driverName
operator|+
literal|" in "
operator|+
name|hostname
operator|+
literal|" is not ready."
argument_list|)
throw|;
block|}
block|}
comment|/**    * Close the State Store driver connection.    */
DECL|method|close ()
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**    * Returns the current time synchronization from the underlying store.    * Override for stores that supply a current date. The data store driver is    * responsible for maintaining the official synchronization time/date for all    * distributed components.    *    * @return Current time stamp, used for all synchronization dates.    */
DECL|method|getTime ()
specifier|public
name|long
name|getTime
parameter_list|()
block|{
return|return
name|Time
operator|.
name|now
argument_list|()
return|;
block|}
comment|/**    * Get the name of the driver implementation for debugging.    *    * @return Name of the driver implementation.    */
DECL|method|getDriverName ()
specifier|private
name|String
name|getDriverName
parameter_list|()
block|{
return|return
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
return|;
block|}
comment|/**    * Get the host name of the machine running the driver for debugging.    *    * @return Host name of the machine running the driver.    */
DECL|method|getHostname ()
specifier|private
name|String
name|getHostname
parameter_list|()
block|{
name|String
name|hostname
init|=
literal|"Unknown"
decl_stmt|;
try|try
block|{
name|hostname
operator|=
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getHostName
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot get local address"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|hostname
return|;
block|}
block|}
end_class

end_unit

