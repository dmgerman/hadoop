begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
name|EnumSet
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
name|HadoopIllegalArgumentException
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

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|enum|XAttrSetFlag
specifier|public
enum|enum
name|XAttrSetFlag
block|{
comment|/**    * Create a new xattr.    * If the xattr exists already, exception will be thrown.    */
DECL|enumConstant|CREATE
name|CREATE
argument_list|(
operator|(
name|short
operator|)
literal|0x01
argument_list|)
block|,
comment|/**    * Replace a existing xattr.    * If the xattr does not exist, exception will be thrown.    */
DECL|enumConstant|REPLACE
name|REPLACE
argument_list|(
operator|(
name|short
operator|)
literal|0x02
argument_list|)
block|;
DECL|field|flag
specifier|private
specifier|final
name|short
name|flag
decl_stmt|;
DECL|method|XAttrSetFlag (short flag)
specifier|private
name|XAttrSetFlag
parameter_list|(
name|short
name|flag
parameter_list|)
block|{
name|this
operator|.
name|flag
operator|=
name|flag
expr_stmt|;
block|}
DECL|method|getFlag ()
name|short
name|getFlag
parameter_list|()
block|{
return|return
name|flag
return|;
block|}
DECL|method|validate (String xAttrName, boolean xAttrExists, EnumSet<XAttrSetFlag> flag)
specifier|public
specifier|static
name|void
name|validate
parameter_list|(
name|String
name|xAttrName
parameter_list|,
name|boolean
name|xAttrExists
parameter_list|,
name|EnumSet
argument_list|<
name|XAttrSetFlag
argument_list|>
name|flag
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|flag
operator|==
literal|null
operator|||
name|flag
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"A flag must be specified."
argument_list|)
throw|;
block|}
if|if
condition|(
name|xAttrExists
condition|)
block|{
if|if
condition|(
operator|!
name|flag
operator|.
name|contains
argument_list|(
name|REPLACE
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"XAttr: "
operator|+
name|xAttrName
operator|+
literal|" already exists. The REPLACE flag must be specified."
argument_list|)
throw|;
block|}
block|}
else|else
block|{
if|if
condition|(
operator|!
name|flag
operator|.
name|contains
argument_list|(
name|CREATE
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"XAttr: "
operator|+
name|xAttrName
operator|+
literal|" does not exist. The CREATE flag must be specified."
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_enum

end_unit

