begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2.annotation
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|annotation
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
name|*
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
comment|/**  * Annotation interface for a group of metrics  */
end_comment

begin_annotation_defn
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
annotation|@
name|Documented
annotation|@
name|Target
argument_list|(
block|{
name|ElementType
operator|.
name|TYPE
block|}
argument_list|)
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
DECL|annotation|Metrics
specifier|public
annotation_defn|@interface
name|Metrics
block|{
comment|/**    * @return the (record) name of the metrics    */
DECL|method|name ()
name|String
name|name
parameter_list|()
default|default
literal|""
function_decl|;
comment|/**    * @return the optional description of metrics    */
DECL|method|about ()
name|String
name|about
parameter_list|()
default|default
literal|""
function_decl|;
comment|/**    * @return the context name for a group of metrics    */
DECL|method|context ()
name|String
name|context
parameter_list|()
function_decl|;
block|}
end_annotation_defn

end_unit

