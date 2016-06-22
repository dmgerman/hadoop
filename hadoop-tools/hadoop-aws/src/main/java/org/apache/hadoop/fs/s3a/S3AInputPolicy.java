begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|fs
operator|.
name|s3a
operator|.
name|Constants
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Filesystem input policy.  */
end_comment

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|enum|S3AInputPolicy
specifier|public
enum|enum
name|S3AInputPolicy
block|{
DECL|enumConstant|Normal
name|Normal
parameter_list|(
name|INPUT_FADV_NORMAL
parameter_list|)
operator|,
DECL|enumConstant|Sequential
constructor|Sequential(INPUT_FADV_SEQUENTIAL
block|)
enum|,
DECL|enumConstant|Random
name|Random
argument_list|(
name|INPUT_FADV_RANDOM
argument_list|)
enum|;
end_enum

begin_decl_stmt
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|S3AInputPolicy
operator|.
name|class
argument_list|)
decl_stmt|;
end_decl_stmt

begin_decl_stmt
DECL|field|policy
specifier|private
specifier|final
name|String
name|policy
decl_stmt|;
end_decl_stmt

begin_expr_stmt
DECL|method|S3AInputPolicy (String policy)
name|S3AInputPolicy
argument_list|(
name|String
name|policy
argument_list|)
block|{
name|this
operator|.
name|policy
operator|=
name|policy
block|;   }
expr|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
argument_list|()
block|{
return|return
name|policy
return|;
block|}
end_expr_stmt

begin_comment
comment|/**    * Choose an FS access policy.    * Always returns something,    * primarily by downgrading to "normal" if there is no other match.    * @param name strategy name from a configuration option, etc.    * @return the chosen strategy    */
end_comment

begin_function
DECL|method|getPolicy (String name)
specifier|public
specifier|static
name|S3AInputPolicy
name|getPolicy
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|String
name|trimmed
init|=
name|name
operator|.
name|trim
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|trimmed
condition|)
block|{
case|case
name|INPUT_FADV_NORMAL
case|:
return|return
name|Normal
return|;
case|case
name|INPUT_FADV_RANDOM
case|:
return|return
name|Random
return|;
case|case
name|INPUT_FADV_SEQUENTIAL
case|:
return|return
name|Sequential
return|;
default|default:
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unrecognized "
operator|+
name|INPUT_FADVISE
operator|+
literal|" value: \"{}\""
argument_list|,
name|trimmed
argument_list|)
expr_stmt|;
return|return
name|Normal
return|;
block|}
block|}
end_function

unit|}
end_unit

