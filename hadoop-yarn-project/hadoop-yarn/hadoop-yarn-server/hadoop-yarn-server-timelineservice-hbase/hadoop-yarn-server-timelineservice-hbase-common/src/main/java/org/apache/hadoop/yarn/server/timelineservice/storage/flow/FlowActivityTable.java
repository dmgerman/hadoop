begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.flow
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
name|storage
operator|.
name|flow
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
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|common
operator|.
name|BaseTable
import|;
end_import

begin_comment
comment|/**  * The flow activity table has column family info  * Stores the daily activity record for flows  * Useful as a quick lookup of what flows were  * running on a given day  *  * Example flow activity table record:  *  *<pre>  * |-------------------------------------------|  * |  Row key   | Column Family                |  * |            | info                         |  * |-------------------------------------------|  * | clusterId! | r!runid1:version1            |  * | inv Top of |                              |  * | Day!       | r!runid2:version7            |  * | userName!  |                              |  * | flowName   |                              |  * |-------------------------------------------|  *</pre>  */
end_comment

begin_class
DECL|class|FlowActivityTable
specifier|public
specifier|final
class|class
name|FlowActivityTable
extends|extends
name|BaseTable
argument_list|<
name|FlowActivityTable
argument_list|>
block|{ }
end_class

end_unit

