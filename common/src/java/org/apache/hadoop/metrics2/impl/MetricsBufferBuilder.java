begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_comment
comment|/**  * Builder for the immutable metrics buffers  */
end_comment

begin_class
DECL|class|MetricsBufferBuilder
class|class
name|MetricsBufferBuilder
extends|extends
name|ArrayList
argument_list|<
name|MetricsBuffer
operator|.
name|Entry
argument_list|>
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|method|add (String name, Iterable<MetricsRecordImpl> records)
name|boolean
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|Iterable
argument_list|<
name|MetricsRecordImpl
argument_list|>
name|records
parameter_list|)
block|{
return|return
name|add
argument_list|(
operator|new
name|MetricsBuffer
operator|.
name|Entry
argument_list|(
name|name
argument_list|,
name|records
argument_list|)
argument_list|)
return|;
block|}
DECL|method|get ()
name|MetricsBuffer
name|get
parameter_list|()
block|{
return|return
operator|new
name|MetricsBuffer
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

end_unit

