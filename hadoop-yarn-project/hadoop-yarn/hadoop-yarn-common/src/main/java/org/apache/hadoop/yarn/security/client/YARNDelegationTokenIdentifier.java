begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.security.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|security
operator|.
name|client
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|Text
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
name|security
operator|.
name|HadoopKerberosName
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
name|security
operator|.
name|UserGroupInformation
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
name|security
operator|.
name|UserGroupInformation
operator|.
name|AuthenticationMethod
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|AbstractDelegationTokenIdentifier
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
name|proto
operator|.
name|YarnSecurityTokenProtos
operator|.
name|YARNDelegationTokenIdentifierProto
import|;
end_import

begin_class
DECL|class|YARNDelegationTokenIdentifier
specifier|public
specifier|abstract
class|class
name|YARNDelegationTokenIdentifier
extends|extends
name|AbstractDelegationTokenIdentifier
block|{
DECL|field|builder
name|YARNDelegationTokenIdentifierProto
operator|.
name|Builder
name|builder
init|=
name|YARNDelegationTokenIdentifierProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
DECL|method|YARNDelegationTokenIdentifier ()
specifier|public
name|YARNDelegationTokenIdentifier
parameter_list|()
block|{}
DECL|method|YARNDelegationTokenIdentifier (Text owner, Text renewer, Text realUser)
specifier|public
name|YARNDelegationTokenIdentifier
parameter_list|(
name|Text
name|owner
parameter_list|,
name|Text
name|renewer
parameter_list|,
name|Text
name|realUser
parameter_list|)
block|{
if|if
condition|(
name|owner
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setOwner
argument_list|(
name|owner
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|renewer
operator|!=
literal|null
condition|)
block|{
name|HadoopKerberosName
name|renewerKrbName
init|=
operator|new
name|HadoopKerberosName
argument_list|(
name|renewer
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|builder
operator|.
name|setRenewer
argument_list|(
name|renewerKrbName
operator|.
name|getShortName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|realUser
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setRealUser
argument_list|(
name|realUser
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Get the username encoded in the token identifier    *     * @return the username or owner    */
annotation|@
name|Override
DECL|method|getUser ()
specifier|public
name|UserGroupInformation
name|getUser
parameter_list|()
block|{
name|String
name|owner
init|=
name|getOwner
argument_list|()
operator|==
literal|null
condition|?
literal|null
else|:
name|getOwner
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|realUser
init|=
name|getRealUser
argument_list|()
operator|==
literal|null
condition|?
literal|null
else|:
name|getRealUser
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|owner
operator|==
literal|null
operator|)
operator|||
operator|(
name|owner
operator|.
name|toString
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|UserGroupInformation
name|realUgi
decl_stmt|;
specifier|final
name|UserGroupInformation
name|ugi
decl_stmt|;
if|if
condition|(
operator|(
name|realUser
operator|==
literal|null
operator|)
operator|||
operator|(
name|realUser
operator|.
name|toString
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|)
operator|||
name|realUser
operator|.
name|equals
argument_list|(
name|owner
argument_list|)
condition|)
block|{
name|ugi
operator|=
name|realUgi
operator|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|owner
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|realUgi
operator|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|realUser
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ugi
operator|=
name|UserGroupInformation
operator|.
name|createProxyUser
argument_list|(
name|owner
operator|.
name|toString
argument_list|()
argument_list|,
name|realUgi
argument_list|)
expr_stmt|;
block|}
name|realUgi
operator|.
name|setAuthenticationMethod
argument_list|(
name|AuthenticationMethod
operator|.
name|TOKEN
argument_list|)
expr_stmt|;
return|return
name|ugi
return|;
block|}
DECL|method|getOwner ()
specifier|public
name|Text
name|getOwner
parameter_list|()
block|{
name|String
name|owner
init|=
name|builder
operator|.
name|getOwner
argument_list|()
decl_stmt|;
if|if
condition|(
name|owner
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
operator|new
name|Text
argument_list|(
name|owner
argument_list|)
return|;
block|}
block|}
DECL|method|getRenewer ()
specifier|public
name|Text
name|getRenewer
parameter_list|()
block|{
name|String
name|renewer
init|=
name|builder
operator|.
name|getRenewer
argument_list|()
decl_stmt|;
if|if
condition|(
name|renewer
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
operator|new
name|Text
argument_list|(
name|renewer
argument_list|)
return|;
block|}
block|}
DECL|method|getRealUser ()
specifier|public
name|Text
name|getRealUser
parameter_list|()
block|{
name|String
name|realUser
init|=
name|builder
operator|.
name|getRealUser
argument_list|()
decl_stmt|;
if|if
condition|(
name|realUser
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
operator|new
name|Text
argument_list|(
name|realUser
argument_list|)
return|;
block|}
block|}
DECL|method|setIssueDate (long issueDate)
specifier|public
name|void
name|setIssueDate
parameter_list|(
name|long
name|issueDate
parameter_list|)
block|{
name|builder
operator|.
name|setIssueDate
argument_list|(
name|issueDate
argument_list|)
expr_stmt|;
block|}
DECL|method|getIssueDate ()
specifier|public
name|long
name|getIssueDate
parameter_list|()
block|{
return|return
name|builder
operator|.
name|getIssueDate
argument_list|()
return|;
block|}
DECL|method|setRenewDate (long renewDate)
specifier|public
name|void
name|setRenewDate
parameter_list|(
name|long
name|renewDate
parameter_list|)
block|{
name|builder
operator|.
name|setRenewDate
argument_list|(
name|renewDate
argument_list|)
expr_stmt|;
block|}
DECL|method|getRenewDate ()
specifier|public
name|long
name|getRenewDate
parameter_list|()
block|{
return|return
name|builder
operator|.
name|getRenewDate
argument_list|()
return|;
block|}
DECL|method|setMaxDate (long maxDate)
specifier|public
name|void
name|setMaxDate
parameter_list|(
name|long
name|maxDate
parameter_list|)
block|{
name|builder
operator|.
name|setMaxDate
argument_list|(
name|maxDate
argument_list|)
expr_stmt|;
block|}
DECL|method|getMaxDate ()
specifier|public
name|long
name|getMaxDate
parameter_list|()
block|{
return|return
name|builder
operator|.
name|getMaxDate
argument_list|()
return|;
block|}
DECL|method|setSequenceNumber (int seqNum)
specifier|public
name|void
name|setSequenceNumber
parameter_list|(
name|int
name|seqNum
parameter_list|)
block|{
name|builder
operator|.
name|setSequenceNumber
argument_list|(
name|seqNum
argument_list|)
expr_stmt|;
block|}
DECL|method|getSequenceNumber ()
specifier|public
name|int
name|getSequenceNumber
parameter_list|()
block|{
return|return
name|builder
operator|.
name|getSequenceNumber
argument_list|()
return|;
block|}
DECL|method|setMasterKeyId (int newId)
specifier|public
name|void
name|setMasterKeyId
parameter_list|(
name|int
name|newId
parameter_list|)
block|{
name|builder
operator|.
name|setMasterKeyId
argument_list|(
name|newId
argument_list|)
expr_stmt|;
block|}
DECL|method|getMasterKeyId ()
specifier|public
name|int
name|getMasterKeyId
parameter_list|()
block|{
return|return
name|builder
operator|.
name|getMasterKeyId
argument_list|()
return|;
block|}
DECL|method|isEqual (Object a, Object b)
specifier|protected
specifier|static
name|boolean
name|isEqual
parameter_list|(
name|Object
name|a
parameter_list|,
name|Object
name|b
parameter_list|)
block|{
return|return
name|a
operator|==
literal|null
condition|?
name|b
operator|==
literal|null
else|:
name|a
operator|.
name|equals
argument_list|(
name|b
argument_list|)
return|;
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
name|obj
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|instanceof
name|YARNDelegationTokenIdentifier
condition|)
block|{
name|YARNDelegationTokenIdentifier
name|that
init|=
operator|(
name|YARNDelegationTokenIdentifier
operator|)
name|obj
decl_stmt|;
return|return
name|this
operator|.
name|getSequenceNumber
argument_list|()
operator|==
name|that
operator|.
name|getSequenceNumber
argument_list|()
operator|&&
name|this
operator|.
name|getIssueDate
argument_list|()
operator|==
name|that
operator|.
name|getIssueDate
argument_list|()
operator|&&
name|this
operator|.
name|getMaxDate
argument_list|()
operator|==
name|that
operator|.
name|getMaxDate
argument_list|()
operator|&&
name|this
operator|.
name|getMasterKeyId
argument_list|()
operator|==
name|that
operator|.
name|getMasterKeyId
argument_list|()
operator|&&
name|isEqual
argument_list|(
name|this
operator|.
name|getOwner
argument_list|()
argument_list|,
name|that
operator|.
name|getOwner
argument_list|()
argument_list|)
operator|&&
name|isEqual
argument_list|(
name|this
operator|.
name|getRenewer
argument_list|()
argument_list|,
name|that
operator|.
name|getRenewer
argument_list|()
argument_list|)
operator|&&
name|isEqual
argument_list|(
name|this
operator|.
name|getRealUser
argument_list|()
argument_list|,
name|that
operator|.
name|getRealUser
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|false
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
return|return
name|this
operator|.
name|getSequenceNumber
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|mergeFrom
argument_list|(
operator|(
name|DataInputStream
operator|)
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|build
argument_list|()
operator|.
name|writeTo
argument_list|(
operator|(
name|DataOutputStream
operator|)
name|out
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"owner="
operator|+
name|getOwner
argument_list|()
operator|+
literal|", renewer="
operator|+
name|getRenewer
argument_list|()
operator|+
literal|", realUser="
operator|+
name|getRealUser
argument_list|()
operator|+
literal|", issueDate="
operator|+
name|getIssueDate
argument_list|()
operator|+
literal|", maxDate="
operator|+
name|getMaxDate
argument_list|()
operator|+
literal|", sequenceNumber="
operator|+
name|getSequenceNumber
argument_list|()
operator|+
literal|", masterKeyId="
operator|+
name|getMasterKeyId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

