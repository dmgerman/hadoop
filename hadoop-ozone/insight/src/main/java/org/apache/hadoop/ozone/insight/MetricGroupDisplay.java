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
name|ArrayList
import|;
end_import

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
name|ozone
operator|.
name|insight
operator|.
name|Component
operator|.
name|Type
import|;
end_import

begin_comment
comment|/**  * Definition of a group of metrics which can be displayed.  */
end_comment

begin_class
DECL|class|MetricGroupDisplay
specifier|public
class|class
name|MetricGroupDisplay
block|{
comment|/**    * List fhe included metrics.    */
DECL|field|metrics
specifier|private
name|List
argument_list|<
name|MetricDisplay
argument_list|>
name|metrics
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * Name of the component which includes the metrics (scm, om,...).    */
DECL|field|component
specifier|private
name|Component
name|component
decl_stmt|;
comment|/**    * Human readable description.    */
DECL|field|description
specifier|private
name|String
name|description
decl_stmt|;
DECL|method|MetricGroupDisplay (Component component, String description)
specifier|public
name|MetricGroupDisplay
parameter_list|(
name|Component
name|component
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|this
operator|.
name|component
operator|=
name|component
expr_stmt|;
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
block|}
DECL|method|MetricGroupDisplay (Type componentType, String metricName)
specifier|public
name|MetricGroupDisplay
parameter_list|(
name|Type
name|componentType
parameter_list|,
name|String
name|metricName
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|Component
argument_list|(
name|componentType
argument_list|)
argument_list|,
name|metricName
argument_list|)
expr_stmt|;
block|}
DECL|method|getMetrics ()
specifier|public
name|List
argument_list|<
name|MetricDisplay
argument_list|>
name|getMetrics
parameter_list|()
block|{
return|return
name|metrics
return|;
block|}
DECL|method|addMetrics (MetricDisplay item)
specifier|public
name|void
name|addMetrics
parameter_list|(
name|MetricDisplay
name|item
parameter_list|)
block|{
name|this
operator|.
name|metrics
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
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
DECL|method|getComponent ()
specifier|public
name|Component
name|getComponent
parameter_list|()
block|{
return|return
name|component
return|;
block|}
block|}
end_class

end_unit

