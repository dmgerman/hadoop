begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|security
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|lang3
operator|.
name|builder
operator|.
name|EqualsBuilder
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|OzoneManagerProtocolProtos
operator|.
name|OMTokenProto
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|OzoneManagerProtocolProtos
operator|.
name|OMTokenProto
operator|.
name|Type
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|OzoneManagerProtocolProtos
operator|.
name|OMTokenProto
operator|.
name|Type
operator|.
name|S3TOKEN
import|;
end_import

begin_comment
comment|/**  * The token identifier for Ozone Master.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|OzoneTokenIdentifier
specifier|public
class|class
name|OzoneTokenIdentifier
extends|extends
name|AbstractDelegationTokenIdentifier
block|{
DECL|field|KIND_NAME
specifier|public
specifier|final
specifier|static
name|Text
name|KIND_NAME
init|=
operator|new
name|Text
argument_list|(
literal|"OzoneToken"
argument_list|)
decl_stmt|;
DECL|field|omCertSerialId
specifier|private
name|String
name|omCertSerialId
decl_stmt|;
DECL|field|tokenType
specifier|private
name|Type
name|tokenType
decl_stmt|;
DECL|field|awsAccessId
specifier|private
name|String
name|awsAccessId
decl_stmt|;
DECL|field|signature
specifier|private
name|String
name|signature
decl_stmt|;
DECL|field|strToSign
specifier|private
name|String
name|strToSign
decl_stmt|;
comment|/**    * Create an empty delegation token identifier.    */
DECL|method|OzoneTokenIdentifier ()
specifier|public
name|OzoneTokenIdentifier
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|tokenType
operator|=
name|Type
operator|.
name|DELEGATION_TOKEN
expr_stmt|;
block|}
comment|/**    * Create a new ozone master delegation token identifier.    *    * @param owner the effective username of the token owner    * @param renewer the username of the renewer    * @param realUser the real username of the token owner    */
DECL|method|OzoneTokenIdentifier (Text owner, Text renewer, Text realUser)
specifier|public
name|OzoneTokenIdentifier
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
name|super
argument_list|(
name|owner
argument_list|,
name|renewer
argument_list|,
name|realUser
argument_list|)
expr_stmt|;
name|this
operator|.
name|tokenType
operator|=
name|Type
operator|.
name|DELEGATION_TOKEN
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|getKind ()
specifier|public
name|Text
name|getKind
parameter_list|()
block|{
return|return
name|KIND_NAME
return|;
block|}
comment|/**    * Overrides default implementation to write using Protobuf.    *    * @param out output stream    * @throws IOException    */
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
name|OMTokenProto
operator|.
name|Builder
name|builder
init|=
name|OMTokenProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setMaxDate
argument_list|(
name|getMaxDate
argument_list|()
argument_list|)
operator|.
name|setType
argument_list|(
name|getTokenType
argument_list|()
argument_list|)
operator|.
name|setOwner
argument_list|(
name|getOwner
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|setRealUser
argument_list|(
name|getRealUser
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|setRenewer
argument_list|(
name|getRenewer
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|setIssueDate
argument_list|(
name|getIssueDate
argument_list|()
argument_list|)
operator|.
name|setMaxDate
argument_list|(
name|getMaxDate
argument_list|()
argument_list|)
operator|.
name|setSequenceNumber
argument_list|(
name|getSequenceNumber
argument_list|()
argument_list|)
operator|.
name|setMasterKeyId
argument_list|(
name|getMasterKeyId
argument_list|()
argument_list|)
decl_stmt|;
comment|// Set s3 specific fields.
if|if
condition|(
name|getTokenType
argument_list|()
operator|.
name|equals
argument_list|(
name|S3TOKEN
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setAccessKeyId
argument_list|(
name|getAwsAccessId
argument_list|()
argument_list|)
operator|.
name|setSignature
argument_list|(
name|getSignature
argument_list|()
argument_list|)
operator|.
name|setStrToSign
argument_list|(
name|getStrToSign
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|setOmCertSerialId
argument_list|(
name|getOmCertSerialId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|OMTokenProto
name|token
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|token
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Overrides default implementation to read using Protobuf.    *    * @param in input stream    * @throws IOException    */
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
name|OMTokenProto
name|token
init|=
name|OMTokenProto
operator|.
name|parseFrom
argument_list|(
operator|(
name|DataInputStream
operator|)
name|in
argument_list|)
decl_stmt|;
name|setTokenType
argument_list|(
name|token
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|setMaxDate
argument_list|(
name|token
operator|.
name|getMaxDate
argument_list|()
argument_list|)
expr_stmt|;
name|setOwner
argument_list|(
operator|new
name|Text
argument_list|(
name|token
operator|.
name|getOwner
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setRealUser
argument_list|(
operator|new
name|Text
argument_list|(
name|token
operator|.
name|getRealUser
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setRenewer
argument_list|(
operator|new
name|Text
argument_list|(
name|token
operator|.
name|getRenewer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setIssueDate
argument_list|(
name|token
operator|.
name|getIssueDate
argument_list|()
argument_list|)
expr_stmt|;
name|setMaxDate
argument_list|(
name|token
operator|.
name|getMaxDate
argument_list|()
argument_list|)
expr_stmt|;
name|setSequenceNumber
argument_list|(
name|token
operator|.
name|getSequenceNumber
argument_list|()
argument_list|)
expr_stmt|;
name|setMasterKeyId
argument_list|(
name|token
operator|.
name|getMasterKeyId
argument_list|()
argument_list|)
expr_stmt|;
name|setOmCertSerialId
argument_list|(
name|token
operator|.
name|getOmCertSerialId
argument_list|()
argument_list|)
expr_stmt|;
comment|// Set s3 specific fields.
if|if
condition|(
name|getTokenType
argument_list|()
operator|.
name|equals
argument_list|(
name|S3TOKEN
argument_list|)
condition|)
block|{
name|setAwsAccessId
argument_list|(
name|token
operator|.
name|getAccessKeyId
argument_list|()
argument_list|)
expr_stmt|;
name|setSignature
argument_list|(
name|token
operator|.
name|getSignature
argument_list|()
argument_list|)
expr_stmt|;
name|setStrToSign
argument_list|(
name|token
operator|.
name|getStrToSign
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Reads protobuf encoded input stream to construct {@link    * OzoneTokenIdentifier}.    */
DECL|method|readProtoBuf (DataInput in)
specifier|public
specifier|static
name|OzoneTokenIdentifier
name|readProtoBuf
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|OMTokenProto
name|token
init|=
name|OMTokenProto
operator|.
name|parseFrom
argument_list|(
operator|(
name|DataInputStream
operator|)
name|in
argument_list|)
decl_stmt|;
name|OzoneTokenIdentifier
name|identifier
init|=
operator|new
name|OzoneTokenIdentifier
argument_list|()
decl_stmt|;
name|identifier
operator|.
name|setTokenType
argument_list|(
name|token
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|identifier
operator|.
name|setMaxDate
argument_list|(
name|token
operator|.
name|getMaxDate
argument_list|()
argument_list|)
expr_stmt|;
comment|// Set type specific fields.
if|if
condition|(
name|token
operator|.
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
name|S3TOKEN
argument_list|)
condition|)
block|{
name|identifier
operator|.
name|setSignature
argument_list|(
name|token
operator|.
name|getSignature
argument_list|()
argument_list|)
expr_stmt|;
name|identifier
operator|.
name|setStrToSign
argument_list|(
name|token
operator|.
name|getStrToSign
argument_list|()
argument_list|)
expr_stmt|;
name|identifier
operator|.
name|setAwsAccessId
argument_list|(
name|token
operator|.
name|getAccessKeyId
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|identifier
operator|.
name|setRenewer
argument_list|(
operator|new
name|Text
argument_list|(
name|token
operator|.
name|getRenewer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|identifier
operator|.
name|setOwner
argument_list|(
operator|new
name|Text
argument_list|(
name|token
operator|.
name|getOwner
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|identifier
operator|.
name|setRealUser
argument_list|(
operator|new
name|Text
argument_list|(
name|token
operator|.
name|getRealUser
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|identifier
operator|.
name|setIssueDate
argument_list|(
name|token
operator|.
name|getIssueDate
argument_list|()
argument_list|)
expr_stmt|;
name|identifier
operator|.
name|setSequenceNumber
argument_list|(
name|token
operator|.
name|getSequenceNumber
argument_list|()
argument_list|)
expr_stmt|;
name|identifier
operator|.
name|setMasterKeyId
argument_list|(
name|token
operator|.
name|getMasterKeyId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|identifier
operator|.
name|setOmCertSerialId
argument_list|(
name|token
operator|.
name|getOmCertSerialId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|identifier
return|;
block|}
comment|/**    * Reads protobuf encoded input stream to construct {@link    * OzoneTokenIdentifier}.    */
DECL|method|readProtoBuf (byte[] identifier)
specifier|public
specifier|static
name|OzoneTokenIdentifier
name|readProtoBuf
parameter_list|(
name|byte
index|[]
name|identifier
parameter_list|)
throws|throws
name|IOException
block|{
name|DataInputStream
name|in
init|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|identifier
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|readProtoBuf
argument_list|(
name|in
argument_list|)
return|;
block|}
comment|/**    * Creates new instance.    */
DECL|method|newInstance ()
specifier|public
specifier|static
name|OzoneTokenIdentifier
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|OzoneTokenIdentifier
argument_list|()
return|;
block|}
comment|/**    * Creates new instance.    */
DECL|method|newInstance (Text owner, Text renewer, Text realUser)
specifier|public
specifier|static
name|OzoneTokenIdentifier
name|newInstance
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
return|return
operator|new
name|OzoneTokenIdentifier
argument_list|(
name|owner
argument_list|,
name|renewer
argument_list|,
name|realUser
argument_list|)
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
name|super
operator|.
name|hashCode
argument_list|()
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
operator|!
operator|(
name|obj
operator|instanceof
name|OzoneTokenIdentifier
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|OzoneTokenIdentifier
name|that
init|=
operator|(
name|OzoneTokenIdentifier
operator|)
name|obj
decl_stmt|;
return|return
operator|new
name|EqualsBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|getOmCertSerialId
argument_list|()
argument_list|,
name|that
operator|.
name|getOmCertSerialId
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|getMaxDate
argument_list|()
argument_list|,
name|that
operator|.
name|getMaxDate
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|getIssueDate
argument_list|()
argument_list|,
name|that
operator|.
name|getIssueDate
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|getMasterKeyId
argument_list|()
argument_list|,
name|that
operator|.
name|getMasterKeyId
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|getOwner
argument_list|()
argument_list|,
name|that
operator|.
name|getOwner
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|getRealUser
argument_list|()
argument_list|,
name|that
operator|.
name|getRealUser
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|getRenewer
argument_list|()
argument_list|,
name|that
operator|.
name|getRenewer
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|getKind
argument_list|()
argument_list|,
name|that
operator|.
name|getKind
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|getSequenceNumber
argument_list|()
argument_list|,
name|that
operator|.
name|getSequenceNumber
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Class to encapsulate a token's renew date and password.    */
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|TokenInfo
specifier|public
specifier|static
class|class
name|TokenInfo
block|{
DECL|field|renewDate
specifier|private
name|long
name|renewDate
decl_stmt|;
DECL|field|password
specifier|private
name|byte
index|[]
name|password
decl_stmt|;
DECL|field|trackingId
specifier|private
name|String
name|trackingId
decl_stmt|;
DECL|method|TokenInfo (long renewDate, byte[] password)
specifier|public
name|TokenInfo
parameter_list|(
name|long
name|renewDate
parameter_list|,
name|byte
index|[]
name|password
parameter_list|)
block|{
name|this
argument_list|(
name|renewDate
argument_list|,
name|password
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|TokenInfo (long renewDate, byte[] password, String trackingId)
specifier|public
name|TokenInfo
parameter_list|(
name|long
name|renewDate
parameter_list|,
name|byte
index|[]
name|password
parameter_list|,
name|String
name|trackingId
parameter_list|)
block|{
name|this
operator|.
name|renewDate
operator|=
name|renewDate
expr_stmt|;
name|this
operator|.
name|password
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|password
argument_list|,
name|password
operator|.
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|trackingId
operator|=
name|trackingId
expr_stmt|;
block|}
comment|/**      * returns renew date.      */
DECL|method|getRenewDate ()
specifier|public
name|long
name|getRenewDate
parameter_list|()
block|{
return|return
name|renewDate
return|;
block|}
comment|/**      * returns password.      */
DECL|method|getPassword ()
name|byte
index|[]
name|getPassword
parameter_list|()
block|{
return|return
name|password
return|;
block|}
comment|/**      * returns tracking id.      */
DECL|method|getTrackingId ()
specifier|public
name|String
name|getTrackingId
parameter_list|()
block|{
return|return
name|trackingId
return|;
block|}
block|}
DECL|method|getOmCertSerialId ()
specifier|public
name|String
name|getOmCertSerialId
parameter_list|()
block|{
return|return
name|omCertSerialId
return|;
block|}
DECL|method|setOmCertSerialId (String omCertSerialId)
specifier|public
name|void
name|setOmCertSerialId
parameter_list|(
name|String
name|omCertSerialId
parameter_list|)
block|{
name|this
operator|.
name|omCertSerialId
operator|=
name|omCertSerialId
expr_stmt|;
block|}
DECL|method|getTokenType ()
specifier|public
name|Type
name|getTokenType
parameter_list|()
block|{
return|return
name|tokenType
return|;
block|}
DECL|method|setTokenType (Type tokenType)
specifier|public
name|void
name|setTokenType
parameter_list|(
name|Type
name|tokenType
parameter_list|)
block|{
name|this
operator|.
name|tokenType
operator|=
name|tokenType
expr_stmt|;
block|}
DECL|method|getAwsAccessId ()
specifier|public
name|String
name|getAwsAccessId
parameter_list|()
block|{
return|return
name|awsAccessId
return|;
block|}
DECL|method|setAwsAccessId (String awsAccessId)
specifier|public
name|void
name|setAwsAccessId
parameter_list|(
name|String
name|awsAccessId
parameter_list|)
block|{
name|this
operator|.
name|awsAccessId
operator|=
name|awsAccessId
expr_stmt|;
block|}
DECL|method|getSignature ()
specifier|public
name|String
name|getSignature
parameter_list|()
block|{
return|return
name|signature
return|;
block|}
DECL|method|setSignature (String signature)
specifier|public
name|void
name|setSignature
parameter_list|(
name|String
name|signature
parameter_list|)
block|{
name|this
operator|.
name|signature
operator|=
name|signature
expr_stmt|;
block|}
DECL|method|getStrToSign ()
specifier|public
name|String
name|getStrToSign
parameter_list|()
block|{
return|return
name|strToSign
return|;
block|}
DECL|method|setStrToSign (String strToSign)
specifier|public
name|void
name|setStrToSign
parameter_list|(
name|String
name|strToSign
parameter_list|)
block|{
name|this
operator|.
name|strToSign
operator|=
name|strToSign
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
name|getKind
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" owner="
argument_list|)
operator|.
name|append
argument_list|(
name|getOwner
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", renewer="
argument_list|)
operator|.
name|append
argument_list|(
name|getRenewer
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", realUser="
argument_list|)
operator|.
name|append
argument_list|(
name|getRealUser
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", issueDate="
argument_list|)
operator|.
name|append
argument_list|(
name|getIssueDate
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", maxDate="
argument_list|)
operator|.
name|append
argument_list|(
name|getMaxDate
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", sequenceNumber="
argument_list|)
operator|.
name|append
argument_list|(
name|getSequenceNumber
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", masterKeyId="
argument_list|)
operator|.
name|append
argument_list|(
name|getMasterKeyId
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", strToSign="
argument_list|)
operator|.
name|append
argument_list|(
name|getStrToSign
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", signature="
argument_list|)
operator|.
name|append
argument_list|(
name|getSignature
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", awsAccessKeyId="
argument_list|)
operator|.
name|append
argument_list|(
name|getAwsAccessId
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

