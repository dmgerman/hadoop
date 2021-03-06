begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.application
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
name|application
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
comment|/**  * The application table as column families info, config and metrics. Info  * stores information about a YARN application entity, config stores  * configuration data of a YARN application, metrics stores the metrics of a  * YARN application. This table is entirely analogous to the entity table but  * created for better performance.  *  * Example application table record:  *  *<pre>  * |-------------------------------------------------------------------------|  * |  Row       | Column Family                | Column Family| Column Family|  * |  key       | info                         | metrics      | config       |  * |-------------------------------------------------------------------------|  * | clusterId! | id:appId                     | metricId1:   | configKey1:  |  * | userName!  |                              | metricValue1 | configValue1 |  * | flowName!  | created_time:                | @timestamp1  |              |  * | flowRunId! | 1392993084018                |              | configKey2:  |  * | AppId      |                              | metriciD1:   | configValue2 |  * |            | i!infoKey:                   | metricValue2 |              |  * |            | infoValue                    | @timestamp2  |              |  * |            |                              |              |              |  * |            | r!relatesToKey:              | metricId2:   |              |  * |            | id3=id4=id5                  | metricValue1 |              |  * |            |                              | @timestamp2  |              |  * |            | s!isRelatedToKey:            |              |              |  * |            | id7=id9=id6                  |              |              |  * |            |                              |              |              |  * |            | e!eventId=timestamp=infoKey: |              |              |  * |            | eventInfoValue               |              |              |  * |            |                              |              |              |  * |            | flowVersion:                 |              |              |  * |            | versionValue                 |              |              |  * |-------------------------------------------------------------------------|  *</pre>  */
end_comment

begin_class
DECL|class|ApplicationTable
specifier|public
specifier|final
class|class
name|ApplicationTable
extends|extends
name|BaseTable
argument_list|<
name|ApplicationTable
argument_list|>
block|{ }
end_class

end_unit

