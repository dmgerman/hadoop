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
name|Set
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
name|DisabledNameservice
import|;
end_import

begin_comment
comment|/**  * State store record to track disabled name services.  */
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
DECL|class|DisabledNameserviceStore
specifier|public
specifier|abstract
class|class
name|DisabledNameserviceStore
extends|extends
name|CachedRecordStore
argument_list|<
name|DisabledNameservice
argument_list|>
block|{
DECL|method|DisabledNameserviceStore (StateStoreDriver driver)
specifier|public
name|DisabledNameserviceStore
parameter_list|(
name|StateStoreDriver
name|driver
parameter_list|)
block|{
name|super
argument_list|(
name|DisabledNameservice
operator|.
name|class
argument_list|,
name|driver
argument_list|)
expr_stmt|;
block|}
comment|/**    * Disable a name service.    *    * @param nsId Identifier of the name service.    * @return If the name service was successfully disabled.    * @throws IOException If the state store could not be queried.    */
DECL|method|disableNameservice (String nsId)
specifier|public
specifier|abstract
name|boolean
name|disableNameservice
parameter_list|(
name|String
name|nsId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Enable a name service.    *    * @param nsId Identifier of the name service.    * @return If the name service was successfully brought back.    * @throws IOException If the state store could not be queried.    */
DECL|method|enableNameservice (String nsId)
specifier|public
specifier|abstract
name|boolean
name|enableNameservice
parameter_list|(
name|String
name|nsId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get a list of disabled name services.    *    * @return List of disabled name services.    * @throws IOException If the state store could not be queried.    */
DECL|method|getDisabledNameservices ()
specifier|public
specifier|abstract
name|Set
argument_list|<
name|String
argument_list|>
name|getDisabledNameservices
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

