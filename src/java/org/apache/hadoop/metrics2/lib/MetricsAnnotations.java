begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2.lib
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|lib
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
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|MetricsSource
import|;
end_import

begin_comment
comment|/**  * Metrics annotation helpers.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|MetricsAnnotations
specifier|public
class|class
name|MetricsAnnotations
block|{
comment|/**    * Make an metrics source from an annotated object.    * @param source  the annotated object.    * @return a metrics source    */
DECL|method|makeSource (Object source)
specifier|public
specifier|static
name|MetricsSource
name|makeSource
parameter_list|(
name|Object
name|source
parameter_list|)
block|{
return|return
operator|new
name|MetricsSourceBuilder
argument_list|(
name|source
argument_list|,
name|DefaultMetricsFactory
operator|.
name|getAnnotatedMetricsFactory
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|newSourceBuilder (Object source)
specifier|public
specifier|static
name|MetricsSourceBuilder
name|newSourceBuilder
parameter_list|(
name|Object
name|source
parameter_list|)
block|{
return|return
operator|new
name|MetricsSourceBuilder
argument_list|(
name|source
argument_list|,
name|DefaultMetricsFactory
operator|.
name|getAnnotatedMetricsFactory
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

