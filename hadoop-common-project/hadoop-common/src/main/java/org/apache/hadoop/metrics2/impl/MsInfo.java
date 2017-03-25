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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|MoreObjects
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
name|metrics2
operator|.
name|MetricsInfo
import|;
end_import

begin_comment
comment|/**  * Metrics system related metrics info instances  */
end_comment

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|enum|MsInfo
specifier|public
enum|enum
name|MsInfo
implements|implements
name|MetricsInfo
block|{
DECL|enumConstant|NumActiveSources
name|NumActiveSources
argument_list|(
literal|"Number of active metrics sources"
argument_list|)
block|,
DECL|enumConstant|NumAllSources
name|NumAllSources
argument_list|(
literal|"Number of all registered metrics sources"
argument_list|)
block|,
DECL|enumConstant|NumActiveSinks
name|NumActiveSinks
argument_list|(
literal|"Number of active metrics sinks"
argument_list|)
block|,
DECL|enumConstant|NumAllSinks
name|NumAllSinks
argument_list|(
literal|"Number of all registered metrics sinks"
argument_list|)
block|,
DECL|enumConstant|Context
name|Context
argument_list|(
literal|"Metrics context"
argument_list|)
block|,
DECL|enumConstant|Hostname
name|Hostname
argument_list|(
literal|"Local hostname"
argument_list|)
block|,
DECL|enumConstant|SessionId
name|SessionId
argument_list|(
literal|"Session ID"
argument_list|)
block|,
DECL|enumConstant|ProcessName
name|ProcessName
argument_list|(
literal|"Process name"
argument_list|)
block|;
DECL|field|desc
specifier|private
specifier|final
name|String
name|desc
decl_stmt|;
DECL|method|MsInfo (String desc)
name|MsInfo
parameter_list|(
name|String
name|desc
parameter_list|)
block|{
name|this
operator|.
name|desc
operator|=
name|desc
expr_stmt|;
block|}
DECL|method|description ()
annotation|@
name|Override
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
name|desc
return|;
block|}
DECL|method|toString ()
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|MoreObjects
operator|.
name|toStringHelper
argument_list|(
name|this
argument_list|)
operator|.
name|add
argument_list|(
literal|"name"
argument_list|,
name|name
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"description"
argument_list|,
name|desc
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_enum

end_unit

