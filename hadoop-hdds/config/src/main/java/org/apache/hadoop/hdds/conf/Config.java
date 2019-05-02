begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.conf
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|conf
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * Mark field to be configurable from ozone-site.xml.  */
end_comment

begin_annotation_defn
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
annotation|@
name|Target
argument_list|(
name|ElementType
operator|.
name|METHOD
argument_list|)
DECL|annotation|Config
specifier|public
annotation_defn|@interface
name|Config
block|{
comment|/**    * Configuration fragment relative to the prefix defined with @ConfigGroup.    */
DECL|method|key ()
name|String
name|key
parameter_list|()
function_decl|;
comment|/**    * Default value to use if not set.    */
DECL|method|defaultValue ()
name|String
name|defaultValue
parameter_list|()
function_decl|;
comment|/**    * Custom description as a help.    */
DECL|method|description ()
name|String
name|description
parameter_list|()
function_decl|;
comment|/**    * Type of configuration. Use AUTO to decide it based on the java type.    */
DECL|method|type ()
DECL|field|ConfigType.AUTO
name|ConfigType
name|type
parameter_list|()
default|default
name|ConfigType
operator|.
name|AUTO
function_decl|;
comment|/**    * If type == TIME the unit should be defined with this attribute.    */
DECL|method|timeUnit ()
DECL|field|TimeUnit.MILLISECONDS
name|TimeUnit
name|timeUnit
parameter_list|()
default|default
name|TimeUnit
operator|.
name|MILLISECONDS
function_decl|;
DECL|method|tags ()
name|ConfigTag
index|[]
name|tags
parameter_list|()
function_decl|;
block|}
end_annotation_defn

end_unit

