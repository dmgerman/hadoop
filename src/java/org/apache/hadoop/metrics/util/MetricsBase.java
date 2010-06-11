begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics
operator|.
name|util
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
name|metrics
operator|.
name|MetricsRecord
import|;
end_import

begin_comment
comment|/**  *   * This is base class for all metrics  *  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|MetricsBase
specifier|public
specifier|abstract
class|class
name|MetricsBase
block|{
DECL|field|NO_DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|NO_DESCRIPTION
init|=
literal|"NoDescription"
decl_stmt|;
DECL|field|name
specifier|final
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|description
specifier|final
specifier|private
name|String
name|description
decl_stmt|;
DECL|method|MetricsBase (final String nam)
specifier|protected
name|MetricsBase
parameter_list|(
specifier|final
name|String
name|nam
parameter_list|)
block|{
name|name
operator|=
name|nam
expr_stmt|;
name|description
operator|=
name|NO_DESCRIPTION
expr_stmt|;
block|}
DECL|method|MetricsBase (final String nam, final String desc)
specifier|protected
name|MetricsBase
parameter_list|(
specifier|final
name|String
name|nam
parameter_list|,
specifier|final
name|String
name|desc
parameter_list|)
block|{
name|name
operator|=
name|nam
expr_stmt|;
name|description
operator|=
name|desc
expr_stmt|;
block|}
DECL|method|pushMetric (final MetricsRecord mr)
specifier|public
specifier|abstract
name|void
name|pushMetric
parameter_list|(
specifier|final
name|MetricsRecord
name|mr
parameter_list|)
function_decl|;
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
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
empty_stmt|;
block|}
end_class

end_unit

