begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.store
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|driver
operator|.
name|StateStoreDriver
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

begin_comment
comment|/**  * Store records in the State Store. Subclasses provide interfaces to operate on  * those records.  *  * @param<R> Record to store by this interface.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|RecordStore
specifier|public
specifier|abstract
class|class
name|RecordStore
parameter_list|<
name|R
extends|extends
name|BaseRecord
parameter_list|>
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RecordStore
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Class of the record stored in this State Store. */
DECL|field|recordClass
specifier|private
specifier|final
name|Class
argument_list|<
name|R
argument_list|>
name|recordClass
decl_stmt|;
comment|/** State store driver backed by persistent storage. */
DECL|field|driver
specifier|private
specifier|final
name|StateStoreDriver
name|driver
decl_stmt|;
comment|/**    * Create a new store for records.    *    * @param clazz Class of the record to store.    * @param stateStoreDriver Driver for the State Store.    */
DECL|method|RecordStore (Class<R> clazz, StateStoreDriver stateStoreDriver)
specifier|protected
name|RecordStore
parameter_list|(
name|Class
argument_list|<
name|R
argument_list|>
name|clazz
parameter_list|,
name|StateStoreDriver
name|stateStoreDriver
parameter_list|)
block|{
name|this
operator|.
name|recordClass
operator|=
name|clazz
expr_stmt|;
name|this
operator|.
name|driver
operator|=
name|stateStoreDriver
expr_stmt|;
block|}
comment|/**    * Report a required record to the data store. The data store uses this to    * create/maintain storage for the record.    *    * @return The class of the required record or null if no record is required    *         for this interface.    */
DECL|method|getRecordClass ()
specifier|public
name|Class
argument_list|<
name|R
argument_list|>
name|getRecordClass
parameter_list|()
block|{
return|return
name|this
operator|.
name|recordClass
return|;
block|}
comment|/**    * Get the State Store driver.    *    * @return State Store driver.    */
DECL|method|getDriver ()
specifier|protected
name|StateStoreDriver
name|getDriver
parameter_list|()
block|{
return|return
name|this
operator|.
name|driver
return|;
block|}
comment|/**    * Build a state store API implementation interface.    *    * @param clazz The specific interface implementation to create    * @param driver The {@link StateStoreDriver} implementation in use.    * @return An initialized instance of the specified state store API    *         implementation.    */
DECL|method|newInstance ( final Class<T> clazz, final StateStoreDriver driver)
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|RecordStore
argument_list|<
name|?
argument_list|>
parameter_list|>
name|T
name|newInstance
parameter_list|(
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|,
specifier|final
name|StateStoreDriver
name|driver
parameter_list|)
block|{
try|try
block|{
name|Constructor
argument_list|<
name|T
argument_list|>
name|constructor
init|=
name|clazz
operator|.
name|getConstructor
argument_list|(
name|StateStoreDriver
operator|.
name|class
argument_list|)
decl_stmt|;
name|T
name|recordStore
init|=
name|constructor
operator|.
name|newInstance
argument_list|(
name|driver
argument_list|)
decl_stmt|;
return|return
name|recordStore
return|;
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
literal|"Cannot create new instance for "
operator|+
name|clazz
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

