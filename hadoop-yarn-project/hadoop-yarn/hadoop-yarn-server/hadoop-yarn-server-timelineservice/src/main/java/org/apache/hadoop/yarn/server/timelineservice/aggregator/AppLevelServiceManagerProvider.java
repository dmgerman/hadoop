begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.aggregator
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
name|timelineservice
operator|.
name|aggregator
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Provider
import|;
end_import

begin_comment
comment|/**  * A guice provider that provides a global singleton instance of  * AppLevelServiceManager.  */
end_comment

begin_class
DECL|class|AppLevelServiceManagerProvider
specifier|public
class|class
name|AppLevelServiceManagerProvider
implements|implements
name|Provider
argument_list|<
name|AppLevelServiceManager
argument_list|>
block|{
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|AppLevelServiceManager
name|get
parameter_list|()
block|{
return|return
name|AppLevelServiceManager
operator|.
name|getInstance
argument_list|()
return|;
block|}
block|}
end_class

end_unit

