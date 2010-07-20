begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.token.delegation
package|package
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
name|DataOutput
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
name|io
operator|.
name|WritableUtils
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
name|KerberosName
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
name|token
operator|.
name|TokenIdentifier
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|,
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|AbstractDelegationTokenIdentifier
specifier|public
specifier|abstract
class|class
name|AbstractDelegationTokenIdentifier
extends|extends
name|TokenIdentifier
block|{
DECL|field|VERSION
specifier|private
specifier|static
specifier|final
name|byte
name|VERSION
init|=
literal|0
decl_stmt|;
DECL|field|owner
specifier|private
name|Text
name|owner
decl_stmt|;
DECL|field|renewer
specifier|private
name|Text
name|renewer
decl_stmt|;
DECL|field|realUser
specifier|private
name|Text
name|realUser
decl_stmt|;
DECL|field|issueDate
specifier|private
name|long
name|issueDate
decl_stmt|;
DECL|field|maxDate
specifier|private
name|long
name|maxDate
decl_stmt|;
DECL|field|sequenceNumber
specifier|private
name|int
name|sequenceNumber
decl_stmt|;
DECL|field|masterKeyId
specifier|private
name|int
name|masterKeyId
init|=
literal|0
decl_stmt|;
DECL|method|AbstractDelegationTokenIdentifier ()
specifier|public
name|AbstractDelegationTokenIdentifier
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|Text
argument_list|()
argument_list|,
operator|new
name|Text
argument_list|()
argument_list|,
operator|new
name|Text
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|AbstractDelegationTokenIdentifier (Text owner, Text renewer, Text realUser)
specifier|public
name|AbstractDelegationTokenIdentifier
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
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|owner
operator|=
operator|new
name|Text
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|owner
operator|=
name|owner
expr_stmt|;
block|}
if|if
condition|(
name|renewer
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|renewer
operator|=
operator|new
name|Text
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|KerberosName
name|renewerKrbName
init|=
operator|new
name|KerberosName
argument_list|(
name|renewer
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|this
operator|.
name|renewer
operator|=
operator|new
name|Text
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
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|realUser
operator|=
operator|new
name|Text
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|realUser
operator|=
name|realUser
expr_stmt|;
block|}
name|issueDate
operator|=
literal|0
expr_stmt|;
name|maxDate
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getKind ()
specifier|public
specifier|abstract
name|Text
name|getKind
parameter_list|()
function_decl|;
comment|/**    * Get the username encoded in the token identifier    *     * @return the username or owner    */
DECL|method|getUser ()
specifier|public
name|UserGroupInformation
name|getUser
parameter_list|()
block|{
if|if
condition|(
operator|(
name|owner
operator|==
literal|null
operator|)
operator|||
operator|(
literal|""
operator|.
name|equals
argument_list|(
name|owner
operator|.
name|toString
argument_list|()
argument_list|)
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|(
name|realUser
operator|==
literal|null
operator|)
operator|||
operator|(
literal|""
operator|.
name|equals
argument_list|(
name|realUser
operator|.
name|toString
argument_list|()
argument_list|)
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
return|return
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|owner
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
name|UserGroupInformation
name|realUgi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|realUser
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
return|return
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
return|;
block|}
block|}
DECL|method|getRenewer ()
specifier|public
name|Text
name|getRenewer
parameter_list|()
block|{
return|return
name|renewer
return|;
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
name|this
operator|.
name|issueDate
operator|=
name|issueDate
expr_stmt|;
block|}
DECL|method|getIssueDate ()
specifier|public
name|long
name|getIssueDate
parameter_list|()
block|{
return|return
name|issueDate
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
name|this
operator|.
name|maxDate
operator|=
name|maxDate
expr_stmt|;
block|}
DECL|method|getMaxDate ()
specifier|public
name|long
name|getMaxDate
parameter_list|()
block|{
return|return
name|maxDate
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
name|this
operator|.
name|sequenceNumber
operator|=
name|seqNum
expr_stmt|;
block|}
DECL|method|getSequenceNumber ()
specifier|public
name|int
name|getSequenceNumber
parameter_list|()
block|{
return|return
name|sequenceNumber
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
name|masterKeyId
operator|=
name|newId
expr_stmt|;
block|}
DECL|method|getMasterKeyId ()
specifier|public
name|int
name|getMasterKeyId
parameter_list|()
block|{
return|return
name|masterKeyId
return|;
block|}
DECL|method|isEqual (Object a, Object b)
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
comment|/** {@inheritDoc} */
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
name|AbstractDelegationTokenIdentifier
condition|)
block|{
name|AbstractDelegationTokenIdentifier
name|that
init|=
operator|(
name|AbstractDelegationTokenIdentifier
operator|)
name|obj
decl_stmt|;
return|return
name|this
operator|.
name|sequenceNumber
operator|==
name|that
operator|.
name|sequenceNumber
operator|&&
name|this
operator|.
name|issueDate
operator|==
name|that
operator|.
name|issueDate
operator|&&
name|this
operator|.
name|maxDate
operator|==
name|that
operator|.
name|maxDate
operator|&&
name|this
operator|.
name|masterKeyId
operator|==
name|that
operator|.
name|masterKeyId
operator|&&
name|isEqual
argument_list|(
name|this
operator|.
name|owner
argument_list|,
name|that
operator|.
name|owner
argument_list|)
operator|&&
name|isEqual
argument_list|(
name|this
operator|.
name|renewer
argument_list|,
name|that
operator|.
name|renewer
argument_list|)
operator|&&
name|isEqual
argument_list|(
name|this
operator|.
name|realUser
argument_list|,
name|that
operator|.
name|realUser
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/** {@inheritDoc} */
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|this
operator|.
name|sequenceNumber
return|;
block|}
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
name|byte
name|version
init|=
name|in
operator|.
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
name|version
operator|!=
name|VERSION
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown version of delegation token "
operator|+
name|version
argument_list|)
throw|;
block|}
name|owner
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|renewer
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|realUser
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|issueDate
operator|=
name|WritableUtils
operator|.
name|readVLong
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|maxDate
operator|=
name|WritableUtils
operator|.
name|readVLong
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|sequenceNumber
operator|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|masterKeyId
operator|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
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
name|out
operator|.
name|writeByte
argument_list|(
name|VERSION
argument_list|)
expr_stmt|;
name|owner
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|renewer
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|realUser
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVLong
argument_list|(
name|out
argument_list|,
name|issueDate
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVLong
argument_list|(
name|out
argument_list|,
name|maxDate
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|sequenceNumber
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|masterKeyId
argument_list|)
expr_stmt|;
block|}
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
name|owner
operator|+
literal|", renewer="
operator|+
name|renewer
operator|+
literal|", realUser="
operator|+
name|realUser
operator|+
literal|", issueDate="
operator|+
name|issueDate
operator|+
literal|", maxDate="
operator|+
name|maxDate
operator|+
literal|", sequenceNumber="
operator|+
name|sequenceNumber
operator|+
literal|", masterKeyId="
operator|+
name|masterKeyId
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

