begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|commons
operator|.
name|lang
operator|.
name|builder
operator|.
name|HashCodeBuilder
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

begin_comment
comment|/**  * Internal class similar to an {@link EncryptionZone} which also holds a  * unique id. Used to implement batched listing of encryption zones.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|EncryptionZoneWithId
specifier|public
class|class
name|EncryptionZoneWithId
extends|extends
name|EncryptionZone
block|{
DECL|field|id
specifier|final
name|long
name|id
decl_stmt|;
DECL|method|EncryptionZoneWithId (String path, String keyName, long id)
specifier|public
name|EncryptionZoneWithId
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|keyName
parameter_list|,
name|long
name|id
parameter_list|)
block|{
name|super
argument_list|(
name|path
argument_list|,
name|keyName
argument_list|)
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
DECL|method|getId ()
specifier|public
name|long
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|toEncryptionZone ()
name|EncryptionZone
name|toEncryptionZone
parameter_list|()
block|{
return|return
operator|new
name|EncryptionZone
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|getKeyName
argument_list|()
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
operator|new
name|HashCodeBuilder
argument_list|(
literal|17
argument_list|,
literal|29
argument_list|)
operator|.
name|append
argument_list|(
name|super
operator|.
name|hashCode
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|id
argument_list|)
operator|.
name|toHashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|EncryptionZoneWithId
name|that
init|=
operator|(
name|EncryptionZoneWithId
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|id
operator|!=
name|that
operator|.
name|id
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
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"EncryptionZoneWithId ["
operator|+
literal|"id="
operator|+
name|id
operator|+
literal|", "
operator|+
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|']'
return|;
block|}
block|}
end_class

end_unit

