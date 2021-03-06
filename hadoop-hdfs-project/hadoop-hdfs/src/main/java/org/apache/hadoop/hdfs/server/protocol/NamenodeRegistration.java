begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.protocol
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
name|protocol
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
name|common
operator|.
name|Storage
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
name|common
operator|.
name|StorageInfo
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
name|common
operator|.
name|HdfsServerConstants
operator|.
name|NamenodeRole
import|;
end_import

begin_comment
comment|/**  * Information sent by a subordinate name-node to the active name-node  * during the registration process.   */
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
DECL|class|NamenodeRegistration
specifier|public
class|class
name|NamenodeRegistration
extends|extends
name|StorageInfo
implements|implements
name|NodeRegistration
block|{
DECL|field|rpcAddress
specifier|final
name|String
name|rpcAddress
decl_stmt|;
comment|// RPC address of the node
DECL|field|httpAddress
specifier|final
name|String
name|httpAddress
decl_stmt|;
comment|// HTTP address of the node
DECL|field|role
specifier|final
name|NamenodeRole
name|role
decl_stmt|;
comment|// node role
DECL|method|NamenodeRegistration (String address, String httpAddress, StorageInfo storageInfo, NamenodeRole role)
specifier|public
name|NamenodeRegistration
parameter_list|(
name|String
name|address
parameter_list|,
name|String
name|httpAddress
parameter_list|,
name|StorageInfo
name|storageInfo
parameter_list|,
name|NamenodeRole
name|role
parameter_list|)
block|{
name|super
argument_list|(
name|storageInfo
argument_list|)
expr_stmt|;
name|this
operator|.
name|rpcAddress
operator|=
name|address
expr_stmt|;
name|this
operator|.
name|httpAddress
operator|=
name|httpAddress
expr_stmt|;
name|this
operator|.
name|role
operator|=
name|role
expr_stmt|;
block|}
annotation|@
name|Override
comment|// NodeRegistration
DECL|method|getAddress ()
specifier|public
name|String
name|getAddress
parameter_list|()
block|{
return|return
name|rpcAddress
return|;
block|}
DECL|method|getHttpAddress ()
specifier|public
name|String
name|getHttpAddress
parameter_list|()
block|{
return|return
name|httpAddress
return|;
block|}
annotation|@
name|Override
comment|// NodeRegistration
DECL|method|getRegistrationID ()
specifier|public
name|String
name|getRegistrationID
parameter_list|()
block|{
return|return
name|Storage
operator|.
name|getRegistrationID
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
comment|// NodeRegistration
DECL|method|getVersion ()
specifier|public
name|int
name|getVersion
parameter_list|()
block|{
return|return
name|super
operator|.
name|getLayoutVersion
argument_list|()
return|;
block|}
annotation|@
name|Override
comment|// NodeRegistration
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"("
operator|+
name|rpcAddress
operator|+
literal|", role="
operator|+
name|getRole
argument_list|()
operator|+
literal|")"
return|;
block|}
comment|/**    * Get name-node role.    */
DECL|method|getRole ()
specifier|public
name|NamenodeRole
name|getRole
parameter_list|()
block|{
return|return
name|role
return|;
block|}
DECL|method|isRole (NamenodeRole that)
specifier|public
name|boolean
name|isRole
parameter_list|(
name|NamenodeRole
name|that
parameter_list|)
block|{
return|return
name|role
operator|.
name|equals
argument_list|(
name|that
argument_list|)
return|;
block|}
block|}
end_class

end_unit

