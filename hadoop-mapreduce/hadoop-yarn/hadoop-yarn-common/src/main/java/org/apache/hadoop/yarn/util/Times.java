begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_class
DECL|class|Times
specifier|public
class|class
name|Times
block|{
DECL|field|dateFormat
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|SimpleDateFormat
argument_list|>
name|dateFormat
init|=
operator|new
name|ThreadLocal
argument_list|<
name|SimpleDateFormat
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|SimpleDateFormat
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|SimpleDateFormat
argument_list|(
literal|"d-MMM-yyyy HH:mm:ss"
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|method|elapsed (long started, long finished)
specifier|public
specifier|static
name|long
name|elapsed
parameter_list|(
name|long
name|started
parameter_list|,
name|long
name|finished
parameter_list|)
block|{
if|if
condition|(
name|finished
operator|>
literal|0
condition|)
block|{
return|return
name|finished
operator|-
name|started
return|;
block|}
return|return
name|started
operator|>
literal|0
condition|?
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|started
else|:
literal|0
return|;
block|}
DECL|method|format (long ts)
specifier|public
specifier|static
name|String
name|format
parameter_list|(
name|long
name|ts
parameter_list|)
block|{
return|return
name|ts
operator|>
literal|0
condition|?
name|String
operator|.
name|valueOf
argument_list|(
name|dateFormat
operator|.
name|get
argument_list|()
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
name|ts
argument_list|)
argument_list|)
argument_list|)
else|:
literal|"N/A"
return|;
block|}
block|}
end_class

end_unit

