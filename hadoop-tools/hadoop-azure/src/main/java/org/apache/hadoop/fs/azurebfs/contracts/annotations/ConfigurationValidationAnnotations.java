begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.contracts.annotations
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|contracts
operator|.
name|annotations
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
name|ElementType
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
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Target
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
comment|/**  * Definitions of Annotations for all types of the validators.  */
end_comment

begin_class
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|ConfigurationValidationAnnotations
specifier|public
class|class
name|ConfigurationValidationAnnotations
block|{
comment|/**    * Describes the requirements when validating the annotated int field.    */
annotation|@
name|Target
argument_list|(
block|{
name|ElementType
operator|.
name|FIELD
block|}
argument_list|)
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
DECL|annotation|IntegerConfigurationValidatorAnnotation
specifier|public
annotation_defn|@interface
name|IntegerConfigurationValidatorAnnotation
block|{
DECL|method|ConfigurationKey ()
name|String
name|ConfigurationKey
parameter_list|()
function_decl|;
DECL|method|MaxValue ()
DECL|field|Integer.MAX_VALUE
name|int
name|MaxValue
parameter_list|()
default|default
name|Integer
operator|.
name|MAX_VALUE
function_decl|;
DECL|method|MinValue ()
DECL|field|Integer.MIN_VALUE
name|int
name|MinValue
parameter_list|()
default|default
name|Integer
operator|.
name|MIN_VALUE
function_decl|;
DECL|method|DefaultValue ()
name|int
name|DefaultValue
parameter_list|()
function_decl|;
DECL|method|ThrowIfInvalid ()
DECL|field|false
name|boolean
name|ThrowIfInvalid
parameter_list|()
default|default
literal|false
function_decl|;
block|}
comment|/**    * Describes the requirements when validating the annotated long field.    */
annotation|@
name|Target
argument_list|(
block|{
name|ElementType
operator|.
name|FIELD
block|}
argument_list|)
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
DECL|annotation|LongConfigurationValidatorAnnotation
specifier|public
annotation_defn|@interface
name|LongConfigurationValidatorAnnotation
block|{
DECL|method|ConfigurationKey ()
name|String
name|ConfigurationKey
parameter_list|()
function_decl|;
DECL|method|MaxValue ()
DECL|field|Long.MAX_VALUE
name|long
name|MaxValue
parameter_list|()
default|default
name|Long
operator|.
name|MAX_VALUE
function_decl|;
DECL|method|MinValue ()
DECL|field|Long.MIN_VALUE
name|long
name|MinValue
parameter_list|()
default|default
name|Long
operator|.
name|MIN_VALUE
function_decl|;
DECL|method|DefaultValue ()
name|long
name|DefaultValue
parameter_list|()
function_decl|;
DECL|method|ThrowIfInvalid ()
DECL|field|false
name|boolean
name|ThrowIfInvalid
parameter_list|()
default|default
literal|false
function_decl|;
block|}
comment|/**    * Describes the requirements when validating the annotated String field.    */
annotation|@
name|Target
argument_list|(
block|{
name|ElementType
operator|.
name|FIELD
block|}
argument_list|)
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
DECL|annotation|StringConfigurationValidatorAnnotation
specifier|public
annotation_defn|@interface
name|StringConfigurationValidatorAnnotation
block|{
DECL|method|ConfigurationKey ()
name|String
name|ConfigurationKey
parameter_list|()
function_decl|;
DECL|method|DefaultValue ()
name|String
name|DefaultValue
parameter_list|()
function_decl|;
DECL|method|ThrowIfInvalid ()
DECL|field|false
name|boolean
name|ThrowIfInvalid
parameter_list|()
default|default
literal|false
function_decl|;
block|}
comment|/**    * Describes the requirements when validating the annotated String field.    */
annotation|@
name|Target
argument_list|(
block|{
name|ElementType
operator|.
name|FIELD
block|}
argument_list|)
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
DECL|annotation|Base64StringConfigurationValidatorAnnotation
specifier|public
annotation_defn|@interface
name|Base64StringConfigurationValidatorAnnotation
block|{
DECL|method|ConfigurationKey ()
name|String
name|ConfigurationKey
parameter_list|()
function_decl|;
DECL|method|DefaultValue ()
name|String
name|DefaultValue
parameter_list|()
function_decl|;
DECL|method|ThrowIfInvalid ()
DECL|field|false
name|boolean
name|ThrowIfInvalid
parameter_list|()
default|default
literal|false
function_decl|;
block|}
comment|/**    * Describes the requirements when validating the annotated boolean field.    */
annotation|@
name|Target
argument_list|(
block|{
name|ElementType
operator|.
name|FIELD
block|}
argument_list|)
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
DECL|annotation|BooleanConfigurationValidatorAnnotation
specifier|public
annotation_defn|@interface
name|BooleanConfigurationValidatorAnnotation
block|{
DECL|method|ConfigurationKey ()
name|String
name|ConfigurationKey
parameter_list|()
function_decl|;
DECL|method|DefaultValue ()
name|boolean
name|DefaultValue
parameter_list|()
function_decl|;
DECL|method|ThrowIfInvalid ()
DECL|field|false
name|boolean
name|ThrowIfInvalid
parameter_list|()
default|default
literal|false
function_decl|;
block|}
block|}
end_class

end_unit

