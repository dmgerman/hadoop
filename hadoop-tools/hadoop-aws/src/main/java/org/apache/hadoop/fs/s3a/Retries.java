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
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Documented
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Retention
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|RetentionPolicy
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

begin_comment
comment|/**  * Declaration of retry policy for documentation only.  * This is purely for visibility in source and is currently package-scoped.  * Compare with {@link org.apache.hadoop.io.retry.AtMostOnce}  * and {@link org.apache.hadoop.io.retry.Idempotent}; these are real  * markers used by Hadoop RPC.  */
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
DECL|class|Retries
specifier|public
class|class
name|Retries
block|{
comment|/**    * No retry, exceptions are translated.    */
annotation|@
name|Documented
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|SOURCE
argument_list|)
DECL|annotation|OnceTranslated
specifier|public
annotation_defn|@interface
name|OnceTranslated
block|{
DECL|method|value ()
name|String
name|value
parameter_list|()
default|default
literal|""
function_decl|;
block|}
comment|/**    * No retry, exceptions are not translated.    */
annotation|@
name|Documented
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|SOURCE
argument_list|)
DECL|annotation|OnceRaw
specifier|public
annotation_defn|@interface
name|OnceRaw
block|{
DECL|method|value ()
name|String
name|value
parameter_list|()
default|default
literal|""
function_decl|;
block|}
comment|/**    * No retry, expect a bit of both.    */
annotation|@
name|Documented
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|SOURCE
argument_list|)
DECL|annotation|OnceMixed
specifier|public
annotation_defn|@interface
name|OnceMixed
block|{
DECL|method|value ()
name|String
name|value
parameter_list|()
default|default
literal|""
function_decl|;
block|}
comment|/**    * Retried, exceptions are translated.    */
annotation|@
name|Documented
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|SOURCE
argument_list|)
DECL|annotation|RetryTranslated
specifier|public
annotation_defn|@interface
name|RetryTranslated
block|{
DECL|method|value ()
name|String
name|value
parameter_list|()
default|default
literal|""
function_decl|;
block|}
comment|/**    * Retried, no translation.    */
annotation|@
name|Documented
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|SOURCE
argument_list|)
DECL|annotation|RetryRaw
specifier|public
annotation_defn|@interface
name|RetryRaw
block|{
DECL|method|value ()
name|String
name|value
parameter_list|()
default|default
literal|""
function_decl|;
block|}
comment|/**    * Retried, mixed translation.    */
annotation|@
name|Documented
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|SOURCE
argument_list|)
DECL|annotation|RetryMixed
specifier|public
annotation_defn|@interface
name|RetryMixed
block|{
DECL|method|value ()
name|String
name|value
parameter_list|()
default|default
literal|""
function_decl|;
block|}
block|}
end_class

end_unit

