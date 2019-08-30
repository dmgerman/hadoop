begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.insight
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|insight
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Definition of one displayable hadoop metrics.  */
end_comment

begin_class
DECL|class|MetricDisplay
specifier|public
class|class
name|MetricDisplay
block|{
comment|/**    * Prometheus metrics name.    */
DECL|field|id
specifier|private
name|String
name|id
decl_stmt|;
comment|/**    * Human readable definition of the metrhics.    */
DECL|field|description
specifier|private
name|String
name|description
decl_stmt|;
comment|/**    * Prometheus metrics tag to filter out the right metrics.    */
DECL|field|filter
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|filter
decl_stmt|;
DECL|method|MetricDisplay (String description, String id)
specifier|public
name|MetricDisplay
parameter_list|(
name|String
name|description
parameter_list|,
name|String
name|id
parameter_list|)
block|{
name|this
argument_list|(
name|description
argument_list|,
name|id
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|MetricDisplay (String description, String id, Map<String, String> filter)
specifier|public
name|MetricDisplay
parameter_list|(
name|String
name|description
parameter_list|,
name|String
name|id
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|filter
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
block|}
DECL|method|getId ()
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|getDescription ()
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|description
return|;
block|}
DECL|method|getFilter ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getFilter
parameter_list|()
block|{
return|return
name|filter
return|;
block|}
DECL|method|checkLine (String line)
specifier|public
name|boolean
name|checkLine
parameter_list|(
name|String
name|line
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

