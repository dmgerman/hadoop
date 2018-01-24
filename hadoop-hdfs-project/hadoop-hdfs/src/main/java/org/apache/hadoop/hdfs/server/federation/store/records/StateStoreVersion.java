begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.store.records
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
name|records
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|StateStoreSerializer
import|;
end_import

begin_comment
comment|/**  * Entry to track the version of the State Store data stored in the State Store  * by a Router.  */
end_comment

begin_class
DECL|class|StateStoreVersion
specifier|public
specifier|abstract
class|class
name|StateStoreVersion
extends|extends
name|BaseRecord
block|{
DECL|method|newInstance ()
specifier|public
specifier|static
name|StateStoreVersion
name|newInstance
parameter_list|()
block|{
return|return
name|StateStoreSerializer
operator|.
name|newRecord
argument_list|(
name|StateStoreVersion
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|newInstance (long membershipVersion, long mountTableVersion)
specifier|public
specifier|static
name|StateStoreVersion
name|newInstance
parameter_list|(
name|long
name|membershipVersion
parameter_list|,
name|long
name|mountTableVersion
parameter_list|)
block|{
name|StateStoreVersion
name|record
init|=
name|newInstance
argument_list|()
decl_stmt|;
name|record
operator|.
name|setMembershipVersion
argument_list|(
name|membershipVersion
argument_list|)
expr_stmt|;
name|record
operator|.
name|setMountTableVersion
argument_list|(
name|mountTableVersion
argument_list|)
expr_stmt|;
return|return
name|record
return|;
block|}
DECL|method|getMembershipVersion ()
specifier|public
specifier|abstract
name|long
name|getMembershipVersion
parameter_list|()
function_decl|;
DECL|method|setMembershipVersion (long version)
specifier|public
specifier|abstract
name|void
name|setMembershipVersion
parameter_list|(
name|long
name|version
parameter_list|)
function_decl|;
DECL|method|getMountTableVersion ()
specifier|public
specifier|abstract
name|long
name|getMountTableVersion
parameter_list|()
function_decl|;
DECL|method|setMountTableVersion (long version)
specifier|public
specifier|abstract
name|void
name|setMountTableVersion
parameter_list|(
name|long
name|version
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|getPrimaryKeys ()
specifier|public
name|SortedMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getPrimaryKeys
parameter_list|()
block|{
comment|// This record is not stored directly, no key needed
name|SortedMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
return|return
name|map
return|;
block|}
annotation|@
name|Override
DECL|method|getExpirationMs ()
specifier|public
name|long
name|getExpirationMs
parameter_list|()
block|{
comment|// This record is not stored directly, no expiration needed
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|setDateModified (long time)
specifier|public
name|void
name|setDateModified
parameter_list|(
name|long
name|time
parameter_list|)
block|{
comment|// We don't store this record directly
block|}
annotation|@
name|Override
DECL|method|getDateModified ()
specifier|public
name|long
name|getDateModified
parameter_list|()
block|{
comment|// We don't store this record directly
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|setDateCreated (long time)
specifier|public
name|void
name|setDateCreated
parameter_list|(
name|long
name|time
parameter_list|)
block|{
comment|// We don't store this record directly
block|}
annotation|@
name|Override
DECL|method|getDateCreated ()
specifier|public
name|long
name|getDateCreated
parameter_list|()
block|{
comment|// We don't store this record directly
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Membership: "
operator|+
name|getMembershipVersion
argument_list|()
operator|+
literal|" Mount Table: "
operator|+
name|getMountTableVersion
argument_list|()
return|;
block|}
block|}
end_class

end_unit

