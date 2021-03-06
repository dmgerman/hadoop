begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.store.impl
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
name|impl
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
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|DisabledNameserviceStore
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
comment|/**  * Implementation of {@link DisabledNameserviceStore}.  */
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
DECL|class|DisabledNameserviceStoreImpl
specifier|public
class|class
name|DisabledNameserviceStoreImpl
extends|extends
name|DisabledNameserviceStore
block|{
DECL|method|DisabledNameserviceStoreImpl (StateStoreDriver driver)
specifier|public
name|DisabledNameserviceStoreImpl
parameter_list|(
name|StateStoreDriver
name|driver
parameter_list|)
block|{
name|super
argument_list|(
name|driver
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|disableNameservice (String nsId)
specifier|public
name|boolean
name|disableNameservice
parameter_list|(
name|String
name|nsId
parameter_list|)
throws|throws
name|IOException
block|{
name|DisabledNameservice
name|record
init|=
name|DisabledNameservice
operator|.
name|newInstance
argument_list|(
name|nsId
argument_list|)
decl_stmt|;
return|return
name|getDriver
argument_list|()
operator|.
name|put
argument_list|(
name|record
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|enableNameservice (String nsId)
specifier|public
name|boolean
name|enableNameservice
parameter_list|(
name|String
name|nsId
parameter_list|)
throws|throws
name|IOException
block|{
name|DisabledNameservice
name|record
init|=
name|DisabledNameservice
operator|.
name|newInstance
argument_list|(
name|nsId
argument_list|)
decl_stmt|;
return|return
name|getDriver
argument_list|()
operator|.
name|remove
argument_list|(
name|record
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDisabledNameservices ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getDisabledNameservices
parameter_list|()
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|disabledNameservices
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|DisabledNameservice
name|record
range|:
name|getCachedRecords
argument_list|()
control|)
block|{
name|String
name|nsId
init|=
name|record
operator|.
name|getNameserviceId
argument_list|()
decl_stmt|;
name|disabledNameservices
operator|.
name|add
argument_list|(
name|nsId
argument_list|)
expr_stmt|;
block|}
return|return
name|disabledNameservices
return|;
block|}
block|}
end_class

end_unit

