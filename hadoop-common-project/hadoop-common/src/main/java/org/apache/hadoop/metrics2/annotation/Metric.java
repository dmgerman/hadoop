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
comment|/**  * Annotation interface for a single metric used to annotate a field or a method  * in the class.  */
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
name|FIELD
block|,
name|ElementType
operator|.
name|METHOD
block|}
argument_list|)
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
DECL|annotation|Metric
specifier|public
annotation_defn|@interface
name|Metric
block|{
DECL|enum|Type
specifier|public
enum|enum
name|Type
block|{
DECL|enumConstant|DEFAULT
DECL|enumConstant|COUNTER
DECL|enumConstant|GAUGE
DECL|enumConstant|TAG
name|DEFAULT
block|,
name|COUNTER
block|,
name|GAUGE
block|,
name|TAG
block|}
comment|/**    * Shorthand for optional name and description    * @return {description} or {name, description}    */
DECL|method|value ()
name|String
index|[]
name|value
argument_list|()
expr|default
block|{}
expr_stmt|;
comment|/**    * @return optional description of the metric    */
DECL|method|about ()
name|String
name|about
parameter_list|()
default|default
literal|""
function_decl|;
comment|/**    * @return optional sample name for MutableStat/Rate/Rates    */
DECL|method|sampleName ()
name|String
name|sampleName
parameter_list|()
default|default
literal|"Ops"
function_decl|;
comment|/**    * @return optional value name for MutableStat/Rate/Rates    */
DECL|method|valueName ()
name|String
name|valueName
parameter_list|()
default|default
literal|"Time"
function_decl|;
comment|/**    * @return true to create a metric snapshot even if unchanged.    */
DECL|method|always ()
DECL|field|false
name|boolean
name|always
parameter_list|()
default|default
literal|false
function_decl|;
comment|/**    * @return optional type (counter|gauge) of the metric    */
DECL|method|type ()
DECL|field|Type.DEFAULT
name|Type
name|type
parameter_list|()
default|default
name|Type
operator|.
name|DEFAULT
function_decl|;
comment|/**    * @return optional roll over interval in secs for MutableQuantiles    */
DECL|method|interval ()
name|int
name|interval
parameter_list|()
default|default
literal|10
function_decl|;
block|}
end_annotation_defn

end_unit

