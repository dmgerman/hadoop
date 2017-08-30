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
name|api
operator|.
name|records
operator|.
name|Token
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
name|AppCollectorData
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
DECL|class|ReportNewCollectorInfoRequest
specifier|public
specifier|abstract
class|class
name|ReportNewCollectorInfoRequest
block|{
DECL|method|newInstance ( List<AppCollectorData> appCollectorsList)
specifier|public
specifier|static
name|ReportNewCollectorInfoRequest
name|newInstance
parameter_list|(
name|List
argument_list|<
name|AppCollectorData
argument_list|>
name|appCollectorsList
parameter_list|)
block|{
name|ReportNewCollectorInfoRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ReportNewCollectorInfoRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setAppCollectorsList
argument_list|(
name|appCollectorsList
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
DECL|method|newInstance ( ApplicationId id, String collectorAddr, Token token)
specifier|public
specifier|static
name|ReportNewCollectorInfoRequest
name|newInstance
parameter_list|(
name|ApplicationId
name|id
parameter_list|,
name|String
name|collectorAddr
parameter_list|,
name|Token
name|token
parameter_list|)
block|{
name|ReportNewCollectorInfoRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ReportNewCollectorInfoRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setAppCollectorsList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|AppCollectorData
operator|.
name|newInstance
argument_list|(
name|id
argument_list|,
name|collectorAddr
argument_list|,
name|token
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
DECL|method|getAppCollectorsList ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|AppCollectorData
argument_list|>
name|getAppCollectorsList
parameter_list|()
function_decl|;
DECL|method|setAppCollectorsList ( List<AppCollectorData> appCollectorsList)
specifier|public
specifier|abstract
name|void
name|setAppCollectorsList
parameter_list|(
name|List
argument_list|<
name|AppCollectorData
argument_list|>
name|appCollectorsList
parameter_list|)
function_decl|;
block|}
end_class

end_unit

