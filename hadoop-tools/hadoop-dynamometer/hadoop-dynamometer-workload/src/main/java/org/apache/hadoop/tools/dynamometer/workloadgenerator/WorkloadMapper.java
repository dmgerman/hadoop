begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.dynamometer.workloadgenerator
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|dynamometer
operator|.
name|workloadgenerator
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|conf
operator|.
name|Configuration
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
name|io
operator|.
name|NullWritable
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
name|mapreduce
operator|.
name|InputFormat
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
name|mapreduce
operator|.
name|Mapper
import|;
end_import

begin_comment
comment|/**  * Represents the base class for a generic workload-generating mapper. By  * default, it will expect to use {@link VirtualInputFormat} as its  * {@link InputFormat}. Subclasses expecting a different {@link InputFormat}  * should override the {@link #getInputFormat(Configuration)} method.  */
end_comment

begin_class
DECL|class|WorkloadMapper
specifier|public
specifier|abstract
class|class
name|WorkloadMapper
parameter_list|<
name|KEYIN
parameter_list|,
name|VALUEIN
parameter_list|>
extends|extends
name|Mapper
argument_list|<
name|KEYIN
argument_list|,
name|VALUEIN
argument_list|,
name|NullWritable
argument_list|,
name|NullWritable
argument_list|>
block|{
comment|/**    * Return the input class to be used by this mapper.    */
DECL|method|getInputFormat (Configuration conf)
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|InputFormat
argument_list|>
name|getInputFormat
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|VirtualInputFormat
operator|.
name|class
return|;
block|}
comment|/**    * Get the description of the behavior of this mapper.    */
DECL|method|getDescription ()
specifier|public
specifier|abstract
name|String
name|getDescription
parameter_list|()
function_decl|;
comment|/**    * Get a list of the description of each configuration that this mapper    * accepts.    */
DECL|method|getConfigDescriptions ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|String
argument_list|>
name|getConfigDescriptions
parameter_list|()
function_decl|;
comment|/**    * Verify that the provided configuration contains all configurations required    * by this mapper.    */
DECL|method|verifyConfigurations (Configuration conf)
specifier|public
specifier|abstract
name|boolean
name|verifyConfigurations
parameter_list|(
name|Configuration
name|conf
parameter_list|)
function_decl|;
block|}
end_class

end_unit

