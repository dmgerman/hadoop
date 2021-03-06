begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.fpga
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|resourceplugin
operator|.
name|fpga
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_comment
comment|/** A class that represents an FPGA card. */
end_comment

begin_class
DECL|class|FpgaDevice
specifier|public
class|class
name|FpgaDevice
implements|implements
name|Serializable
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|4678487141824092751L
decl_stmt|;
DECL|field|type
specifier|private
specifier|final
name|String
name|type
decl_stmt|;
DECL|field|major
specifier|private
specifier|final
name|int
name|major
decl_stmt|;
DECL|field|minor
specifier|private
specifier|final
name|int
name|minor
decl_stmt|;
comment|// the alias device name. Intel use acl number acl0 to acl31
DECL|field|aliasDevName
specifier|private
specifier|final
name|String
name|aliasDevName
decl_stmt|;
comment|// IP file identifier. matrix multiplication for instance (mutable)
DECL|field|IPID
specifier|private
name|String
name|IPID
decl_stmt|;
comment|// SHA-256 hash of the uploaded aocx file (mutable)
DECL|field|aocxHash
specifier|private
name|String
name|aocxHash
decl_stmt|;
comment|// cached hash value
DECL|field|hashCode
specifier|private
name|Integer
name|hashCode
decl_stmt|;
DECL|method|getType ()
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|getMajor ()
specifier|public
name|int
name|getMajor
parameter_list|()
block|{
return|return
name|major
return|;
block|}
DECL|method|getMinor ()
specifier|public
name|int
name|getMinor
parameter_list|()
block|{
return|return
name|minor
return|;
block|}
DECL|method|getIPID ()
specifier|public
name|String
name|getIPID
parameter_list|()
block|{
return|return
name|IPID
return|;
block|}
DECL|method|getAocxHash ()
specifier|public
name|String
name|getAocxHash
parameter_list|()
block|{
return|return
name|aocxHash
return|;
block|}
DECL|method|setAocxHash (String hash)
specifier|public
name|void
name|setAocxHash
parameter_list|(
name|String
name|hash
parameter_list|)
block|{
name|this
operator|.
name|aocxHash
operator|=
name|hash
expr_stmt|;
block|}
DECL|method|setIPID (String IPID)
specifier|public
name|void
name|setIPID
parameter_list|(
name|String
name|IPID
parameter_list|)
block|{
name|this
operator|.
name|IPID
operator|=
name|IPID
expr_stmt|;
block|}
DECL|method|getAliasDevName ()
specifier|public
name|String
name|getAliasDevName
parameter_list|()
block|{
return|return
name|aliasDevName
return|;
block|}
DECL|method|FpgaDevice (String type, int major, int minor, String aliasDevName)
specifier|public
name|FpgaDevice
parameter_list|(
name|String
name|type
parameter_list|,
name|int
name|major
parameter_list|,
name|int
name|minor
parameter_list|,
name|String
name|aliasDevName
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|type
argument_list|,
literal|"type must not be null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|major
operator|=
name|major
expr_stmt|;
name|this
operator|.
name|minor
operator|=
name|minor
expr_stmt|;
name|this
operator|.
name|aliasDevName
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|aliasDevName
argument_list|,
literal|"aliasDevName must not be null"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|FpgaDevice
name|other
init|=
operator|(
name|FpgaDevice
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|aliasDevName
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|aliasDevName
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|aliasDevName
operator|.
name|equals
argument_list|(
name|other
operator|.
name|aliasDevName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|major
operator|!=
name|other
operator|.
name|major
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|minor
operator|!=
name|other
operator|.
name|minor
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|type
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|type
operator|.
name|equals
argument_list|(
name|other
operator|.
name|type
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
if|if
condition|(
name|hashCode
operator|==
literal|null
condition|)
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|major
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|type
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|minor
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|aliasDevName
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|hashCode
operator|=
name|result
expr_stmt|;
block|}
return|return
name|hashCode
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
literal|"FPGA Device:(Type: "
operator|+
name|this
operator|.
name|type
operator|+
literal|", Major: "
operator|+
name|this
operator|.
name|major
operator|+
literal|", Minor: "
operator|+
name|this
operator|.
name|minor
operator|+
literal|", IPID: "
operator|+
name|this
operator|.
name|IPID
operator|+
literal|", Hash: "
operator|+
name|this
operator|.
name|aocxHash
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

