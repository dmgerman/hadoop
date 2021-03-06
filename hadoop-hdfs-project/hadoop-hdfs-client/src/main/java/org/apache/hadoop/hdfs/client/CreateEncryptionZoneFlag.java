begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|client
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

begin_comment
comment|/**  * CreateEncryptionZoneFlag is used in  * {@link HdfsAdmin#createEncryptionZone(Path, String, EnumSet)} to indicate  * what should be done when creating an encryption zone.  *  * Use CreateEncryptionZoneFlag as follows:  *<ol>  *<li>PROVISION_TRASH - provision a trash directory for the encryption zone  *   to support soft delete.</li>  *</ol>  */
end_comment

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|enum|CreateEncryptionZoneFlag
specifier|public
enum|enum
name|CreateEncryptionZoneFlag
block|{
comment|/**    * Do not provision a trash directory in the encryption zone.    *    * @see CreateEncryptionZoneFlag#NO_TRASH    */
DECL|enumConstant|NO_TRASH
name|NO_TRASH
argument_list|(
operator|(
name|short
operator|)
literal|0x00
argument_list|)
block|,
comment|/**    * Provision a trash directory .Trash/ in the    * encryption zone.    *    * @see CreateEncryptionZoneFlag#PROVISION_TRASH    */
DECL|enumConstant|PROVISION_TRASH
name|PROVISION_TRASH
argument_list|(
operator|(
name|short
operator|)
literal|0x01
argument_list|)
block|;
DECL|field|mode
specifier|private
specifier|final
name|short
name|mode
decl_stmt|;
DECL|method|CreateEncryptionZoneFlag (short mode)
name|CreateEncryptionZoneFlag
parameter_list|(
name|short
name|mode
parameter_list|)
block|{
name|this
operator|.
name|mode
operator|=
name|mode
expr_stmt|;
block|}
DECL|method|valueOf (short mode)
specifier|public
specifier|static
name|CreateEncryptionZoneFlag
name|valueOf
parameter_list|(
name|short
name|mode
parameter_list|)
block|{
for|for
control|(
name|CreateEncryptionZoneFlag
name|flag
range|:
name|CreateEncryptionZoneFlag
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|flag
operator|.
name|getMode
argument_list|()
operator|==
name|mode
condition|)
block|{
return|return
name|flag
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|getMode ()
specifier|public
name|short
name|getMode
parameter_list|()
block|{
return|return
name|mode
return|;
block|}
block|}
end_enum

end_unit

