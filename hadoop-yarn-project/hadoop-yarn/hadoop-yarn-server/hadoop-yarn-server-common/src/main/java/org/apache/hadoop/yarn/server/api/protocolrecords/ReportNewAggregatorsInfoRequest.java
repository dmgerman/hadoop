begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.api.protocolrecords
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|api
operator|.
name|protocolrecords
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
name|java
operator|.
name|util
operator|.
name|Arrays
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
operator|.
name|Private
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|yarn
operator|.
name|server
operator|.
name|api
operator|.
name|records
operator|.
name|AppAggregatorsMap
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
name|yarn
operator|.
name|util
operator|.
name|Records
import|;
end_import

begin_class
annotation|@
name|Private
DECL|class|ReportNewAggregatorsInfoRequest
specifier|public
specifier|abstract
class|class
name|ReportNewAggregatorsInfoRequest
block|{
DECL|method|newInstance ( List<AppAggregatorsMap> appAggregatorsList)
specifier|public
specifier|static
name|ReportNewAggregatorsInfoRequest
name|newInstance
parameter_list|(
name|List
argument_list|<
name|AppAggregatorsMap
argument_list|>
name|appAggregatorsList
parameter_list|)
block|{
name|ReportNewAggregatorsInfoRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ReportNewAggregatorsInfoRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setAppAggregatorsList
argument_list|(
name|appAggregatorsList
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
DECL|method|newInstance ( ApplicationId id, String aggregatorAddr)
specifier|public
specifier|static
name|ReportNewAggregatorsInfoRequest
name|newInstance
parameter_list|(
name|ApplicationId
name|id
parameter_list|,
name|String
name|aggregatorAddr
parameter_list|)
block|{
name|ReportNewAggregatorsInfoRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ReportNewAggregatorsInfoRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setAppAggregatorsList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|AppAggregatorsMap
operator|.
name|newInstance
argument_list|(
name|id
argument_list|,
name|aggregatorAddr
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
DECL|method|getAppAggregatorsList ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|AppAggregatorsMap
argument_list|>
name|getAppAggregatorsList
parameter_list|()
function_decl|;
DECL|method|setAppAggregatorsList ( List<AppAggregatorsMap> appAggregatorsList)
specifier|public
specifier|abstract
name|void
name|setAppAggregatorsList
parameter_list|(
name|List
argument_list|<
name|AppAggregatorsMap
argument_list|>
name|appAggregatorsList
parameter_list|)
function_decl|;
block|}
end_class

end_unit

